package com.travery.traverybackend.services.tour.impl;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.tour.ImageResponse;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.entities.tour.TourItinerary;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.Tourist;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import com.travery.traverybackend.enums.common.ImageType;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.TourInstanceMapper;
import com.travery.traverybackend.mappers.TourMapper;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.repositories.common.ImageRepository;
import com.travery.traverybackend.repositories.finance.RefundPolicyRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.media.MediaService;
import com.travery.traverybackend.services.tour.TourService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourServiceImpl implements TourService {

  private final TourRepository tourRepository;
  private final TourInstanceRepository tourInstanceRepository;
  private final TourMapper tourMapper;
  private final TourInstanceMapper tourInstanceMapper;
  private final ImageRepository imageRepository;
  private final DestinationRepository destinationRepository;
  private final HotelRepository hotelRepository;
  private final RefundPolicyRepository refundPolicyRepository;
  private final UserRepository userRepository;
  private final MediaService mediaService;

  @Override
  @Transactional(readOnly = true)
  public Page<TourSummaryResponse> getTours(TourSearchRequest request, Pageable pageable) {
    Page<Tour> toursPage;

    if (hasSearchCriteria(request)) {
      toursPage = tourRepository.searchTours(request, pageable);
    } else {
      toursPage = tourRepository.findAllByIsCustomFalse(pageable);
    }

    List<UUID> tourIds = toursPage.getContent().stream().map(Tour::getId).toList();
    Map<UUID, String> thumbnails = getThumbnailsForTours(tourIds);

    return toursPage.map(
        tour -> {
          TourSummaryResponse response = tourMapper.toTourSummaryResponse(tour);
          response.setThumbnailUrl(thumbnails.get(tour.getId()));
          return response;
        });
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "featuredTours", key = "'top10'")
  public List<TourSummaryResponse> getFeaturedTours() {
    log.info("Fetching featured tours from Database");
    List<Tour> topTours =
        tourRepository
            .findTop10ByIsCustomFalseAndAverageRatingGreaterThanEqualOrderByAverageRatingDesc(4.5);
    List<UUID> tourIds = topTours.stream().map(Tour::getId).toList();
    Map<UUID, String> thumbnails = getThumbnailsForTours(tourIds);

    return topTours.stream()
        .map(
            tour -> {
              TourSummaryResponse response = tourMapper.toTourSummaryResponse(tour);
              response.setThumbnailUrl(thumbnails.get(tour.getId()));
              return response;
            })
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public TourDetailResponse getTourDetail(UUID id) {
    Tour tour =
        tourRepository
            .findByIdWithDetails(id)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour not found"));

    TourDetailResponse response = tourMapper.toTourDetailResponse(tour);
    enrichTourImages(response, tour);
    return response;
  }

  private void enrichTourImages(TourDetailResponse response, Tour tour) {
    List<Image> tourImages =
        imageRepository.findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(
            tour.getId(), ImageType.TOUR);
    response.setImages(
        tourImages.stream().map(tourMapper::toImageResponse).collect(Collectors.toList()));

    List<UUID> itineraryIds =
        tour.getItineraries().stream().map(TourItinerary::getId).collect(Collectors.toList());

    if (!itineraryIds.isEmpty()) {
      Map<UUID, List<ImageResponse>> itineraryImages =
          imageRepository
              .findByEntityIdInAndEntityTypeOrderByDisplayOrderAsc(
                  itineraryIds, ImageType.TOUR_ITINERARY)
              .stream()
              .collect(
                  Collectors.groupingBy(
                      Image::getEntityId,
                      Collectors.mapping(tourMapper::toImageResponse, Collectors.toList())));

      // Match images to itineraries by index (since MapStruct preserves order)
      for (int i = 0; i < tour.getItineraries().size(); i++) {
        UUID id = tour.getItineraries().get(i).getId();
        if (itineraryImages.containsKey(id)) {
          response.getItineraryList().get(i).setImages(itineraryImages.get(id));
        }
      }
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<TourInstanceResponse> getTourInstances(UUID tourId) {
    Tour tour =
        tourRepository
            .findById(tourId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour not found"));

    // Only return instances that are OPEN, and date is >= today
    List<TourInstanceStatus> statuses = Arrays.asList(TourInstanceStatus.OPEN);

    List<TourInstance> instances =
        tourInstanceRepository
            .findByTourIdAndStatusInAndStartDateGreaterThanEqualOrderByStartDateAsc(
                tourId, statuses, LocalDate.now());

    return instances.stream()
        .map(tourInstanceMapper::toTourInstanceResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public TourResponse createTemplate(
      TourTemplateRequest request,
      List<MultipartFile> tourImages,
      List<MultipartFile> itineraryImages,
      UUID coordinatorId) {
    List<String> uploadedPublicIds = new ArrayList<>();
    try {
      Coordinator coordinator =
          userRepository
              .findById(coordinatorId)
              .filter(user -> user instanceof Coordinator)
              .map(user -> (Coordinator) user)
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.FORBIDDEN, "User is not a coordinator"));

      Destination destination =
          destinationRepository
              .findById(request.getDestinationId())
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Destination not found"));

      Hotel hotel = null;
      if (request.getHotelId() != null) {
        hotel =
            hotelRepository
                .findById(request.getHotelId())
                .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));
      }

      RefundPolicy refundPolicy =
          refundPolicyRepository
              .findByNameAndServiceType("Standard Tour Policy", RefundServiceType.TOUR)
              .orElseThrow(
                  () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund policy not found"));

      User requestedByUser = null;
      if (request.getRequestedByUserId() != null) {
        requestedByUser =
            userRepository
                .findById(request.getRequestedByUserId())
                .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

        if (!(requestedByUser instanceof Tourist)) {
          throw new BaseAppException(
              WebErrorCode.BAD_REQUEST, "Only Tourists can request custom tours");
        }
      }

      Tour tour = tourMapper.toEntity(request);
      tour.setCoordinator(coordinator);
      tour.setDestination(destination);
      tour.setHotel(hotel);
      tour.setRefundPolicy(refundPolicy);
      tour.setRequestedByUser(requestedByUser);

      List<TourItinerary> itineraries =
          request.getItineraries().stream()
              .map(
                  itineraryRequest ->
                      TourItinerary.builder()
                          .tour(tour)
                          .dayNumber(itineraryRequest.getDayNumber())
                          .title(itineraryRequest.getTitle())
                          .description(itineraryRequest.getDescription())
                          .build())
              .collect(Collectors.toList());

      tour.setItineraries(itineraries);

      Tour savedTour = tourRepository.save(tour);

      // Save Tour Images
      if (tourImages != null && !tourImages.isEmpty()) {
        int order = 0;
        for (MultipartFile file : tourImages) {
          if (file.isEmpty()) continue;
          Map<String, Object> uploadResult = mediaService.uploadImage(file, CloudinaryFolder.TOURS);
          String publicId = (String) uploadResult.get("public_id");
          uploadedPublicIds.add(publicId);

          Image image =
              Image.builder()
                  .entityId(savedTour.getId())
                  .entityType(ImageType.TOUR)
                  .url((String) uploadResult.get("secure_url"))
                  .publicId(publicId)
                  .isThumbnail(order == 0)
                  .displayOrder(order++)
                  .build();
          imageRepository.save(image);
        }
      }

      // Save Itinerary Images
      if (request.getItineraries() != null && itineraryImages != null) {
        for (int i = 0;
            i < Math.min(request.getItineraries().size(), itineraryImages.size());
            i++) {
          MultipartFile file = itineraryImages.get(i);
          if (file != null && !file.isEmpty()) {
            Map<String, Object> uploadResult =
                mediaService.uploadImage(file, CloudinaryFolder.ITINERARIES);
            String publicId = (String) uploadResult.get("public_id");
            uploadedPublicIds.add(publicId);

            TourItinerary savedItinerary = savedTour.getItineraries().get(i);
            Image image =
                Image.builder()
                    .entityId(savedItinerary.getId())
                    .entityType(ImageType.TOUR_ITINERARY)
                    .url((String) uploadResult.get("secure_url"))
                    .publicId(publicId)
                    .isThumbnail(true)
                    .displayOrder(0)
                    .build();
            imageRepository.save(image);
          }
        }
      }

      return tourMapper.toTourResponse(savedTour);

    } catch (Exception e) {
      // Rollback Cloudinary uploads if anything fails
      for (String publicId : uploadedPublicIds) {
        mediaService.deleteImage(publicId);
      }
      throw e;
    }
  }

  private Map<UUID, String> getThumbnailsForTours(List<UUID> tourIds) {
    if (tourIds.isEmpty()) return Map.of();
    return imageRepository
        .findByEntityIdInAndEntityTypeAndIsThumbnailTrue(tourIds, ImageType.TOUR)
        .stream()
        .collect(
            Collectors.toMap(
                Image::getEntityId, Image::getUrl, (existing, replacement) -> existing));
  }

  private boolean hasSearchCriteria(TourSearchRequest request) {
    if (request == null) return false;
    return (request.getKeyword() != null && !request.getKeyword().isBlank())
        || request.getMinPrice() != null
        || request.getMaxPrice() != null
        || request.getDestinationId() != null
        || request.getMinRating() != null
        || request.getStartDate() != null;
  }
}

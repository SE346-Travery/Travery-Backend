package com.travery.traverybackend.services.hotel;

import com.travery.traverybackend.dtos.request.hotel.HotelSearchRequest;
import com.travery.traverybackend.dtos.response.booking.ReviewResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelDetailResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelResponse;
import com.travery.traverybackend.dtos.response.hotel.RoomTypeResponse;
import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.entities.common.Review;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.hotel.RoomType;
import com.travery.traverybackend.enums.common.ImageType;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelMapper;
import com.travery.traverybackend.repositories.common.ImageRepository;
import com.travery.traverybackend.repositories.common.ReviewRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.hotel.RoomTypeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

  private final HotelRepository hotelRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final ImageRepository imageRepository;
  private final ReviewRepository reviewRepository;
  private final HotelMapper hotelMapper;

  @Override
  public Page<HotelResponse> searchHotels(HotelSearchRequest request, Pageable pageable) {
    Page<Hotel> hotels = hotelRepository.searchHotels(request, pageable);

    List<UUID> hotelIds = hotels.getContent().stream().map(Hotel::getId).toList();

    // Batch fetch thumbnails
    Map<UUID, String> thumbnails =
        imageRepository
            .findByEntityIdInAndEntityTypeAndIsThumbnailTrue(hotelIds, ImageType.HOTEL)
            .stream()
            .collect(Collectors.toMap(Image::getEntityId, Image::getUrl, (a, b) -> a));

    // Batch fetch min prices
    Map<UUID, BigDecimal> minPrices =
        roomTypeRepository.findAll().stream()
            .filter(rt -> hotelIds.contains(rt.getHotel().getId()))
            .collect(
                Collectors.groupingBy(
                    rt -> rt.getHotel().getId(),
                    Collectors.mapping(
                        RoomType::getBasePrice,
                        Collectors.reducing(
                            BigDecimal.valueOf(Double.MAX_VALUE), BigDecimal::min))));

    return hotels.map(
        hotel -> {
          HotelResponse response = hotelMapper.toHotelResponse(hotel);
          response.setThumbnailUrl(thumbnails.get(hotel.getId()));
          response.setMinPrice(minPrices.getOrDefault(hotel.getId(), BigDecimal.ZERO));
          return response;
        });
  }

  @Override
  public HotelDetailResponse getHotelDetail(UUID hotelId) {
    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    HotelDetailResponse response = hotelMapper.toHotelDetailResponse(hotel);

    // Fetch hotel images
    List<String> hotelImages =
        imageRepository
            .findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(hotelId, ImageType.HOTEL)
            .stream()
            .map(Image::getUrl)
            .toList();
    response.setImages(hotelImages);

    // Fetch and map RoomTypes with their images
    List<RoomType> roomTypes = roomTypeRepository.findAllByHotel_Id(hotelId);
    List<UUID> roomTypeIds = roomTypes.stream().map(RoomType::getId).toList();

    Map<UUID, List<String>> roomTypeImages =
        imageRepository
            .findByEntityIdInAndEntityTypeOrderByDisplayOrderAsc(roomTypeIds, ImageType.ROOM_TYPE)
            .stream()
            .collect(
                Collectors.groupingBy(
                    Image::getEntityId, Collectors.mapping(Image::getUrl, Collectors.toList())));

    List<RoomTypeResponse> roomTypeResponses =
        roomTypes.stream()
            .map(
                rt -> {
                  RoomTypeResponse rtResponse = hotelMapper.toRoomTypeResponse(rt);
                  rtResponse.setImages(roomTypeImages.getOrDefault(rt.getId(), List.of()));
                  return rtResponse;
                })
            .toList();

    response.setRoomTypes(roomTypeResponses);

    // Fetch reviews
    List<Review> reviews =
        reviewRepository
            .findByTargetIdAndTargetType(hotelId, ReviewTargetType.HOTEL, PageRequest.of(0, 20))
            .getContent();

    List<ReviewResponse> reviewResponses =
        reviews.stream()
            .map(
                r ->
                    ReviewResponse.builder()
                        .id(r.getId())
                        .rating(r.getAverageRating())
                        .content(r.getContent())
                        .reviewerName(r.getUser().getFullName())
                        .createdAt(r.getCreatedAt())
                        .build())
            .toList();
    response.setReviews(reviewResponses);

    return response;
  }
}

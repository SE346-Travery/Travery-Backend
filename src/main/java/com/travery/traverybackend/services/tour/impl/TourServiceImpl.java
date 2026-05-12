package com.travery.traverybackend.services.tour.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.TourMapper;
import com.travery.traverybackend.repositories.ImageRepository;
import com.travery.traverybackend.repositories.TourInstanceRepository;
import com.travery.traverybackend.repositories.TourRepository;
import com.travery.traverybackend.enums.common.ImageType;
import java.util.Map;
import com.travery.traverybackend.services.tour.TourService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourInstanceRepository tourInstanceRepository;
    private final TourMapper tourMapper;
    private final ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TourSummaryResponse> getTours(TourSearchRequest request, Pageable pageable) {
        Page<Tour> toursPage;
        
        // Use Hibernate Search if any search criteria is provided, otherwise use standard JPA
        if (hasSearchCriteria(request)) {
            toursPage = tourRepository.searchTours(request, pageable);
        } else {
            toursPage = tourRepository.findAllByIsCustomFalse(pageable);
        }
        
        List<UUID> tourIds = toursPage.getContent().stream().map(Tour::getId).toList();
        Map<UUID, String> thumbnails = getThumbnailsForTours(tourIds);
        
        return toursPage.map(tour -> {
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
        List<Tour> topTours = tourRepository.findTop10ByIsCustomFalseAndAverageRatingGreaterThanEqualOrderByAverageRatingDesc(4.5);
        List<UUID> tourIds = topTours.stream().map(Tour::getId).toList();
        Map<UUID, String> thumbnails = getThumbnailsForTours(tourIds);
        
        return topTours.stream()
                .map(tour -> {
                    TourSummaryResponse response = tourMapper.toTourSummaryResponse(tour);
                    response.setThumbnailUrl(thumbnails.get(tour.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailResponse getTourDetail(UUID id) {
        Tour tour = tourRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour not found"));
                
        return tourMapper.toTourDetailResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourInstanceResponse> getTourInstances(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Tour not found"));
        
        
        // Only return instances that are OPEN or PLANNING, and date is >= today
        List<TourInstanceStatus> statuses = Arrays.asList(TourInstanceStatus.OPEN, TourInstanceStatus.PLANNING);
        
        List<TourInstance> instances = tourInstanceRepository
                .findByTourIdAndStatusInAndStartDateGreaterThanEqualOrderByStartDateAsc(
                        tourId, statuses, LocalDate.now());
                        
        // Using TourMapper
        return instances.stream()
                .map(instance -> {
                    TourInstanceResponse response = tourMapper.toTourInstanceResponse(instance);
                    response.setAvailableSlots(Math.max(0, tour.getMaxParticipants() - instance.getCurrentParticipants()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    private Map<UUID, String> getThumbnailsForTours(List<UUID> tourIds) {
        if (tourIds.isEmpty()) return Map.of();
        return imageRepository.findByEntityIdInAndEntityTypeAndIsThumbnailTrue(tourIds, ImageType.TOUR)
                .stream()
                .collect(Collectors.toMap(com.travery.traverybackend.entities.common.Image::getEntityId, com.travery.traverybackend.entities.common.Image::getUrl));
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

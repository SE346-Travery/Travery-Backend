package com.travery.traverybackend.schedulers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.mappers.TourMapper;
import com.travery.traverybackend.repositories.tour.TourRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TourCacheScheduler {

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final CacheManager cacheManager;

    // Chạy mỗi giờ một lần (Cronjob)
    @Scheduled(cron = "0 0 * * * *")
    public void refreshFeaturedToursCache() {
        log.info("Cronjob: Refreshing featured tours cache in Redis");
        List<Tour> topTours = tourRepository.findTop10ByIsCustomFalseAndAverageRatingGreaterThanEqualOrderByAverageRatingDesc(4.5);
        List<TourSummaryResponse> result = topTours.stream()
                .map(tourMapper::toTourSummaryResponse)
                .collect(Collectors.toList());
                
        Cache cache = cacheManager.getCache("featuredTours");
        if (cache != null) {
            cache.put("top10", result);
        }
    }
}

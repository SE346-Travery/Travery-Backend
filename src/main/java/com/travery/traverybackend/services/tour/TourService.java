package com.travery.traverybackend.services.tour;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;

public interface TourService {

    Page<TourSummaryResponse> getTours(TourSearchRequest request, Pageable pageable);

    List<TourSummaryResponse> getFeaturedTours();

    TourDetailResponse getTourDetail(UUID id);

    List<TourInstanceResponse> getTourInstances(UUID tourId);
}

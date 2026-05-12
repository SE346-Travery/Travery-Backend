package com.travery.traverybackend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import com.travery.traverybackend.services.tour.TourService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
    private final ResponseFactory responseFactory;

    @GetMapping
    public ResponseEntity<SingleResponse<Page<TourSummaryResponse>>> getTours(
            @Valid TourSearchRequest request,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<TourSummaryResponse> tours = tourService.getTours(request, pageable);
        return responseFactory.success(tours, "Fetched tours successfully");
    }

    @GetMapping("/featured")
    public ResponseEntity<SingleResponse<List<TourSummaryResponse>>> getFeaturedTours() {
        List<TourSummaryResponse> featuredTours = tourService.getFeaturedTours();
        return responseFactory.success(featuredTours, "Fetched featured tours successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResponse<TourDetailResponse>> getTourDetail(@PathVariable UUID id) {
        TourDetailResponse detail = tourService.getTourDetail(id);
        return responseFactory.success(detail, "Fetched tour detail successfully");
    }

    @GetMapping("/{id}/instances")
    public ResponseEntity<SingleResponse<List<TourInstanceResponse>>> getTourInstances(@PathVariable UUID id) {
        List<TourInstanceResponse> instances = tourService.getTourInstances(id);
        return responseFactory.success(instances, "Fetched tour instances successfully");
    }
}

package com.travery.traverybackend.controllers.common;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.common.ReviewResponse;
import com.travery.traverybackend.enums.common.ReviewTargetType;
import com.travery.traverybackend.services.common.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;

import com.travery.traverybackend.dtos.request.booking.CreateReviewRequest;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.security.user.CustomUserDetails;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController extends AbstractBaseController {

    private final ReviewService reviewService;

    @GetMapping("/hotels/{hotelId}/reviews")
    public ResponseEntity<SingleResponse<Page<ReviewResponse>>> getHotelReviews(
            @PathVariable UUID hotelId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(hotelId, ReviewTargetType.HOTEL, pageable);
        return success(reviews, "Hotel reviews fetched successfully");
    }

    @GetMapping("/tours/{tourId}/reviews")
    public ResponseEntity<SingleResponse<Page<ReviewResponse>>> getTourReviews(
            @PathVariable UUID tourId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(tourId, ReviewTargetType.TOUR, pageable);
        return success(reviews, "Tour reviews fetched successfully");
    }

    @GetMapping("/routes/{routeId}/reviews")
    public ResponseEntity<SingleResponse<Page<ReviewResponse>>> getRouteReviews(
            @PathVariable UUID routeId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(routeId, ReviewTargetType.ROUTE, pageable);
        return success(reviews, "Route reviews fetched successfully");
    }

    @PostMapping("/hotel-bookings/{bookingId}/reviews")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<SingleResponse<ReviewResponse>> createHotelReview(
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ReviewResponse response = reviewService.createReview(
                bookingId, request, currentUser.getUserId(), BookingType.HOTEL_BOOKING);
        return created(response, "Hotel review submitted successfully");
    }

    @PostMapping("/tour-bookings/{bookingId}/reviews")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<SingleResponse<ReviewResponse>> createTourReview(
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ReviewResponse response = reviewService.createReview(
                bookingId, request, currentUser.getUserId(), BookingType.TOUR_BOOKING);
        return created(response, "Tour review submitted successfully");
    }

    @PostMapping("/coach-bookings/{bookingId}/reviews")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<SingleResponse<ReviewResponse>> createCoachReview(
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ReviewResponse response = reviewService.createReview(
                bookingId, request, currentUser.getUserId(), BookingType.COACH_BOOKING);
        return created(response, "Coach review submitted successfully");
    }
}

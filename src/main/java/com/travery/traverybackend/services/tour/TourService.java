package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.tour.TourDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.dtos.response.tour.TourSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TourService {

  Page<TourSummaryResponse> getTours(TourSearchRequest request, Pageable pageable);

  List<TourSummaryResponse> getFeaturedTours();

  TourDetailResponse getTourDetail(UUID id);

  List<TourInstanceResponse> getTourInstances(UUID tourId);

  TourResponse createTemplate(
      TourTemplateRequest request,
      List<MultipartFile> tourImages,
      List<MultipartFile> itineraryImages,
      UUID coordinatorId);
}

package com.travery.traverybackend.repositories.tour;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.entities.tour.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TourSearchCustomRepository {
  Page<Tour> searchTours(TourSearchRequest request, Pageable pageable);
}

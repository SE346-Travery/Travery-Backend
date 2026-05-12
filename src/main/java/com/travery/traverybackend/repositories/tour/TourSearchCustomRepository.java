package com.travery.traverybackend.repositories.tour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.entities.tour.Tour;

public interface TourSearchCustomRepository {
    Page<Tour> searchTours(TourSearchRequest request, Pageable pageable);
}

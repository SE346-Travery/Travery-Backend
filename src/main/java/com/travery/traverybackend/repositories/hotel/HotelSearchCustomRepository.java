package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.dtos.request.hotel.HotelSearchRequest;
import com.travery.traverybackend.entities.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelSearchCustomRepository {
  Page<Hotel> searchHotels(HotelSearchRequest request, Pageable pageable);
}

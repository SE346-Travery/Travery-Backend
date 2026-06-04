package com.travery.traverybackend.services.hotel;

import com.travery.traverybackend.dtos.request.hotel.HotelSearchRequest;
import com.travery.traverybackend.dtos.response.hotel.HotelDetailResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelService {
  Page<HotelResponse> searchHotels(HotelSearchRequest request, Pageable pageable);

  HotelDetailResponse getHotelDetail(UUID hotelId);
}

package com.travery.traverybackend.controllers.hotel;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.hotel.HotelSearchRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelDetailResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelResponse;
import com.travery.traverybackend.services.hotel.HotelService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController extends AbstractBaseController {

  private final HotelService hotelService;

  @GetMapping
  public ResponseEntity<SingleResponse<Page<HotelResponse>>> searchHotels(
      @Valid HotelSearchRequest request, @PageableDefault(size = 10) Pageable pageable) {
    Page<HotelResponse> hotels = hotelService.searchHotels(request, pageable);
    return success(hotels, "Fetched hotels successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<HotelDetailResponse>> getHotelDetail(@PathVariable UUID id) {
    HotelDetailResponse detail = hotelService.getHotelDetail(id);
    return success(detail, "Fetched hotel detail successfully");
  }
}

package com.travery.traverybackend.services.admin;

import com.travery.traverybackend.dtos.request.admin.*;
import com.travery.traverybackend.dtos.response.hotel.*;
import com.travery.traverybackend.dtos.response.staff.ReceptionistRoomResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminInventoryService {
  // Hotel
  HotelDetailResponse createHotel(CreateHotelRequest request);

  Page<HotelResponse> getAllHotels(Pageable pageable);

  HotelDetailResponse updateHotel(UUID hotelId, CreateHotelRequest request);

  // Room Type
  RoomTypeResponse createRoomType(UUID hotelId, CreateRoomTypeRequest request);

  List<RoomTypeResponse> getRoomTypes(UUID hotelId);

  RoomTypeResponse updateRoomType(UUID roomTypeId, CreateRoomTypeRequest request);

  void deleteRoomType(UUID roomTypeId);

  // Room
  ReceptionistRoomResponse createRoom(UUID hotelId, CreateRoomRequest request);

  List<ReceptionistRoomResponse> getRooms(UUID hotelId);

  ReceptionistRoomResponse updateRoom(UUID roomId, CreateRoomRequest request);

  void deleteRoom(UUID roomId);

  // Hotel Service
  HotelServiceResponse createService(UUID hotelId, CreateHotelServiceRequest request);

  List<HotelServiceResponse> getServices(UUID hotelId);

  HotelServiceResponse updateService(UUID serviceId, CreateHotelServiceRequest request);

  void deleteService(UUID serviceId);
}

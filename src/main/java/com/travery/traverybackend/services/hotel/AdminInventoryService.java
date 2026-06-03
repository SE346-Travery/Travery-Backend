package com.travery.traverybackend.services.hotel;

import com.travery.traverybackend.dtos.request.hotel.CreateHotelRequest;
import com.travery.traverybackend.dtos.request.hotel.CreateHotelServiceRequest;
import com.travery.traverybackend.dtos.request.hotel.CreateRoomRequest;
import com.travery.traverybackend.dtos.request.hotel.CreateRoomTypeRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateHotelRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateHotelServiceRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateRoomRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateRoomTypeRequest;
import com.travery.traverybackend.dtos.response.hotel.*;
import com.travery.traverybackend.dtos.response.staff.ReceptionistRoomResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AdminInventoryService {
  // Hotel
  HotelBasicResponse createHotel(CreateHotelRequest request);

  Page<HotelResponse> getAllHotels(Pageable pageable);

  HotelBasicResponse updateHotel(UUID hotelId, UpdateHotelRequest request);

  // Room Type
  RoomTypeResponse createRoomType(UUID hotelId, CreateRoomTypeRequest request);

  List<RoomTypeResponse> getRoomTypes(UUID hotelId);

  RoomTypeResponse updateRoomType(UUID roomTypeId, UpdateRoomTypeRequest request);

  void deleteRoomType(UUID roomTypeId);

  // Room
  ReceptionistRoomResponse createRoom(UUID hotelId, CreateRoomRequest request);

  List<ReceptionistRoomResponse> getRooms(UUID hotelId);

  ReceptionistRoomResponse updateRoom(UUID roomId, UpdateRoomRequest request);

  void deleteRoom(UUID roomId);

  // Hotel Service
  HotelServiceResponse createService(UUID hotelId, CreateHotelServiceRequest request);

  List<HotelServiceResponse> getServices(UUID hotelId);

  HotelServiceResponse updateService(UUID serviceId, UpdateHotelServiceRequest request);

  void deleteService(UUID serviceId);

  // Images
  List<HotelImageResponse> uploadHotelImages(UUID hotelId, List<MultipartFile> files);

  void deleteHotelImage(UUID hotelId, UUID imageId);

  void setHotelThumbnail(UUID hotelId, UUID imageId);

  List<HotelImageResponse> uploadRoomTypeImages(UUID roomTypeId, List<MultipartFile> files);

  void deleteRoomTypeImage(UUID roomTypeId, UUID imageId);
}

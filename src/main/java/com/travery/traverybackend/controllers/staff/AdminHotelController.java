package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.hotel.CreateHotelRequest;
import com.travery.traverybackend.dtos.request.hotel.CreateHotelServiceRequest;
import com.travery.traverybackend.dtos.request.hotel.CreateRoomRequest;
import com.travery.traverybackend.dtos.request.hotel.CreateRoomTypeRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateHotelRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateHotelServiceRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateRoomRequest;
import com.travery.traverybackend.dtos.request.hotel.UpdateRoomTypeRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.hotel.*;
import com.travery.traverybackend.dtos.response.staff.ReceptionistRoomResponse;
import com.travery.traverybackend.services.hotel.AdminInventoryService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminHotelController extends AbstractBaseController {

  private final AdminInventoryService adminInventoryService;

  // --- Hotel ---
  @PostMapping("/hotels")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<HotelBasicResponse>> createHotel(
      @Valid @RequestBody CreateHotelRequest request) {
    HotelBasicResponse response = adminInventoryService.createHotel(request);
    return created(response, "Hotel created successfully");
  }

  @GetMapping("/hotels")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<Page<HotelResponse>>> getAllHotels(
      @PageableDefault(size = 10) Pageable pageable) {
    Page<HotelResponse> response = adminInventoryService.getAllHotels(pageable);
    return success(response, "Hotels retrieved successfully");
  }

  @PatchMapping("/hotels/{hotelId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<HotelBasicResponse>> updateHotel(
      @PathVariable UUID hotelId, @Valid @RequestBody UpdateHotelRequest request) {
    HotelBasicResponse response = adminInventoryService.updateHotel(hotelId, request);
    return success(response, "Hotel updated successfully");
  }

  // --- Room Type ---
  @PostMapping("/hotels/{hotelId}/room-types")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<RoomTypeResponse>> createRoomType(
      @PathVariable UUID hotelId, @Valid @RequestBody CreateRoomTypeRequest request) {
    RoomTypeResponse response = adminInventoryService.createRoomType(hotelId, request);
    return created(response, "Room type created successfully");
  }

  @GetMapping("/hotels/{hotelId}/room-types")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<RoomTypeResponse>>> getRoomTypes(
      @PathVariable UUID hotelId) {
    List<RoomTypeResponse> response = adminInventoryService.getRoomTypes(hotelId);
    return success(response, "Room types retrieved successfully");
  }

  @PatchMapping("/room-types/{roomTypeId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<RoomTypeResponse>> updateRoomType(
      @PathVariable UUID roomTypeId, @Valid @RequestBody UpdateRoomTypeRequest request) {
    RoomTypeResponse response = adminInventoryService.updateRoomType(roomTypeId, request);
    return success(response, "Room type updated successfully");
  }

  @DeleteMapping("/room-types/{roomTypeId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteRoomType(@PathVariable UUID roomTypeId) {
    adminInventoryService.deleteRoomType(roomTypeId);
    return success("Room type deleted successfully");
  }

  // --- Room ---
  @PostMapping("/hotels/{hotelId}/rooms")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<ReceptionistRoomResponse>> createRoom(
      @PathVariable UUID hotelId, @Valid @RequestBody CreateRoomRequest request) {
    ReceptionistRoomResponse response = adminInventoryService.createRoom(hotelId, request);
    return created(response, "Room created successfully");
  }

  @GetMapping("/hotels/{hotelId}/rooms")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<ReceptionistRoomResponse>>> getRooms(
      @PathVariable UUID hotelId) {
    List<ReceptionistRoomResponse> response = adminInventoryService.getRooms(hotelId);
    return success(response, "Rooms retrieved successfully");
  }

  @PatchMapping("/rooms/{roomId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<ReceptionistRoomResponse>> updateRoom(
      @PathVariable UUID roomId, @Valid @RequestBody UpdateRoomRequest request) {
    ReceptionistRoomResponse response = adminInventoryService.updateRoom(roomId, request);
    return success(response, "Room updated successfully");
  }

  @DeleteMapping("/rooms/{roomId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteRoom(@PathVariable UUID roomId) {
    adminInventoryService.deleteRoom(roomId);
    return success("Room deleted successfully");
  }

  // --- Hotel Service ---
  @PostMapping("/hotels/{hotelId}/services")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<HotelServiceResponse>> createService(
      @PathVariable UUID hotelId, @Valid @RequestBody CreateHotelServiceRequest request) {
    HotelServiceResponse response = adminInventoryService.createService(hotelId, request);
    return created(response, "Service created successfully");
  }

  @GetMapping("/hotels/{hotelId}/services")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<HotelServiceResponse>>> getServices(
      @PathVariable UUID hotelId) {
    List<HotelServiceResponse> response = adminInventoryService.getServices(hotelId);
    return success(response, "Services retrieved successfully");
  }

  @PatchMapping("/services/{serviceId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<HotelServiceResponse>> updateService(
      @PathVariable UUID serviceId, @Valid @RequestBody UpdateHotelServiceRequest request) {
    HotelServiceResponse response = adminInventoryService.updateService(serviceId, request);
    return success(response, "Service updated successfully");
  }

  @DeleteMapping("/services/{serviceId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteService(@PathVariable UUID serviceId) {
    adminInventoryService.deleteService(serviceId);
    return success("Service deleted successfully");
  }

  // --- Hotel Images ---
  @PostMapping("/hotels/{hotelId}/images")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<HotelImageResponse>>> uploadHotelImages(
      @PathVariable UUID hotelId,
      @RequestParam("files") List<MultipartFile> files) {
    List<HotelImageResponse> response = adminInventoryService.uploadHotelImages(hotelId, files);
    return created(response, "Hotel images uploaded successfully");
  }

  @DeleteMapping("/hotels/{hotelId}/images/{imageId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteHotelImage(
      @PathVariable UUID hotelId, @PathVariable UUID imageId) {
    adminInventoryService.deleteHotelImage(hotelId, imageId);
    return success("Hotel image deleted successfully");
  }

  @PutMapping("/hotels/{hotelId}/images/{imageId}/thumbnail")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> setHotelThumbnail(
      @PathVariable UUID hotelId, @PathVariable UUID imageId) {
    adminInventoryService.setHotelThumbnail(hotelId, imageId);
    return success("Hotel thumbnail set successfully");
  }

  // --- Room Type Images ---
  @PostMapping("/room-types/{roomTypeId}/images")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SingleResponse<List<HotelImageResponse>>> uploadRoomTypeImages(
      @PathVariable UUID roomTypeId,
      @RequestParam("files") List<MultipartFile> files) {
    List<HotelImageResponse> response = adminInventoryService.uploadRoomTypeImages(roomTypeId, files);
    return created(response, "Room type images uploaded successfully");
  }

  @DeleteMapping("/room-types/{roomTypeId}/images/{imageId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse> deleteRoomTypeImage(
      @PathVariable UUID roomTypeId, @PathVariable UUID imageId) {
    adminInventoryService.deleteRoomTypeImage(roomTypeId, imageId);
    return success("Room type image deleted successfully");
  }
}

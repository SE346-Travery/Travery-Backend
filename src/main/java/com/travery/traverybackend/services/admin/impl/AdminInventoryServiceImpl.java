package com.travery.traverybackend.services.admin.impl;

import com.travery.traverybackend.dtos.request.admin.*;
import com.travery.traverybackend.dtos.response.hotel.*;
import com.travery.traverybackend.dtos.response.staff.ReceptionistRoomResponse;
import com.travery.traverybackend.entities.hotel.*;
import com.travery.traverybackend.enums.hotel.BedType;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import com.travery.traverybackend.enums.hotel.ServiceCategory;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelMapper;
import com.travery.traverybackend.mappers.ReceptionistMapper;
import com.travery.traverybackend.repositories.hotel.*;
import com.travery.traverybackend.services.admin.AdminInventoryService;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminInventoryServiceImpl implements AdminInventoryService {

  private final HotelRepository hotelRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final RoomRepository roomRepository;
  private final HotelServiceRepository hotelServiceRepository;
  private final AmenityRepository amenityRepository;
  private final HotelMapper hotelMapper;
  private final com.travery.traverybackend.services.hotel.HotelService touristHotelService;
  private final ReceptionistMapper receptionistMapper;

  @Override
  public HotelDetailResponse createHotel(CreateHotelRequest request) {
    Hotel hotel =
        Hotel.builder()
            .name(request.getName())
            .description(request.getDescription())
            .address(request.getAddress())
            .cityProvince(request.getCityProvince())
            .checkInTime(request.getCheckInTime())
            .checkOutTime(request.getCheckOutTime())
            .build();

    if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
      List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
      hotel.setAmenities(new HashSet<>(amenities));
    }

    hotel = hotelRepository.save(hotel);
    return touristHotelService.getHotelDetail(hotel.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<HotelResponse> getAllHotels(Pageable pageable) {
    return hotelRepository.findAll(pageable).map(hotelMapper::toHotelResponse);
  }

  @Override
  public HotelDetailResponse updateHotel(UUID hotelId, CreateHotelRequest request) {
    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    hotel.setName(request.getName());
    hotel.setDescription(request.getDescription());
    hotel.setAddress(request.getAddress());
    hotel.setCityProvince(request.getCityProvince());
    hotel.setCheckInTime(request.getCheckInTime());
    hotel.setCheckOutTime(request.getCheckOutTime());

    if (request.getAmenityIds() != null) {
      List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
      hotel.setAmenities(new HashSet<>(amenities));
    }

    hotelRepository.save(hotel);
    return touristHotelService.getHotelDetail(hotelId);
  }

  @Override
  public RoomTypeResponse createRoomType(UUID hotelId, CreateRoomTypeRequest request) {
    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    RoomType roomType =
        RoomType.builder()
            .hotel(hotel)
            .name(request.getName())
            .description(request.getDescription())
            .capacityAdults(request.getMaxAdults())
            .capacityChildren(request.getMaxChildren())
            .basePrice(request.getBasePrice())
            .bedType(BedType.valueOf(request.getBedType().toUpperCase()))
            .area(request.getArea())
            .quantity(request.getQuantity())
            .build();

    roomType = roomTypeRepository.save(roomType);
    return hotelMapper.toRoomTypeResponse(roomType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoomTypeResponse> getRoomTypes(UUID hotelId) {
    return roomTypeRepository.findAllByHotel_Id(hotelId).stream()
        .map(hotelMapper::toRoomTypeResponse)
        .toList();
  }

  @Override
  public RoomTypeResponse updateRoomType(UUID roomTypeId, CreateRoomTypeRequest request) {
    RoomType roomType =
        roomTypeRepository
            .findById(roomTypeId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

    roomType.setName(request.getName());
    roomType.setDescription(request.getDescription());
    roomType.setCapacityAdults(request.getMaxAdults());
    roomType.setCapacityChildren(request.getMaxChildren());
    roomType.setBasePrice(request.getBasePrice());
    roomType.setBedType(BedType.valueOf(request.getBedType().toUpperCase()));
    roomType.setArea(request.getArea());
    roomType.setQuantity(request.getQuantity());

    roomType = roomTypeRepository.save(roomType);
    return hotelMapper.toRoomTypeResponse(roomType);
  }

  @Override
  public void deleteRoomType(UUID roomTypeId) {
    if (!roomTypeRepository.existsById(roomTypeId)) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found");
    }
    roomTypeRepository.deleteById(roomTypeId);
  }

  @Override
  public ReceptionistRoomResponse createRoom(UUID hotelId, CreateRoomRequest request) {
    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));
    RoomType roomType =
        roomTypeRepository
            .findById(request.getRoomTypeId())
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

    Room room =
        Room.builder()
            .hotel(hotel)
            .roomType(roomType)
            .roomNumber(request.getRoomNumber())
            .floor(request.getFloor())
            .status(RoomStatus.AVAILABLE)
            .build();

    room = roomRepository.save(room);
    return receptionistMapper.toReceptionistRoomResponse(room);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReceptionistRoomResponse> getRooms(UUID hotelId) {
    return roomRepository.findAllByHotel_Id(hotelId).stream()
        .map(receptionistMapper::toReceptionistRoomResponse)
        .toList();
  }

  @Override
  public ReceptionistRoomResponse updateRoom(UUID roomId, CreateRoomRequest request) {
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room not found"));
    RoomType roomType =
        roomTypeRepository
            .findById(request.getRoomTypeId())
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

    room.setRoomNumber(request.getRoomNumber());
    room.setFloor(request.getFloor());
    room.setRoomType(roomType);

    room = roomRepository.save(room);
    return receptionistMapper.toReceptionistRoomResponse(room);
  }

  @Override
  public void deleteRoom(UUID roomId) {
    roomRepository.deleteById(roomId);
  }

  @Override
  public HotelServiceResponse createService(UUID hotelId, CreateHotelServiceRequest request) {
    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    com.travery.traverybackend.entities.hotel.HotelService service =
        com.travery.traverybackend.entities.hotel.HotelService.builder()
            .hotel(hotel)
            .name(request.getName())
            .category(ServiceCategory.valueOf(request.getCategory().toUpperCase()))
            .price(request.getPrice())
            .unit(request.getUnit())
            .description(request.getDescription())
            .build();

    service = hotelServiceRepository.save(service);
    return receptionistMapper.toHotelServiceResponse(service);
  }

  @Override
  @Transactional(readOnly = true)
  public List<HotelServiceResponse> getServices(UUID hotelId) {
    return hotelServiceRepository.findAllByHotel_Id(hotelId).stream()
        .map(receptionistMapper::toHotelServiceResponse)
        .toList();
  }

  @Override
  public HotelServiceResponse updateService(UUID serviceId, CreateHotelServiceRequest request) {
    com.travery.traverybackend.entities.hotel.HotelService service =
        hotelServiceRepository
            .findById(serviceId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Service not found"));

    service.setName(request.getName());
    service.setCategory(ServiceCategory.valueOf(request.getCategory().toUpperCase()));
    service.setPrice(request.getPrice());
    service.setUnit(request.getUnit());
    service.setDescription(request.getDescription());

    service = hotelServiceRepository.save(service);
    return receptionistMapper.toHotelServiceResponse(service);
  }

  @Override
  public void deleteService(UUID serviceId) {
    hotelServiceRepository.deleteById(serviceId);
  }
}

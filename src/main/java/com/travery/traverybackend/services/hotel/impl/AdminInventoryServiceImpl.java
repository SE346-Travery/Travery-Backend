package com.travery.traverybackend.services.hotel.impl;

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
import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.entities.hotel.*;
import com.travery.traverybackend.enums.common.ImageType;
import com.travery.traverybackend.enums.hotel.RoomStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelMapper;
import com.travery.traverybackend.mappers.ReceptionistMapper;
import com.travery.traverybackend.repositories.common.ImageRepository;
import com.travery.traverybackend.repositories.hotel.*;
import com.travery.traverybackend.services.hotel.AdminInventoryService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import com.travery.traverybackend.services.media.MediaService;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final ImageRepository imageRepository;
  private final HotelMapper hotelMapper;
  private final ReceptionistMapper receptionistMapper;
  private final MediaService mediaService;

  @Override
  public HotelBasicResponse createHotel(CreateHotelRequest request) {
    Hotel hotel = Hotel.builder()
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

    return hotelMapper.toHotelBasicResponse(hotel);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<HotelResponse> getAllHotels(Pageable pageable) {
    Page<Hotel> hotels = hotelRepository.findAll(pageable);
    List<UUID> hotelIds = hotels.getContent().stream().map(Hotel::getId).toList();

    // Batch fetch thumbnails
    Map<UUID, String> thumbnails = imageRepository
        .findByEntityIdInAndEntityTypeAndIsThumbnailTrue(hotelIds, ImageType.HOTEL)
        .stream()
        .collect(Collectors.toMap(Image::getEntityId, Image::getUrl, (a, b) -> a));

    // Batch fetch min prices
    Map<UUID, BigDecimal> minPrices = roomTypeRepository.findAllByHotel_IdIn(hotelIds).stream()
        .filter(rt -> !rt.isDeleted())
        .collect(
            Collectors.groupingBy(
                rt -> rt.getHotel().getId(),
                Collectors.mapping(
                    RoomType::getBasePrice,
                    Collectors.reducing(
                        BigDecimal.valueOf(Double.MAX_VALUE), BigDecimal::min))));

    return hotels.map(
        hotel -> {
          HotelResponse response = hotelMapper.toHotelResponse(hotel);
          response.setThumbnailUrl(thumbnails.get(hotel.getId()));
          BigDecimal minPrice = minPrices.get(hotel.getId());
          if (minPrice == null || minPrice.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) == 0) {
            minPrice = BigDecimal.ZERO;
          }
          response.setMinPrice(minPrice);
          return response;
        });
  }

  @Override
  public HotelBasicResponse updateHotel(UUID hotelId, UpdateHotelRequest request) {
    Hotel hotel = hotelRepository
        .findById(hotelId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    if (request.getName() != null)
      hotel.setName(request.getName());
    if (request.getDescription() != null)
      hotel.setDescription(request.getDescription());
    if (request.getAddress() != null)
      hotel.setAddress(request.getAddress());
    if (request.getCityProvince() != null)
      hotel.setCityProvince(request.getCityProvince());
    if (request.getCheckInTime() != null)
      hotel.setCheckInTime(request.getCheckInTime());
    if (request.getCheckOutTime() != null)
      hotel.setCheckOutTime(request.getCheckOutTime());

    if (request.getAmenityIds() != null) {
      List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
      hotel.setAmenities(new HashSet<>(amenities));
    }

    hotel = hotelRepository.save(hotel);
    return hotelMapper.toHotelBasicResponse(hotel);
  }

  @Override
  public RoomTypeResponse createRoomType(UUID hotelId, CreateRoomTypeRequest request) {
    Hotel hotel = hotelRepository
        .findById(hotelId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    RoomType roomType = RoomType.builder()
        .hotel(hotel)
        .name(request.getName())
        .description(request.getDescription())
        .capacityAdults(request.getCapacityAdults())
        .capacityChildren(request.getCapacityChildren())
        .basePrice(request.getBasePrice())
        .bedType(request.getBedType())
        .area(request.getArea())
        .build();

    roomType = roomTypeRepository.save(roomType);
    return hotelMapper.toRoomTypeResponse(roomType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoomTypeResponse> getRoomTypes(UUID hotelId) {
    List<RoomType> roomTypes = roomTypeRepository.findAllByHotel_Id(hotelId).stream()
        .filter(rt -> !rt.isDeleted())
        .toList();

    if (roomTypes.isEmpty()) {
      return Collections.emptyList();
    }

    List<UUID> roomTypeIds = roomTypes.stream().map(RoomType::getId).toList();

    Map<UUID, List<HotelImageResponse>> images = imageRepository
        .findByEntityIdInAndEntityTypeOrderByDisplayOrderAsc(roomTypeIds, ImageType.ROOM_TYPE)
        .stream()
        .collect(
            Collectors.groupingBy(
                Image::getEntityId,
                Collectors.mapping(
                    img -> HotelImageResponse.builder()
                        .id(img.getId())
                        .url(img.getUrl())
                        .isThumbnail(img.isThumbnail())
                        .build(),
                    Collectors.toList())));

    return roomTypes.stream()
        .map(
            roomType -> {
              RoomTypeResponse response = hotelMapper.toRoomTypeResponse(roomType);

              response.setImages(images.getOrDefault(roomType.getId(), Collections.emptyList()));

              return response;
            })
        .toList();
  }

  @Override
  public RoomTypeResponse updateRoomType(UUID roomTypeId, UpdateRoomTypeRequest request) {
    RoomType roomType = roomTypeRepository
        .findById(roomTypeId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

    if (request.getName() != null)
      roomType.setName(request.getName());
    if (request.getDescription() != null)
      roomType.setDescription(request.getDescription());
    if (request.getCapacityAdults() != null)
      roomType.setCapacityAdults(request.getCapacityAdults());
    if (request.getCapacityChildren() != null)
      roomType.setCapacityChildren(request.getCapacityChildren());
    if (request.getBasePrice() != null)
      roomType.setBasePrice(request.getBasePrice());
    if (request.getBedType() != null)
      roomType.setBedType(request.getBedType());
    if (request.getArea() != null)
      roomType.setArea(request.getArea());

    roomType = roomTypeRepository.save(roomType);
    return hotelMapper.toRoomTypeResponse(roomType);
  }

  @Override
  public void deleteRoomType(UUID roomTypeId) {
    RoomType roomType = roomTypeRepository
        .findById(roomTypeId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

    roomType.setDeleted(true);
    roomTypeRepository.save(roomType);
  }

  @Override
  public ReceptionistRoomResponse createRoom(UUID hotelId, CreateRoomRequest request) {
    Hotel hotel = hotelRepository
        .findById(hotelId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));
    RoomType roomType = roomTypeRepository
        .findById(request.getRoomTypeId())
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));

    Room room = Room.builder()
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
        .filter(r -> !r.isDeleted())
        .map(receptionistMapper::toReceptionistRoomResponse)
        .toList();
  }

  @Override
  public ReceptionistRoomResponse updateRoom(UUID roomId, UpdateRoomRequest request) {
    Room room = roomRepository
        .findById(roomId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room not found"));
    if (request.getRoomTypeId() != null) {
      RoomType roomType = roomTypeRepository
          .findById(request.getRoomTypeId())
          .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found"));
      room.setRoomType(roomType);
    }

    if (request.getRoomNumber() != null)
      room.setRoomNumber(request.getRoomNumber());
    if (request.getFloor() != null)
      room.setFloor(request.getFloor());

    room = roomRepository.save(room);
    return receptionistMapper.toReceptionistRoomResponse(room);
  }

  @Override
  public void deleteRoom(UUID roomId) {
    Room room = roomRepository
        .findById(roomId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Room not found"));

    room.setDeleted(true);
    roomRepository.save(room);
  }

  @Override
  public HotelServiceResponse createService(UUID hotelId, CreateHotelServiceRequest request) {
    Hotel hotel = hotelRepository
        .findById(hotelId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    HotelService service = HotelService
        .builder()
        .hotel(hotel)
        .name(request.getName())
        .category(request.getCategory())
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
        .filter(s -> !s.isDeleted())
        .map(receptionistMapper::toHotelServiceResponse)
        .toList();
  }

  @Override
  public HotelServiceResponse updateService(UUID serviceId, UpdateHotelServiceRequest request) {
    HotelService service = hotelServiceRepository
        .findById(serviceId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Service not found"));

    if (request.getName() != null)
      service.setName(request.getName());
    if (request.getCategory() != null)
      service.setCategory(request.getCategory());
    if (request.getPrice() != null)
      service.setPrice(request.getPrice());
    if (request.getUnit() != null)
      service.setUnit(request.getUnit());
    if (request.getDescription() != null)
      service.setDescription(request.getDescription());
    if (request.getIsActive() != null)
      service.setActive(request.getIsActive());

    service = hotelServiceRepository.save(service);
    return receptionistMapper.toHotelServiceResponse(service);
  }

  @Override
  public void deleteService(UUID serviceId) {
    HotelService service = hotelServiceRepository
        .findById(serviceId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Service not found"));

    service.setDeleted(true);
    hotelServiceRepository.save(service);
  }

  @Override
  public List<HotelImageResponse> uploadHotelImages(UUID hotelId, List<MultipartFile> files) {
    if (!hotelRepository.existsById(hotelId)) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found");
    }

    List<Image> newImages = new ArrayList<>();

    // Get max display order
    int maxOrder = imageRepository.findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(hotelId, ImageType.HOTEL)
        .stream().mapToInt(Image::getDisplayOrder).max().orElse(-1);

    for (MultipartFile file : files) {
      Map<String, Object> uploadResult = mediaService.uploadImage(file, CloudinaryFolder.HOTELS);
      String url = (String) uploadResult.get("url");
      String publicId = (String) uploadResult.get("public_id");

      Image image = Image.builder()
          .entityId(hotelId)
          .entityType(ImageType.HOTEL)
          .url(url)
          .publicId(publicId)
          .displayOrder(++maxOrder)
          .isThumbnail(maxOrder == 0) // First image is thumbnail by default
          .build();
      newImages.add(image);
    }

    newImages = imageRepository.saveAll(newImages);

    return newImages.stream()
        .map(img -> HotelImageResponse.builder()
            .id(img.getId())
            .url(img.getUrl())
            .isThumbnail(img.isThumbnail())
            .build())
        .toList();
  }

  @Override
  public void deleteHotelImage(UUID hotelId, UUID imageId) {
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Image not found"));

    if (!image.getEntityId().equals(hotelId) || image.getEntityType() != ImageType.HOTEL) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Image does not belong to this hotel");
    }

    if (image.getPublicId() != null) {
      mediaService.deleteImage(image.getPublicId());
    }

    imageRepository.delete(image);

    // If it was thumbnail, set first remaining image as thumbnail
    if (image.isThumbnail()) {
      List<Image> remaining = imageRepository.findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(hotelId,
          ImageType.HOTEL);
      if (!remaining.isEmpty()) {
        Image newThumbnail = remaining.get(0);
        newThumbnail.setThumbnail(true);
        imageRepository.save(newThumbnail);
      }
    }
  }

  @Override
  public void setHotelThumbnail(UUID hotelId, UUID imageId) {
    Image newThumbnail = imageRepository.findById(imageId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Image not found"));

    if (!newThumbnail.getEntityId().equals(hotelId) || newThumbnail.getEntityType() != ImageType.HOTEL) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Image does not belong to this hotel");
    }

    // Unset current thumbnail
    imageRepository.findFirstByEntityIdAndEntityTypeAndIsThumbnailTrue(hotelId, ImageType.HOTEL)
        .ifPresent(img -> {
          img.setThumbnail(false);
          imageRepository.save(img);
        });

    // Set new thumbnail
    newThumbnail.setThumbnail(true);
    imageRepository.save(newThumbnail);
  }

  @Override
  public List<HotelImageResponse> uploadRoomTypeImages(UUID roomTypeId, List<MultipartFile> files) {
    if (!roomTypeRepository.existsById(roomTypeId)) {
      throw new BaseAppException(WebErrorCode.NOT_FOUND, "Room type not found");
    }

    List<Image> newImages = new ArrayList<>();

    // Get max display order
    int maxOrder = imageRepository.findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(roomTypeId, ImageType.ROOM_TYPE)
        .stream().mapToInt(Image::getDisplayOrder).max().orElse(-1);

    for (MultipartFile file : files) {
      Map<String, Object> uploadResult = mediaService.uploadImage(file, CloudinaryFolder.ROOM_TYPES);
      String url = (String) uploadResult.get("url");
      String publicId = (String) uploadResult.get("public_id");

      Image image = Image.builder()
          .entityId(roomTypeId)
          .entityType(ImageType.ROOM_TYPE)
          .url(url)
          .publicId(publicId)
          .displayOrder(++maxOrder)
          .isThumbnail(maxOrder == 0) // First image is thumbnail by default
          .build();
      newImages.add(image);
    }

    newImages = imageRepository.saveAll(newImages);

    return newImages.stream()
        .map(img -> HotelImageResponse.builder()
            .id(img.getId())
            .url(img.getUrl())
            .isThumbnail(img.isThumbnail())
            .build())
        .toList();
  }

  @Override
  public void deleteRoomTypeImage(UUID roomTypeId, UUID imageId) {
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Image not found"));

    if (!image.getEntityId().equals(roomTypeId) || image.getEntityType() != ImageType.ROOM_TYPE) {
      throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Image does not belong to this room type");
    }

    if (image.getPublicId() != null) {
      mediaService.deleteImage(image.getPublicId());
    }
    imageRepository.delete(image);

    // If it was thumbnail, set first remaining image as thumbnail
    if (image.isThumbnail()) {
      List<Image> remaining = imageRepository.findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(roomTypeId,
          ImageType.ROOM_TYPE);
      if (!remaining.isEmpty()) {
        Image newThumbnail = remaining.get(0);
        newThumbnail.setThumbnail(true);
        imageRepository.save(newThumbnail);
      }
    }
  }
}

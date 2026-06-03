package com.travery.traverybackend.services.hotel.impl;

import com.travery.traverybackend.dtos.request.hotel.HotelSearchRequest;
import com.travery.traverybackend.dtos.response.hotel.HotelDetailResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelImageResponse;
import com.travery.traverybackend.dtos.response.hotel.HotelResponse;
import com.travery.traverybackend.dtos.response.hotel.RoomTypeResponse;
import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.hotel.RoomType;
import com.travery.traverybackend.enums.common.ImageType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.HotelMapper;
import com.travery.traverybackend.repositories.common.ImageRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.hotel.RoomTypeRepository;
import com.travery.traverybackend.services.hotel.HotelService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

  private final HotelRepository hotelRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final ImageRepository imageRepository;
  private final HotelMapper hotelMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<HotelResponse> searchHotels(HotelSearchRequest request, Pageable pageable) {
    if (request.getStartDate() != null && request.getEndDate() != null) {
      int roomCount =
          request.getRoomCount() != null && request.getRoomCount() > 0 ? request.getRoomCount() : 1;
      List<UUID> availableHotelIds =
          roomTypeRepository.findAvailableHotelIds(
              request.getStartDate(), request.getEndDate(), roomCount);
      request.setAvailableHotelIds(availableHotelIds);
    }

    Page<Hotel> hotels = hotelRepository.searchHotels(request, pageable);

    List<UUID> hotelIds = hotels.getContent().stream().map(Hotel::getId).toList();

    // Batch fetch thumbnails
    Map<UUID, String> thumbnails =
        imageRepository
            .findByEntityIdInAndEntityTypeAndIsThumbnailTrue(hotelIds, ImageType.HOTEL)
            .stream()
            .collect(Collectors.toMap(Image::getEntityId, Image::getUrl, (a, b) -> a));

    // Batch fetch min prices directly from DB
    Map<UUID, BigDecimal> minPrices =
        roomTypeRepository.findMinPricesByHotelIds(hotelIds).stream()
            .collect(
                Collectors.toMap(
                    RoomTypeRepository.HotelMinPriceProjection::getHotelId,
                    RoomTypeRepository.HotelMinPriceProjection::getMinPrice));

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
  public HotelDetailResponse getHotelDetail(UUID hotelId) {
    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));

    HotelDetailResponse response = hotelMapper.toHotelDetailResponse(hotel);

    // Fetch hotel images
    List<HotelImageResponse> hotelImages =
        imageRepository
            .findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(hotelId, ImageType.HOTEL)
            .stream()
            .map(
                img ->
                    HotelImageResponse.builder()
                        .id(img.getId())
                        .url(img.getUrl())
                        .isThumbnail(img.isThumbnail())
                        .build())
            .toList();
    response.setImages(hotelImages);

    // Fetch and map RoomTypes with their images
    List<RoomType> roomTypes =
        roomTypeRepository.findAllByHotel_Id(hotelId).stream()
            .filter(rt -> !rt.isDeleted())
            .toList();
    List<UUID> roomTypeIds = roomTypes.stream().map(RoomType::getId).toList();

    Map<UUID, List<HotelImageResponse>> roomTypeImages;
    if (roomTypeIds.isEmpty()) {
      roomTypeImages = Map.of();
    } else {
      roomTypeImages =
          imageRepository
              .findByEntityIdInAndEntityTypeOrderByDisplayOrderAsc(roomTypeIds, ImageType.ROOM_TYPE)
              .stream()
              .collect(
                  Collectors.groupingBy(
                      Image::getEntityId,
                      Collectors.mapping(
                          img ->
                              HotelImageResponse.builder()
                                  .id(img.getId())
                                  .url(img.getUrl())
                                  .isThumbnail(img.isThumbnail())
                                  .build(),
                          Collectors.toList())));
    }

    List<RoomTypeResponse> roomTypeResponses =
        roomTypes.stream()
            .map(
                rt -> {
                  RoomTypeResponse rtResponse = hotelMapper.toRoomTypeResponse(rt);
                  rtResponse.setImages(roomTypeImages.getOrDefault(rt.getId(), List.of()));
                  return rtResponse;
                })
            .toList();

    response.setRoomTypes(roomTypeResponses);

    return response;
  }
}

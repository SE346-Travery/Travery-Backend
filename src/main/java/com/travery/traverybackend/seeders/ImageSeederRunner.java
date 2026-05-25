package com.travery.traverybackend.seeders;

import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourItinerary;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.common.ImageType;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.common.ImageRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.media.MediaService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageSeederRunner implements CommandLineRunner {

  private final ImageRepository imageRepository;
  private final TourRepository tourRepository;
  private final UserRepository userRepository;
  private final DriverRepository driverRepository;
  private final MediaService mediaService;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    long imageCount = imageRepository.count();
    if (imageCount > 0) {
      log.info("Images already seeded. Skipping ImageSeederRunner.");
      return;
    }

    log.info("Starting Image Seeding Process...");

    // 1. Seed Tour and Tour Itinerary Images
    List<Tour> tours = tourRepository.findAll();
    int randomCounter = 1;

    for (Tour tour : tours) {
      log.info("Seeding images for Tour: {}", tour.getName());
      
      // Seed 3 images for Tour
      for (int i = 0; i < 3; i++) {
        String randomUrl = "https://picsum.photos/800/600?random=" + (randomCounter++);
        Map<String, Object> uploadResult = mediaService.uploadImageFromUrl(randomUrl, "tours");
        
        Image image = Image.builder()
            .entityId(tour.getId())
            .entityType(ImageType.TOUR)
            .url((String) uploadResult.get("secure_url"))
            .publicId((String) uploadResult.get("public_id"))
            .isThumbnail(i == 0) // First image is thumbnail
            .displayOrder(i)
            .build();
        imageRepository.save(image);
      }

      // Seed 1 image for each Tour Itinerary
      if (tour.getItineraries() != null) {
        for (TourItinerary itinerary : tour.getItineraries()) {
          String randomUrl = "https://picsum.photos/800/600?random=" + (randomCounter++);
          Map<String, Object> uploadResult = mediaService.uploadImageFromUrl(randomUrl, "itineraries");
          
          Image image = Image.builder()
              .entityId(itinerary.getId())
              .entityType(ImageType.TOUR_ITINERARY)
              .url((String) uploadResult.get("secure_url"))
              .publicId((String) uploadResult.get("public_id"))
              .isThumbnail(true)
              .displayOrder(0)
              .build();
          imageRepository.save(image);
        }
      }
    }

    // 2. Seed User Avatars
    List<User> users = userRepository.findAll();
    for (User user : users) {
      if (user.getAvatarUrl() == null) {
        log.info("Seeding avatar for User: {}", user.getFullName());
        String randomUrl = "https://picsum.photos/200/200?random=" + (randomCounter++);
        Map<String, Object> uploadResult = mediaService.uploadImageFromUrl(randomUrl, "avatars/users");
        
        user.setAvatarUrl((String) uploadResult.get("secure_url"));
        user.setAvatarPublicId((String) uploadResult.get("public_id"));
        userRepository.save(user);
      }
    }

    // 3. Seed Driver Avatars
    List<Driver> drivers = driverRepository.findAll();
    for (Driver driver : drivers) {
      if (driver.getAvatarUrl() == null) {
        log.info("Seeding avatar for Driver: {}", driver.getFullName());
        String randomUrl = "https://picsum.photos/200/200?random=" + (randomCounter++);
        Map<String, Object> uploadResult = mediaService.uploadImageFromUrl(randomUrl, "avatars/drivers");
        
        driver.setAvatarUrl((String) uploadResult.get("secure_url"));
        driver.setAvatarPublicId((String) uploadResult.get("public_id"));
        driverRepository.save(driver);
      }
    }

    log.info("Image Seeding Process Completed Successfully!");
  }
}

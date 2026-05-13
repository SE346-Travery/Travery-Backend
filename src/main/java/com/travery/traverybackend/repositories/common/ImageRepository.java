package com.travery.traverybackend.repositories.common;

import com.travery.traverybackend.entities.common.Image;
import com.travery.traverybackend.enums.common.ImageType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

  Optional<Image> findFirstByEntityIdAndEntityTypeAndIsThumbnailTrue(
      UUID entityId, ImageType entityType);

  List<Image> findByEntityIdInAndEntityTypeAndIsThumbnailTrue(
      List<UUID> entityIds, ImageType entityType);

  List<Image> findByEntityIdInAndEntityTypeOrderByDisplayOrderAsc(
      List<UUID> entityIds, ImageType entityType);

  List<Image> findByEntityIdAndEntityTypeOrderByDisplayOrderAsc(
      UUID entityId, ImageType entityType);
}

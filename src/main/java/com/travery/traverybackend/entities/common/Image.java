package com.travery.traverybackend.entities.common;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.common.ImageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Image extends AbstractBaseEntity {

  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", nullable = false, length = 50)
  private ImageType entityType;

  @Column(nullable = false, length = 500)
  private String url;

  @Column(name = "public_id", length = 255)
  private String publicId;

  @Column(name = "is_thumbnail", nullable = false)
  @Builder.Default
  private boolean isThumbnail = false;

  @Column(name = "display_order", nullable = false)
  @Builder.Default
  private int displayOrder = 0;
}

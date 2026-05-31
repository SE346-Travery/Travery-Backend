package com.travery.traverybackend.services.media.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.travery.traverybackend.enums.common.CloudinaryFolder;
import com.travery.traverybackend.services.media.MediaService;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

  private final Cloudinary cloudinary;

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> uploadImage(MultipartFile file, CloudinaryFolder folder) {
    try {
      return cloudinary
          .uploader()
          .upload(file.getBytes(), ObjectUtils.asMap("folder", "travery/" + folder.getPath()));
    } catch (IOException e) {
      log.error("Failed to upload image to Cloudinary", e);
      throw new RuntimeException("Image upload failed");
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> uploadImageFromUrl(String url, CloudinaryFolder folder) {
    try {
      return cloudinary
          .uploader()
          .upload(url, ObjectUtils.asMap("folder", "travery/" + folder.getPath()));
    } catch (IOException e) {
      log.error("Failed to upload image from URL to Cloudinary", e);
      throw new RuntimeException("Image upload failed");
    }
  }

  @Override
  public void deleteImage(String publicId) {
    if (publicId == null || publicId.isEmpty()) return;
    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      log.error("Failed to delete image from Cloudinary: {}", publicId, e);
    }
  }
}

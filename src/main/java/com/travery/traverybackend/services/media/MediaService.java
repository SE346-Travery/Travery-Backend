package com.travery.traverybackend.services.media;

import com.travery.traverybackend.enums.common.CloudinaryFolder;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
  /** Upload an image from MultipartFile */
  Map<String, Object> uploadImage(MultipartFile file, CloudinaryFolder folder);

  /** Upload an image from an external URL */
  Map<String, Object> uploadImageFromUrl(String url, CloudinaryFolder folder);

  /** Delete an image from Cloudinary by its publicId */
  void deleteImage(String publicId);
}

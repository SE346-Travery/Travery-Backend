package com.travery.traverybackend.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FirebaseConfig {

  @Value("${app.firebase.config-path}")
  private String configPath;

  @Value("${app.firebase.app-name}")
  private String appName;

  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    log.info("Starting Firebase initialization for app: {}", appName);

    // Check if app already exists
    for (FirebaseApp app : FirebaseApp.getApps()) {
      if (app.getName().equals(appName)) {
        log.info("Firebase App '{}' already initialized, returning existing instance.", appName);
        return app;
      }
    }

    if (configPath == null || configPath.isBlank()) {
      log.error("Firebase config path is missing. Please set app.firebase.config-path");
      throw new IllegalStateException("Firebase config path must be provided");
    }

    if (!Files.exists(Paths.get(configPath))) {
      log.error("Firebase config file not found at: {}", configPath);
      throw new IOException("Firebase config file not found at: " + configPath);
    }

    log.info("Loading Firebase configuration from: {}", configPath);
    try (InputStream serviceAccount = new FileInputStream(configPath)) {
      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      FirebaseApp app = FirebaseApp.initializeApp(options, appName);
      log.info("Firebase App '{}' initialized successfully.", appName);
      return app;
    } catch (IOException e) {
      log.error("Failed to initialize Firebase App '{}': {}", appName, e.getMessage());
      throw e;
    }
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
    return FirebaseMessaging.getInstance(firebaseApp);
  }
}

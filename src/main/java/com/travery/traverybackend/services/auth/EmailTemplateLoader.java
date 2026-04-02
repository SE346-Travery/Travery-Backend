package com.travery.traverybackend.services.auth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Loads HTML email templates from classpath:templates/email/ and replaces {{key}} placeholders with
 * provided values.
 *
 * <p>No Thymeleaf or FreeMarker required — zero additional dependencies.
 */
@Component
public class EmailTemplateLoader {

  private static final String TEMPLATE_BASE = "templates/email/";

  /**
   * Loads the given template file and replaces all {{key}} placeholders with entries from the
   * provided map.
   *
   * @param templateName file name inside templates/email/ (e.g. "register-otp.html")
   * @param variables map of placeholder names to replacement values
   * @return rendered HTML string
   */
  public String load(String templateName, Map<String, String> variables) {
    String template = readTemplate(templateName);
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return template;
  }

  private String readTemplate(String templateName) {
    ClassPathResource resource = new ClassPathResource(TEMPLATE_BASE + templateName);
    try (InputStream inputStream = resource.getInputStream()) {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load email template: " + templateName, e);
    }
  }
}

package com.travery.traverybackend.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestUtil {

  @Value("${app.backend.ip}")
  private String backendIp;

  /**
   * Extract client IP address from request headers (handling proxies) or fallback to configured
   * backend IP.
   */
  public String getIpAddress(HttpServletRequest request) {
    String xForwardedForHeader = request.getHeader("X-Forwarded-For");
    if (xForwardedForHeader == null) {
      var remoteAddr = request.getRemoteAddr();
      if (remoteAddr == null) {
        remoteAddr = backendIp;
      }
      return remoteAddr;
    }
    return xForwardedForHeader.split(",")[0].trim();
  }
}

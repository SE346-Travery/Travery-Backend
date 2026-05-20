package com.travery.traverybackend.utils;

import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    @Value("${app.backend.ip}")
    private static String backendIp;

    public static String getIpAddress(HttpServletRequest request) {
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

package com.travery.traverybackend.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.payment.vnpay")
@Getter
@Setter
public class VnPayConfig {

  private String tmnCode;
  private String secretKey;
  private String initPaymentUrl;
  private String returnUrl;
  private int timeout;
  private String deeplinkScheme;
}

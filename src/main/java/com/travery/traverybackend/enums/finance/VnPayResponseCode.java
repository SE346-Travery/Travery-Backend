package com.travery.traverybackend.enums.finance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * VNPAY vnp_ResponseCode / vnp_TransactionStatus codes. See:
 * https://sandbox.vnpayment.vn/apis/docs/bang-ma-loi/
 */
@Getter
@RequiredArgsConstructor
public enum VnPayResponseCode {
  SUCCESS("00", "Giao dich thanh cong"),
  SUSPICIOUS("07", "Giao dich bi nghi ngo gian lan"),
  NOT_REGISTERED("09", "The/Tai khoan chua dang ky InternetBanking"),
  AUTH_FAILED_3_TIMES("10", "Xac thuc khong dung qua 3 lan"),
  EXPIRED("11", "Da het han cho thanh toan"),
  ACCOUNT_LOCKED("12", "The/Tai khoan bi khoa"),
  WRONG_OTP("13", "Sai mat khau xac thuc giao dich (OTP)"),
  CANCELLED("24", "Khach hang huy giao dich"),
  INSUFFICIENT_BALANCE("51", "Tai khoan khong du so du"),
  DAILY_LIMIT_EXCEEDED("65", "Vuot qua han muc giao dich trong ngay"),
  BANK_MAINTENANCE("75", "Ngan hang dang bao tri"),
  WRONG_PASSWORD("79", "Nhap sai mat khau thanh toan qua so lan quy dinh"),
  UNKNOWN("99", "Loi khong xac dinh");

  private final String code;
  private final String description;

  public static VnPayResponseCode fromCode(String code) {
    for (VnPayResponseCode value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    return UNKNOWN;
  }

  public boolean isSuccess() {
    return this == SUCCESS;
  }
}

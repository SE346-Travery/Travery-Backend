package com.travery.traverybackend.services.finance;

import com.travery.traverybackend.dtos.request.finance.ProcessRefundRequest;
import com.travery.traverybackend.dtos.request.finance.RejectRefundRequest;
import com.travery.traverybackend.dtos.response.finance.RefundRequestResponse;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.RefundStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RefundService {
  Page<RefundRequestResponse> getRefundRequests(
      RefundStatus status, BookingType bookingType, Pageable pageable);

  RefundRequestResponse processRefund(
      UUID refundId, ProcessRefundRequest request, UUID coordinatorId);

  RefundRequestResponse rejectRefund(
      UUID refundId, RejectRefundRequest request, UUID coordinatorId);
}

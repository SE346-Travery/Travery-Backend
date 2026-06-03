package com.travery.traverybackend.services.finance.impl;

import com.travery.traverybackend.dtos.request.finance.ProcessRefundRequest;
import com.travery.traverybackend.dtos.request.finance.RejectRefundRequest;
import com.travery.traverybackend.dtos.response.finance.RefundRequestResponse;
import com.travery.traverybackend.entities.finance.RefundRequest;
import com.travery.traverybackend.entities.user.Coordinator;
import com.travery.traverybackend.entities.user.User;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.RefundStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.RefundMapper;
import com.travery.traverybackend.repositories.finance.RefundRequestRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.finance.RefundService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundServiceImpl implements RefundService {

  private final RefundRequestRepository refundRequestRepository;
  private final UserRepository userRepository;
  private final RefundMapper refundMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<RefundRequestResponse> getRefundRequests(
      RefundStatus status, BookingType bookingType, Pageable pageable) {
    Page<RefundRequest> refunds =
        refundRequestRepository.findByFilters(status, bookingType, pageable);
    return refunds.map(refundMapper::toRefundRequestResponse);
  }

  @Override
  @Transactional
  public RefundRequestResponse processRefund(
      UUID refundId, ProcessRefundRequest request, UUID coordinatorId) {
    RefundRequest refund =
        refundRequestRepository
            .findWithUserById(refundId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund request not found"));

    if (refund.getStatus() != RefundStatus.PENDING) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Only pending refund requests can be processed");
    }

    User coordinatorUser =
        userRepository
            .findById(coordinatorId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Coordinator not found"));

    if (!(coordinatorUser instanceof Coordinator)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "User is not a coordinator");
    }

    refund.setActualRefunded(request.getActualRefunded());
    refund.setStatus(RefundStatus.COMPLETED);
    refund.setProcessedBy((Coordinator) coordinatorUser);

    refund = refundRequestRepository.save(refund);

    log.info(
        "Refund request {} processed by coordinator {}. Actual refunded amount: {}",
        refund.getId(),
        coordinatorUser.getId(),
        refund.getActualRefunded());

    return refundMapper.toRefundRequestResponse(refund);
  }

  @Override
  @Transactional
  public RefundRequestResponse rejectRefund(
      UUID refundId, RejectRefundRequest request, UUID coordinatorId) {
    RefundRequest refund =
        refundRequestRepository
            .findWithUserById(refundId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund request not found"));

    if (refund.getStatus() != RefundStatus.PENDING) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Only pending refund requests can be rejected");
    }

    User coordinatorUser =
        userRepository
            .findById(coordinatorId)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Coordinator not found"));

    if (!(coordinatorUser instanceof Coordinator)) {
      throw new BaseAppException(WebErrorCode.FORBIDDEN, "User is not a coordinator");
    }

    refund.setStatus(RefundStatus.REJECTED);
    refund.setProcessedBy((Coordinator) coordinatorUser);
    refund.setRejectReason(request.getReason());

    refund = refundRequestRepository.save(refund);

    log.info(
        "Refund request {} rejected by coordinator {}. Reason: {}",
        refund.getId(),
        coordinatorUser.getId(),
        request.getReason());

    return refundMapper.toRefundRequestResponse(refund);
  }
}

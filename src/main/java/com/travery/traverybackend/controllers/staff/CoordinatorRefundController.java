package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.finance.ProcessRefundRequest;
import com.travery.traverybackend.dtos.request.finance.RejectRefundRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.finance.RefundRequestResponse;
import com.travery.traverybackend.enums.booking.BookingType;
import com.travery.traverybackend.enums.finance.RefundStatus;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.finance.RefundService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coordinator/refunds")
@RequiredArgsConstructor
public class CoordinatorRefundController extends AbstractBaseController {

  private final RefundService refundService;

  @GetMapping
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<Page<RefundRequestResponse>>> getRefundRequests(
      @RequestParam(required = false, defaultValue = "PENDING") RefundStatus status,
      @RequestParam(required = false) BookingType type,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
          Pageable pageable) {

    Page<RefundRequestResponse> response = refundService.getRefundRequests(status, type, pageable);

    return success(response, "Refund requests retrieved successfully");
  }

  @PutMapping("/{refundId}/process")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<RefundRequestResponse>> processRefund(
      @PathVariable UUID refundId,
      @Valid @RequestBody ProcessRefundRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    RefundRequestResponse response =
        refundService.processRefund(refundId, request, userDetails.getUserId());
    return success(response, "Refund request processed successfully");
  }

  @PutMapping("/{refundId}/reject")
  @PreAuthorize("hasRole('COORDINATOR')")
  public ResponseEntity<SingleResponse<RefundRequestResponse>> rejectRefund(
      @PathVariable UUID refundId,
      @Valid @RequestBody RejectRefundRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    RefundRequestResponse response =
        refundService.rejectRefund(refundId, request, userDetails.getUserId());
    return success(response, "Refund request rejected successfully");
  }
}

package com.travery.traverybackend.controllers.staff;

import com.travery.traverybackend.controllers.AbstractBaseController;
import com.travery.traverybackend.dtos.request.finance.RefundPolicyRequest;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.finance.RefundPolicyResponse;
import com.travery.traverybackend.services.finance.RefundPolicyService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/refund-policies")
@RequiredArgsConstructor
public class AdminRefundPolicyController extends AbstractBaseController {

  private final RefundPolicyService refundPolicyService;

  @PostMapping
  public ResponseEntity<SingleResponse<RefundPolicyResponse>> createPolicy(
      @Valid @RequestBody RefundPolicyRequest request) {
    RefundPolicyResponse response = refundPolicyService.createPolicy(request);
    return created(response, "Refund policy created successfully");
  }

  @PutMapping("/{id}")
  public ResponseEntity<SingleResponse<RefundPolicyResponse>> updatePolicy(
      @PathVariable UUID id, @Valid @RequestBody RefundPolicyRequest request) {
    RefundPolicyResponse response = refundPolicyService.updatePolicy(id, request);
    return success(response, "Refund policy updated successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<SingleResponse<RefundPolicyResponse>> getPolicyById(@PathVariable UUID id) {
    RefundPolicyResponse response = refundPolicyService.getPolicyById(id);
    return success(response, "Refund policy retrieved successfully");
  }

  @GetMapping
  public ResponseEntity<SingleResponse<Page<RefundPolicyResponse>>> getAllPolicies(
      @PageableDefault(size = 10) Pageable pageable) {
    Page<RefundPolicyResponse> response = refundPolicyService.getAllPolicies(pageable);
    return success(response, "Refund policies retrieved successfully");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<SuccessResponse> deletePolicy(@PathVariable UUID id) {
    refundPolicyService.deletePolicy(id);
    return success("Refund policy deleted successfully");
  }
}

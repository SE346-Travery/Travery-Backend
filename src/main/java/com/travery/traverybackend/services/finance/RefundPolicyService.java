package com.travery.traverybackend.services.finance;

import com.travery.traverybackend.dtos.request.finance.RefundPolicyRequest;
import com.travery.traverybackend.dtos.response.finance.RefundPolicyResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RefundPolicyService {

  RefundPolicyResponse createPolicy(RefundPolicyRequest request);

  RefundPolicyResponse updatePolicy(UUID id, RefundPolicyRequest request);

  RefundPolicyResponse getPolicyById(UUID id);

  Page<RefundPolicyResponse> getAllPolicies(Pageable pageable);

  void deletePolicy(UUID id);
}

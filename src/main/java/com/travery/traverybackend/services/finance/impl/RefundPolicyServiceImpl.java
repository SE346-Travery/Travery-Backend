package com.travery.traverybackend.services.finance.impl;

import com.travery.traverybackend.dtos.request.finance.RefundPolicyRequest;
import com.travery.traverybackend.dtos.response.finance.RefundPolicyResponse;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.finance.RefundPolicyRule;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.RefundPolicyMapper;
import com.travery.traverybackend.repositories.finance.RefundPolicyRepository;
import com.travery.traverybackend.services.finance.RefundPolicyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundPolicyServiceImpl implements RefundPolicyService {

  private final RefundPolicyRepository refundPolicyRepository;
  private final RefundPolicyMapper refundPolicyMapper;

  @Override
  @Transactional
  public RefundPolicyResponse createPolicy(RefundPolicyRequest request) {
    if (refundPolicyRepository
        .findByNameAndServiceTypeAndIsDeletedFalse(request.getName(), request.getServiceType())
        .isPresent()) {
      throw new BaseAppException(
          WebErrorCode.BAD_REQUEST, "Refund policy with this name and service type already exists");
    }

    RefundPolicy policy = refundPolicyMapper.toRefundPolicy(request);

    // Maintain bidirectional relationship
    if (policy.getRules() != null) {
      for (RefundPolicyRule rule : policy.getRules()) {
        rule.setRefundPolicy(policy);
      }
    }

    policy = refundPolicyRepository.save(policy);
    return refundPolicyMapper.toRefundPolicyResponse(policy);
  }

  @Override
  @Transactional
  public RefundPolicyResponse updatePolicy(UUID id, RefundPolicyRequest request) {
    RefundPolicy policy =
        refundPolicyRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund policy not found"));

    // Check name conflict
    refundPolicyRepository
        .findByNameAndServiceTypeAndIsDeletedFalse(request.getName(), request.getServiceType())
        .filter(p -> !p.getId().equals(id))
        .ifPresent(
            p -> {
              throw new BaseAppException(
                  WebErrorCode.BAD_REQUEST,
                  "Refund policy with this name and service type already exists");
            });

    policy.setName(request.getName());
    policy.setServiceType(request.getServiceType());

    // Update rules (orphanRemoval = true will delete old rules not in the new list)
    policy.getRules().clear();

    List<RefundPolicyRule> newRules =
        request.getRules().stream().map(refundPolicyMapper::toRefundPolicyRule).toList();

    for (RefundPolicyRule rule : newRules) {
      rule.setRefundPolicy(policy);
      policy.getRules().add(rule);
    }

    policy = refundPolicyRepository.save(policy);
    return refundPolicyMapper.toRefundPolicyResponse(policy);
  }

  @Override
  @Transactional(readOnly = true)
  public RefundPolicyResponse getPolicyById(UUID id) {
    RefundPolicy policy =
        refundPolicyRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund policy not found"));
    return refundPolicyMapper.toRefundPolicyResponse(policy);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<RefundPolicyResponse> getAllPolicies(Pageable pageable) {
    return refundPolicyRepository
        .findAllByIsDeletedFalse(pageable)
        .map(refundPolicyMapper::toRefundPolicyResponse);
  }

  @Override
  @Transactional
  public void deletePolicy(UUID id) {
    RefundPolicy policy =
        refundPolicyRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund policy not found"));

    policy.setDeleted(true);
    refundPolicyRepository.save(policy);
  }
}

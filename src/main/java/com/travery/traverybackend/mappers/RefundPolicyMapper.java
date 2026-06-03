package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.request.finance.RefundPolicyRequest;
import com.travery.traverybackend.dtos.request.finance.RefundPolicyRuleRequest;
import com.travery.traverybackend.dtos.response.finance.RefundPolicyResponse;
import com.travery.traverybackend.dtos.response.finance.RefundPolicyRuleResponse;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.finance.RefundPolicyRule;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    builder = @org.mapstruct.Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefundPolicyMapper {

  RefundPolicy toRefundPolicy(RefundPolicyRequest request);

  RefundPolicyRule toRefundPolicyRule(RefundPolicyRuleRequest request);

  RefundPolicyResponse toRefundPolicyResponse(RefundPolicy policy);

  RefundPolicyRuleResponse toRefundPolicyRuleResponse(RefundPolicyRule rule);
}

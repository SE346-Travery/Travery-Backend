package com.travery.traverybackend.repositories.finance;

import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import java.util.Optional;
import java.util.UUID;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, UUID> {

  Optional<RefundPolicy> findByIdAndIsDeletedFalse(UUID id);

  Optional<RefundPolicy> findByNameAndServiceType(String name, RefundServiceType serviceType);

  Optional<RefundPolicy> findByNameAndServiceTypeAndIsDeletedFalse(String name, RefundServiceType serviceType);

  Page<RefundPolicy> findAllByIsDeletedFalse(org.springframework.data.domain.Pageable pageable);
}

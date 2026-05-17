package com.travery.traverybackend.repositories.finance;

import com.travery.traverybackend.entities.finance.RefundRequest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID> {}

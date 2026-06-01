package com.travery.traverybackend.controllers.common;

import com.travery.traverybackend.services.common.HibernateSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/system")
@RequiredArgsConstructor
@Tag(name = "System Admin", description = "System Administration APIs")
public class SystemAdminController {

  private final HibernateSearchService hibernateSearchService;

  @PostMapping("/reindex")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Trigger Hibernate Search Mass Indexing (Async)")
  public ResponseEntity<Map<String, String>> triggerMassIndexing() {
    hibernateSearchService.triggerMassIndexing();
    return ResponseEntity.ok(
        Map.of(
            "message",
            "Mass indexing has been started asynchronously. Please check the server logs for progress."));
  }
}

package com.travery.traverybackend.services.common;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HibernateSearchService {

  private final EntityManager entityManager;

  @Async
  @Transactional
  public void triggerMassIndexing() {
    log.info("Starting Hibernate Search Mass Indexing via API...");
    try {
      SearchSession searchSession = Search.session(entityManager);

      // Tự động tính toán số luồng dựa trên số nhân CPU của Server
      int cpuCores = Runtime.getRuntime().availableProcessors();
      int threadsToUse = Math.max(cpuCores * 2, 4); // Tối thiểu 4 luồng

      searchSession
          .massIndexer()
          .idFetchSize(150)
          .batchSizeToLoadObjects(25)
          .threadsToLoadObjects(threadsToUse)
          .startAndWait();

      log.info(
          "Hibernate Search Mass Indexing completed successfully with {} threads!", threadsToUse);

    } catch (InterruptedException e) {
      log.error("Hibernate Search Mass Indexing was interrupted", e);
      Thread.currentThread().interrupt();
    }
  }
}

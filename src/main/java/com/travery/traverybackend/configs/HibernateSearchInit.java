package com.travery.traverybackend.configs; // Lưu ý package name

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HibernateSearchInit implements ApplicationListener<ApplicationReadyEvent> {

    private final EntityManager entityManager;

    @Value("${app.hibernate.search.mass-indexer.enabled:false}")
    private boolean isMassIndexerEnabled;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!isMassIndexerEnabled) {
            log.info("Hibernate Search Mass Indexer is disabled.");
            return;
        }

        log.info("Starting Hibernate Search Mass Indexing...");
        try {
            SearchSession searchSession = Search.session(entityManager);

            // Tự động tính toán số luồng dựa trên số nhân CPU của Server
            int cpuCores = Runtime.getRuntime().availableProcessors();
            int threadsToUse = Math.max(cpuCores * 2, 4); // Tối thiểu 4 luồng

            searchSession.massIndexer()
                    .idFetchSize(150)
                    .batchSizeToLoadObjects(25)
                    .threadsToLoadObjects(threadsToUse) // Đã được tối ưu tự động
                    .startAndWait();

            log.info("Hibernate Search Mass Indexing completed successfully with {} threads!", threadsToUse);

        } catch (InterruptedException e) {
            log.error("Hibernate Search Mass Indexing was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
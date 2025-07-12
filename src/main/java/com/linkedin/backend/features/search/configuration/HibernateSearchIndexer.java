package com.linkedin.backend.features.search.configuration;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
//import org.hibernate.search.mapper.orm.mapping.SearchMapping;
//import org.hibernate.search.mapper.orm.session.SearchSession;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Component
//public class HibernateSearchIndexer {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional
//    public void initiateIndexing() {
//        try {
//            // Hibernate Search 7.2.2: dùng unwrap để lấy SearchSession
//            SearchSession searchSession = entityManager.unwrap(SearchSession.class);
//
//            MassIndexer indexer = searchSession.massIndexer()
//                    .idFetchSize(150)
//                    .batchSizeToLoadObjects(25)
//                    .threadsToLoadObjects(4);
//
//            indexer.start()
//                    .thenAccept(result -> log.info("Hibernate Search 8 indexing complete"))
//                    .exceptionally(ex -> {
//                        log.error("Error during Hibernate Search indexing: {}", ex.getMessage());
//                        return null;
//                    });
//
//        } catch (Exception e) {
//            log.error("Failed to initiate Hibernate Search indexer", e);
//        }
//    }
//}
//


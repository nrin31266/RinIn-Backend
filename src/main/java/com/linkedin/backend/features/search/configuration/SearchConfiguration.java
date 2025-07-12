package com.linkedin.backend.features.search.configuration;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
@Configuration
public class SearchConfiguration {
    private static final String LUCENE_INDEX_DIR = "./lucene/indexes";


    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initiateIndexing() {
        try {
            SearchSession searchSession = Search.session(entityManager);
            searchSession.massIndexer()
                    .threadsToLoadObjects(4)
                    .start()
                    .thenAccept(r -> System.out.println("✅ Indexing complete"))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Tạm thời không sử dụng phương thức này để xóa thư mục chỉ mục Lucene
//    @PreDestroy
//    public void cleanUp() {
//        try {
//            Path directory = Paths.get(LUCENE_INDEX_DIR);
//            if (Files.exists(directory)) {
//                deleteDirectoryRecursively(directory);
//                log.info("Lucene index directory cleared successfully.");
//            }
//        } catch (IOException e) {
//            log.error("Error while clearing Lucene index directory: {}", e.getMessage());
//        }
//    }
//
//    private void deleteDirectoryRecursively(Path path) throws IOException {
//        Files.walk(path)
//                .sorted(Comparator.reverseOrder()) // Đảm bảo xóa các file con trước khi xóa thư mục cha
//                .map(Path::toFile)
//                .forEach(File::delete);
//    }
}

package com.linkedin.backend.features.search.service;

import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchService {
    EntityManager entityManager;

    public List<User> searchUsers(String query) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession.search(User.class)
                .where(f -> f.match()
                        .fields("firstName", "lastName", "email", "company", "position", "location", "about")
                        .matching(query)
                        .fuzzy(2) // Cho phép tìm kiếm mờ với độ mờ là 2
                )
                .fetchAllHits();
    }
}

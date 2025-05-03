package com.tdsa1.tdsa1.Security;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends ElasticsearchRepository<UserEntity, String> {
    UserEntity findByUsername(String username);

    boolean existsByUsername(String username);
}

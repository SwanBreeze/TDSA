package com.tdsa1.tdsa1.Repository;

import com.tdsa1.tdsa1.Document.EmploiDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmploiRepository extends ElasticsearchRepository<EmploiDocument, String> {}
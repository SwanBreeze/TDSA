package com.tdsa1.tdsa1.Repository;

import com.tdsa1.tdsa1.Document.EmploiDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmploiRepository extends ElasticsearchRepository<EmploiDocument, String> {}


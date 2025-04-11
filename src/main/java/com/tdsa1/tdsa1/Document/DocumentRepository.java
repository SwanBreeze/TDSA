package com.tdsa1.tdsa1.Document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentRepository extends ElasticsearchRepository<DocumentModel, String> {
}

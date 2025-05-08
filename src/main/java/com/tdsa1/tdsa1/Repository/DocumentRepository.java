package com.tdsa1.tdsa1.Repository;

import com.tdsa1.tdsa1.Document.BaseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends ElasticsearchRepository<BaseDocument, String> {
}
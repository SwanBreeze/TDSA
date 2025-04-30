package com.tdsa1.tdsa1.Repository;


import com.tdsa1.tdsa1.Document.PvDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PvRepository extends ElasticsearchRepository<PvDocument, String> {}

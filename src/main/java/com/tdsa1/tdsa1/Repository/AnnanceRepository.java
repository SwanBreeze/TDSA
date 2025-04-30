package com.tdsa1.tdsa1.Repository;

import com.tdsa1.tdsa1.Document.AnnanceDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AnnanceRepository extends ElasticsearchRepository<AnnanceDocument, String> {}


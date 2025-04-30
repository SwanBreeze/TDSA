package com.tdsa1.tdsa1.Repository;

import com.tdsa1.tdsa1.Document.PlanningDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PlanningRepository extends ElasticsearchRepository<PlanningDocument, String> {}

package com.tdsa1.tdsa1.Repository;
import com.tdsa1.tdsa1.Model.DocumentModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentRepository extends ElasticsearchRepository<DocumentModel,String > {


}

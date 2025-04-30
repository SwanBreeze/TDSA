package com.tdsa1.tdsa1.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.tdsa1.tdsa1.Document")
public class ElasticConfiguration extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.username}")
    private String username;


    @Value("${spring.elasticsearch.password}")
    private String password;


    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")  // HTTP
                .withBasicAuth(username, password)
                .build();  // No SSL
    }
}
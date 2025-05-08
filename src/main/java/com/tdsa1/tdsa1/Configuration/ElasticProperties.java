package com.tdsa1.tdsa1.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.elasticsearch")
@EnableElasticsearchRepositories(basePackages = "com.tdsa1.tdsa1.Repository")
public class ElasticProperties {

    private String username;
    private String password;
    private String uris;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUris() {
        return uris;
    }

    public void setUris(String uris) {
        this.uris = uris;
    }
}

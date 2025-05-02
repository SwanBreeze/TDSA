package com.tdsa1.tdsa1.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Document;


@AllArgsConstructor

@Builder

@Document(indexName = "pv")
public class PvDocument extends BaseDocument {

    private String type;        // csf or csd

    public PvDocument() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}



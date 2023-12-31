package com.example.couchbase.multiple.clusters.entity;

import com.example.couchbase.multiple.clusters.framework.WithDocumentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
public class SampleEntity implements WithDocumentId<SampleEntity> {
    @Id private String documentId;

    public SampleEntity(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public SampleEntity withId(String id) {
        return new SampleEntity(id);
    }
}

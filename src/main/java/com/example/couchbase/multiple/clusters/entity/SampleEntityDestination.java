package com.example.couchbase.multiple.clusters.entity;

public class SampleEntityDestination extends SampleEntitySource {
    public SampleEntityDestination(String documentId) {
        super(documentId);
    }

    public SampleEntityDestination(SampleEntitySource sameEntity) {
        this(sameEntity.getDocumentId());
    }
}

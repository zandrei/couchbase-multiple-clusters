package com.example.couchbase.multiple.clusters.entity;

public class SampleEntitySource extends SampleEntity {

    public SampleEntitySource(String documentId) {
        super(documentId);
    }

    @Override
    public SampleEntitySource withId(String id) {
        return new SampleEntitySource(id);
    }
}

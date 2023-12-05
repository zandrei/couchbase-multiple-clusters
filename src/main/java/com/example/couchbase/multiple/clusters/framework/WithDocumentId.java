package com.example.couchbase.multiple.clusters.framework;

public interface WithDocumentId<T> {

    T withId(String id);
}

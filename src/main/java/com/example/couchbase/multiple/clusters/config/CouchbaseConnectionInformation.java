package com.example.couchbase.multiple.clusters.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouchbaseConnectionInformation {
    protected String connection;
    protected BucketInformation bucket;
    protected Integer port;

    public record BucketInformation(String name, String username, String password) {}
}

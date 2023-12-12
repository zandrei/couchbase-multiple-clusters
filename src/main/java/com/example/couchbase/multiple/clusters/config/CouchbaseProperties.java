package com.example.couchbase.multiple.clusters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.couchbase")
public class CouchbaseProperties extends CouchbaseConnectionInformation {

    public String getConnectionString() {
        return port == null ? connection : String.format("couchbase://localhost:11210,%s:%s=manager", connection, port);
    }
}

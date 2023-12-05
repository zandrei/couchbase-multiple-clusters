package com.example.couchbase.multiple.clusters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.couchbase-alt")
public class CouchbaseAltProperties extends CouchbaseConnectionInformation {

    public String getConnectionString() {
        return port == null ? connection : String.format("%s:%s=manager", connection, port);
    }
}

package com.example.couchbase.multiple.clusters.repository;

import com.example.couchbase.multiple.clusters.entity.SampleEntitySource;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleSourceRepository
        extends org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository<
                SampleEntitySource, String> {}

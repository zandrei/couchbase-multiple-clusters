package com.example.couchbase.multiple.clusters.repository;

import com.example.couchbase.multiple.clusters.entity.SampleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository
        extends org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository<
                SampleEntity, String> {}

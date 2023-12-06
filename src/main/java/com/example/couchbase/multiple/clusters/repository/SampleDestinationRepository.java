package com.example.couchbase.multiple.clusters.repository;

import com.example.couchbase.multiple.clusters.entity.SampleEntityDestination;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleDestinationRepository
        extends org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository<
                SampleEntityDestination, String> {}

# POC scope

We need to be able to map the same spring-data-couchbase Document to 2 clusters. In a custom repository, we want to be
able to find by id the document in the "destination", and if it not found there, try to get it from "source", save it in
the "destination" cluster and move on.

# POC setup

In the `docker-compose` folder you have a setup to start 2 couchbase clusters (`community-6.6.0`) on different ports. In
one of them (`db`) we will also seed some documents for testing. In the other one (`db-alt`) we will not add anything
but create the `sample` bucket.
The application will use a mapping of `<destination>:<source>` to be able to change the tenant when migrating (this was
added only to highlight that, because of how the TargetRepository is constructed in the
ReactiveCouchbaseRepositoryFactory, changing the repositoryBaseClass inside the EnableReactiveCouchbaseRepositories is
not sufficient to use a different base repository class).

The application will create a TestService which will do a findById for an entity that is mapped to both clusters, will
not find it in the "destination" cluster, so it will apply the above algorithm. In the end, it will delete it so that
the next run behaves the same.

# How to run it

Start the docker compose setup with `docker compose up -d` then start the application with `./gradlew bootRun`. You
should see the logs about not finding the document in the primary cluster and going to fetch it from the fallback
cluster

# Issues

The main goal of the requirement is to have a custom repository implementation that will apply the given algorithm.
Because we needed two different instances of a ReactiveCouchbaseOperations we needed to change the RepositoryFactory as
well.

Apart from these we also had to do these changes:

- overwrite the `findAll(Query query)` method from the SimpleReactiveCouchbaseRepository because of the
  getQueryScanConsistency() method inside the base repository that
  uses the crudMethodMetadata property in the CouchbaseRepositoryBase class which is package private (so we cannot
  overwrite it)
- overwrite ReactiveCouchbaseRepositoryFactoryBean because we want to make spring boot back off with registering a
  CouchbaseReactiveRepositoriesRegistrar which will EnableReactiveCouchbaseRepositories instead of
  EnableSampleReactiveCouchbaseRepositories and re-register every bean
- duplicate the EnableReactiveCouchbaseRepositories annotation to avoid importing the previous stack because of the
  Import of ReactiveCouchbaseRepositoriesRegistrar
- reimplement a CrudMethodMetadataPostProcessor

# Questions

Is there an easier way to achieve this? It feels like we're replacing the entire layer of spring-data-couchbase and the
issue I'm thinking of is maintenance during framework upgrades

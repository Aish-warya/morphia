package xyz.morphia.query;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import xyz.morphia.Datastore;

/**
 * A factory for {@link Query}ies.
 */
public interface QueryFactory {

    /**
     * Creates and returns a {@link Query} for the given arguments. Default implementations of this method will simply delegate to {@link
     * #createQuery(Datastore, MongoCollection, Class, Document)} with the last argument being {@code null}.
     *
     * @param datastore  the Datastore to use
     * @param collection the collection to query
     * @param type       the type of the result
     * @param <T>        the type of the result
     * @return the query
     * @see #createQuery(Datastore, MongoCollection, Class, Document)
     */
    <T> Query<T> createQuery(Datastore datastore, MongoCollection<T> collection, Class<T> type);

    /**
     * Creates and returns a {@link Query} for the given arguments. The last argument is optional and may be {@code null}.
     *
     * @param datastore  the Datastore to use
     * @param collection the collection to query
     * @param type       the type of the result
     * @param query      the document containing the query structure
     * @param <T>        the type of the result
     * @return the query
     */
    <T> Query<T> createQuery(Datastore datastore, MongoCollection<T> collection, Class<T> type, Document query);

    /**
     * Creates an unvalidated {@link Query} typically for use in aggregation pipelines.
     *
     * @param datastore the Datastore to use
     * @param <T>       the type of the result
     * @return the query
     */
    <T> Query<T> createQuery(Datastore datastore);
}

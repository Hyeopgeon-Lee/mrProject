package mongo.conn;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB client for the optional MapReduce/MongoDB exercises.
 *
 * Configure MONGODB_URI (including authentication if required) and
 * MONGODB_DATABASE on each YARN container. Never embed passwords in source.
 * This connection must be closed by the Mapper/Reducer cleanup method.
 */
public final class MongoDBConnection implements AutoCloseable {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDB;

    public MongoDBConnection() {
        String uri = System.getenv().getOrDefault("MONGODB_URI", "mongodb://localhost:27017");
        String database = System.getenv().getOrDefault("MONGODB_DATABASE", "MyDB");
        mongoClient = MongoClients.create(uri);
        mongoDB = mongoClient.getDatabase(database);
    }

    public MongoDatabase getMongoDB() {
        return mongoDB;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}

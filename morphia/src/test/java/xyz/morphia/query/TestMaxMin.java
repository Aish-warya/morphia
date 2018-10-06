package xyz.morphia.query;

import com.mongodb.MongoException;
import com.mongodb.client.model.FindOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xyz.morphia.Datastore;
import xyz.morphia.TestBase;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Field;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Index;
import xyz.morphia.annotations.Indexes;

import java.util.List;

public class TestMaxMin extends TestBase {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        getMapper().map(IndexedEntity.class);
        getDatastore().ensureIndexes();
    }

    @Test(expected = MongoException.class)
    public void testExceptionForIndexMismatch() {
        getDatastore().find(IndexedEntity.class)
                      .get(new FindOptions()
                               .min(new Document("doesNotExist", 1)));
    }

    @Test
    public void testMax() {
        final IndexedEntity a = new IndexedEntity("a");
        final IndexedEntity b = new IndexedEntity("b");
        final IndexedEntity c = new IndexedEntity("c");

        Datastore ds = getDatastore();

        ds.save(a);
        ds.save(b);
        ds.save(c);

        Assert.assertEquals("last",
            b.id,
            ds.find(IndexedEntity.class)
              .order("-id")
              .get(new FindOptions()
                       .max(new Document("testField", "c")))
                .id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMaxCompoundIndex() {
        final IndexedEntity a1 = new IndexedEntity("a");
        final IndexedEntity a2 = new IndexedEntity("a");
        final IndexedEntity b1 = new IndexedEntity("b");
        final IndexedEntity b2 = new IndexedEntity("b");
        final IndexedEntity c1 = new IndexedEntity("c");
        final IndexedEntity c2 = new IndexedEntity("c");

        Datastore ds = getDatastore();

        ds.save(a1);
        ds.save(a2);
        ds.save(b1);
        ds.save(b2);
        ds.save(c1);
        ds.save(c2);

        final List<IndexedEntity> l = ds.find(IndexedEntity.class).order("testField, id")
                                        .asList(new FindOptions()
                                                    .max(new Document("testField", "b")
                                                             .append("_id", b2.id)));

        Assert.assertEquals("size", 3, l.size());
        Assert.assertEquals("item", b1.id, l.get(2).id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMin() {
        final IndexedEntity a = new IndexedEntity("a");
        final IndexedEntity b = new IndexedEntity("b");
        final IndexedEntity c = new IndexedEntity("c");

        Datastore ds = getDatastore();

        ds.save(a);
        ds.save(b);
        ds.save(c);

        Assert.assertEquals("last", b.id, ds.find(IndexedEntity.class).order("id")
                                            .get(new FindOptions()
                                                     .min(new Document("testField", "b"))).id);
    }

    @Test
    public void testMinCompoundIndex() {
        final IndexedEntity a1 = new IndexedEntity("a");
        final IndexedEntity a2 = new IndexedEntity("a");
        final IndexedEntity b1 = new IndexedEntity("b");
        final IndexedEntity b2 = new IndexedEntity("b");
        final IndexedEntity c1 = new IndexedEntity("c");
        final IndexedEntity c2 = new IndexedEntity("c");

        Datastore ds = getDatastore();

        ds.save(a1);
        ds.save(a2);
        ds.save(b1);
        ds.save(b2);
        ds.save(c1);
        ds.save(c2);

        final List<IndexedEntity> l = ds.find(IndexedEntity.class).order("testField, id")
                                        .asList(new FindOptions()
                                                    .min(new Document("testField", "b")
                                                             .append("_id", b1.id)));

        Assert.assertEquals("size", 4, l.size());
        Assert.assertEquals("item", b1.id, l.get(0).id);
    }

    @Entity("IndexedEntity")
    @Indexes({
        @Index(fields = @Field("testField")),
        @Index(fields = {@Field("testField"), @Field("_id")})})
    private static final class IndexedEntity {

        @Id
        private ObjectId id;
        private String testField;

        private IndexedEntity(final String testField) {
            this.testField = testField;
        }

        private IndexedEntity() {
        }
    }
}

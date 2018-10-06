package xyz.morphia.mapping;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import xyz.morphia.TestBase;
import xyz.morphia.annotations.Embedded;
import xyz.morphia.annotations.Id;

import static org.junit.Assert.fail;

public class MapWithDotInKeyTest extends TestBase {

    @Test
    public void testMapping() {
        E e = new E();
        e.mymap.put("a.b", "a");
        e.mymap.put("c.e.g", "b");

        try {
            getDatastore().save(e);
        } catch (Exception ex) {
            return;
        }

        fail("Should have got rejection for dot in field names");
        e = getDatastore().get(e);
        Assert.assertEquals("a", e.mymap.get("a.b"));
        Assert.assertEquals("b", e.mymap.get("c.e.g"));
    }

    private static class Goo {
        @Id
        private ObjectId id = new ObjectId();
        private String name;

        Goo() {
        }

        Goo(final String n) {
            name = n;
        }
    }

    private static class E {
        private final MyMap mymap = new MyMap();
        @Id
        private ObjectId id;
    }

    @Embedded
    private static class MyMap extends Document {
    }
}

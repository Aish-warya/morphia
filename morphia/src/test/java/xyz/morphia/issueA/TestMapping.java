package xyz.morphia.issueA;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import xyz.morphia.TestBase;
import xyz.morphia.annotations.Embedded;
import xyz.morphia.annotations.Id;

import java.io.Serializable;


/**
 * Test from email to mongodb-users list.
 */
public class TestMapping extends TestBase {

    @Test
    public void testMapping() {
        getMapper().map(ClassLevelThree.class);
        final ClassLevelThree sp = new ClassLevelThree();

        //Old way
        final Document wrapObj = getMapper().toDocument(sp);  //the error points here from the user
        getDatastore().getDatabase().getCollection("testColl").insertOne(wrapObj);


        //better way
        getDatastore().save(sp);

    }

    private interface InterfaceOne<K> {
        K getK();
    }

    private static class ClassLevelOne<K> implements InterfaceOne<K>, Serializable {
        private K k;

        @Override
        public K getK() {
            return k;
        }
    }

    @Embedded
    private static class ClassLevelTwo extends ClassLevelOne<String> {

    }

    private static class ClassLevelThree {
        @Id
        private ObjectId id;

        private String name;

        private ClassLevelTwo value;
    }

}

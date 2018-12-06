/*
  Copyright (C) 2010 Olafur Gauti Gudmundsson
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
  obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
  and limitations under the License.
 */


package xyz.morphia;


import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.NotSaved;

public class TestNotSaved extends TestBase {

    @Test
    public void testBasic() {
        getDatastore().save(new Normal("value"));
        Normal n = getDatastore().find(Normal.class).first();
        Assert.assertNotNull(n);
        Assert.assertNotNull(n.name);
        getDatastore().deleteOne(n);
        getDatastore().save(new NormalWithNotSaved());
        n = getDatastore().find(Normal.class).first();
        Assert.assertNotNull(n);
        Assert.assertNull(n.name);
        getDatastore().deleteOne(n);
        getDatastore().save(new Normal("value21"));
        final NormalWithNotSaved notSaved = getDatastore().find(NormalWithNotSaved.class).first();
        Assert.assertNotNull(notSaved);
        Assert.assertNotNull(notSaved.name);
        Assert.assertEquals("never", notSaved.name);
    }

    @Entity(value = "Normal", useDiscriminator = false)
    static class Normal {
        @Id
        private ObjectId id = new ObjectId();
        private String name;

        Normal(final String name) {
            this.name = name;
        }

        protected Normal() {
        }
    }

    @Entity(value = "Normal", useDiscriminator = false)
    private static class NormalWithNotSaved {
        @NotSaved
        private final String name = "never";
        @Id
        private ObjectId id = new ObjectId();
    }
}

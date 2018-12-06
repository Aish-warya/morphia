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
import org.junit.Before;
import org.junit.Test;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.mapping.validation.ConstraintViolationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestInheritanceMappings extends TestBase {


    @Before
    @Override
    public void setUp() {
        super.setUp();
        getMapper().map(Car.class, AbstractVehicle.class, FlyingCar.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testMapEntity() {
        getMapper().map(MapLike.class);
        MapLike m = new MapLike();
        m.put("Name", "Scott");
        getDatastore().save(m);
        assertNotNull(m.id);
        assertEquals(1, getDatastore().find(MapLike.class).count());

        m = getDatastore().find(MapLike.class).first();
        assertNotNull(m.id);
        assertTrue(m.containsKey("Name"));
        assertEquals("Scott", m.get("Name"));

    }

    @Test
    public void testParamEntity() {
        getMapper().map(ParameterizedEntity.class);
        ParameterizedEntity c = new ParameterizedEntity();
        c.setId("foo");
        c.b = "eh";
        c.setK(12L);
        getDatastore().save(c);
        c = getDatastore().find(ParameterizedEntity.class).filter("_id", "foo").first();
        assertNotNull(c.getId());
        assertNotNull(c.b);
        assertNotNull(c.getK());

        assertEquals("foo", c.getId());
        assertEquals("eh", c.b);
        assertEquals(12, c.getK().longValue());
        assertEquals(1, getDatastore().find(ParameterizedEntity.class).count());
    }

    @Test
    public void testParamIdEntity() {
        getMapper().map(ParameterizedIdEntity.class);
        ParameterizedIdEntity c = new ParameterizedIdEntity();
        c.setId("foo");
        getDatastore().save(c);
        c = getDatastore().find(ParameterizedIdEntity.class).filter("_id", "foo").first();
        assertNotNull(c.getId());

        assertEquals("foo", c.getId());
        assertEquals(1, getDatastore().find(ParameterizedIdEntity.class).count());
    }

    @Test
    public void testParamIdEntity2() {
        getMapper().map(ParameterizedIdEntity2.class);
        ParameterizedIdEntity2 c = new ParameterizedIdEntity2();
        c.setId("foo");
        getDatastore().save(c);
        c = getDatastore().find(ParameterizedIdEntity2.class).filter("_id", "foo").first();
        assertNotNull(c.getId());

        assertEquals("foo", c.getId());
        assertEquals(1, getDatastore().find(ParameterizedIdEntity2.class).count());
    }

    @Test
    public void testSuperclassEntity() {
        final Car c = new Car();
        getDatastore().save(c);
        assertNotNull(c.getId());

        assertEquals(1, getDatastore().find(Car.class).count());
        assertEquals(1, getDatastore().find(AbstractVehicle.class).count());

    }

    public enum VehicleClass {
        Bicycle,
        Moped,
        MiniCar,
        Car,
        Truck
    }

    private interface Vehicle {
        String getId();

        VehicleClass getVehicleClass();

        int getWheelCount();
    }

    @Entity("vehicles")
    private abstract static class AbstractVehicle implements Vehicle {
        @Id
        private ObjectId id;

        @Override
        public String getId() {
            return id.toString();
        }
    }

    private static class Car extends AbstractVehicle {
        @Override
        public VehicleClass getVehicleClass() {
            return VehicleClass.Car;
        }

        @Override
        public int getWheelCount() {
            return 4;
        }
    }

    private static class FlyingCar extends AbstractVehicle {
        @Override
        public VehicleClass getVehicleClass() {
            return VehicleClass.Car;
        }

        @Override
        public int getWheelCount() {
            return 0;
        }
    }

    public static class GenericIdPlus<T, K> {
        @Id
        private T id;
        private K k;

        public T getId() {
            return id;
        }

        public void setId(final T id) {
            this.id = id;
        }

        public K getK() {
            return k;
        }

        public void setK(final K k) {
            this.k = k;
        }
    }

    private static class ParameterizedEntity extends GenericIdPlus<String, Long> {
        private String b;
    }

    private static class GenericId<T> {
        @Id
        private T id;

        public T getId() {
            return id;
        }

        public void setId(final T id) {
            this.id = id;
        }
    }

    private static class GenericIdSub<V> extends GenericId<V> {
    }

    private static class ParameterizedIdEntity2 extends GenericIdSub<String> {
    }

    private static class ParameterizedIdEntity extends GenericId<String> {
    }

    @Entity(useDiscriminator = false)
    public static class MapLike implements Iterable<Entry<String, String>>, Map<String, String> {
        private final HashMap<String, String> realMap = new HashMap<>();
        @Id
        private ObjectId id;

        @Override
        public Iterator<Entry<String, String>> iterator() {
            return realMap.entrySet().iterator();
        }

        @Override
        public int size() {
            return realMap.size();
        }

        @Override
        public boolean isEmpty() {
            return realMap.isEmpty();
        }

        @Override
        public boolean containsKey(final Object key) {
            return realMap.containsKey(key);
        }

        @Override
        public boolean containsValue(final Object value) {
            return realMap.containsValue(value);
        }

        @Override
        public String get(final Object key) {
            return realMap.get(key);
        }

        @Override
        public String put(final String key, final String value) {
            return realMap.put(key, value);
        }

        @Override
        public String remove(final Object key) {
            return realMap.remove(key);
        }

        @Override
        public void putAll(final Map<? extends String, ? extends String> m) {
            realMap.putAll(m);
        }

        @Override
        public void clear() {
            realMap.clear();
        }

        @Override
        public Set<String> keySet() {
            return realMap.keySet();
        }

        @Override
        public Collection<String> values() {
            return realMap.values();
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            return realMap.entrySet();
        }

    }

}

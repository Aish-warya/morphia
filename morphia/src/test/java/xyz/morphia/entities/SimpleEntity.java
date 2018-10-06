package xyz.morphia.entities;

import org.bson.types.ObjectId;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;

@Entity
public class SimpleEntity {
    @Id
    private ObjectId id;

    private String name;
    private Integer integer;
}

package xyz.morphia.query.validation;

import org.bson.types.ObjectId;
import org.junit.Test;
import xyz.morphia.entities.EntityWithNoId;
import xyz.morphia.entities.SimpleEntity;
import xyz.morphia.mapping.MappedClass;
import xyz.morphia.mapping.MappedField;
import xyz.morphia.mapping.Mapper;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EntityTypeAndIdValueValidatorTest {
    @Test
    public void shouldAllowTypeThatIsAMappedEntityAndAValueWithSameClassAsIdOfMappedEntity() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("_id");

        boolean validationApplied = EntityTypeAndIdValueValidator.getInstance().apply(mappedClass, mappedField, new ObjectId(),
                                                                                      validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldNotValidateIfEntityHasNoIdField() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        MappedClass mappedClass = new MappedClass(EntityWithNoId.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("_id");
        boolean validationApplied = EntityTypeAndIdValueValidator.getInstance().apply(mappedClass, mappedField, "some non-null value",
                                                                                      validationFailures);
        // then
        assertThat(validationApplied, is(false));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldRejectValueWithATypeThatDoesNotMatchTheEntityIdFieldType() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("_id");
        boolean validationApplied = EntityTypeAndIdValueValidator.getInstance().apply(mappedClass, mappedField, "some non-ObjectId value",
                                                                                      validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(1));
    }
}

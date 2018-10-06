package xyz.morphia.mapping.validation.classrules;


import xyz.morphia.mapping.MappedClass;
import xyz.morphia.mapping.Mapper;
import xyz.morphia.mapping.validation.ClassConstraint;
import xyz.morphia.mapping.validation.ConstraintViolation;
import xyz.morphia.mapping.validation.ConstraintViolation.Level;

import java.util.Map;
import java.util.Set;

/**
 * Checks that the entity is neither a {@code Map} nor and {@code Iterable}
 */
public class EntityCannotBeMapOrIterable implements ClassConstraint {

    @Override
    public void check(final Mapper mapper, final MappedClass mc, final Set<ConstraintViolation> ve) {

        if (mc.getEntityAnnotation() != null && (Map.class.isAssignableFrom(mc.getClazz())
                                                 || Iterable.class.isAssignableFrom(mc.getClazz()))) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, getClass(), "Entities cannot implement Map/Iterable"));
        }

    }
}

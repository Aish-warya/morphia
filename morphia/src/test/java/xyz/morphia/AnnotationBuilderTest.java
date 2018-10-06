/*
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.morphia;

import org.junit.Test;
import xyz.morphia.annotations.Collation;
import xyz.morphia.annotations.Field;
import xyz.morphia.annotations.Index;
import xyz.morphia.annotations.IndexOptions;
import xyz.morphia.annotations.Indexed;
import xyz.morphia.annotations.Text;
import xyz.morphia.annotations.Validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

public class AnnotationBuilderTest {
    @Test
    public void builders() throws NoSuchMethodException {
        compareFields(Index.class, IndexBuilder.class);
        compareFields(IndexOptions.class, IndexOptionsBuilder.class);
        compareFields(Indexed.class, IndexedBuilder.class);
        compareFields(Field.class, FieldBuilder.class);
        compareFields(Collation.class, CollationBuilder.class);
        compareFields(Text.class, TextBuilder.class);
        compareFields(Validation.class, ValidationBuilder.class);
    }

    private <T extends Annotation> void compareFields(final Class<T> annotationType, final Class<? extends AnnotationBuilder<T>> builder)
        throws NoSuchMethodException {

        for (Method method : annotationType.getDeclaredMethods()) {
            Method getter = builder.getDeclaredMethod(method.getName(), method.getReturnType());
            assertNotNull(String.format("Looking for %s.%s(%s) on ", builder.getSimpleName(), method.getName(), method.getReturnType()
                                                                                                                      .getSimpleName()),
                          getter);
        }
    }

}

package org.raven.hibernate.jpa.test.repository;

import org.hibernate.annotations.GenericGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@GenericGenerator(name = "timestampGenerator", strategy = "org.raven.hibernate.entity.id.TimestampIdentifierGenerator")
@Target({PACKAGE, TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface TGenerator {
}

package org.raven.hibernate.jpa;

//import org.raven.hibernate.criteria.CriteriaQueryRoot;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import java.util.List;

@FunctionalInterface
public interface Joiner<T> {

    @Nullable
    List<Join<T, ?>> join(From<T, T> combineRoot, CriteriaBuilder builder);

}

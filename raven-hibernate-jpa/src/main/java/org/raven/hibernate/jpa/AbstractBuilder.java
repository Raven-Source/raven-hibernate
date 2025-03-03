package org.raven.hibernate.jpa;

import lombok.Getter;
import lombok.NonNull;
import org.raven.commons.util.StringUtils;
import org.raven.hibernate.util.ManagedTypeUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;

public abstract class AbstractBuilder<S, T> {

    @Getter
    protected final From<S, T> from;

    @Getter
    protected final CriteriaBuilder builder;

    public AbstractBuilder(@NonNull From<S, T> from, @NonNull CriteriaBuilder builder) {

        this.from = from;
        this.builder = builder;
    }

    @SuppressWarnings("unchecked")
    public <X> Join<T, X> getJoin(@NonNull String joinAttributeName) {
        for (Join<T, ?> join : getFrom().getJoins()) {
            if (join.getAttribute().getName().equalsIgnoreCase(joinAttributeName)) {
                return (Join<T, X>) join;
            }
        }

        throw new RuntimeException("Join not found: " + joinAttributeName);
    }

    public <Y> Path<Y> getAttribute(@NonNull String attributeName) {
        return getAttribute((EntityType<T>) from.getModel(), from, attributeName);
    }

    public <Y, X> Path<Y> getAttribute(Join<T, X> join, @NonNull String attributeName) {
        return getAttribute((EntityType<X>) join.getModel(), join, attributeName);
    }

    public <Y> Path<Y> getJoinAttribute(@NonNull String joinName, @NonNull String attributeName) {
        return getAttribute(getJoin(joinName), attributeName);
    }

    @SuppressWarnings("unchecked")
    protected <X, Y> Path<Y> getAttribute(ManagedType<X> managedType, Path<X> path, @NonNull String attributeName) {

        SingularAttribute<? super X, Y> attribute = null;

        try {
            attribute = (SingularAttribute<? super X, Y>) managedType.getSingularAttribute(attributeName);
        } catch (IllegalArgumentException ignored) {
        }

        if (attribute != null) {
            return path.get(attribute);
        } else {
            String readAttributeName = ManagedTypeUtils.getAttributeName(managedType, attributeName);
            if (StringUtils.isNotBlank(readAttributeName)) {
                return path.get(readAttributeName);
            } else {
                return path.get(attributeName);
            }
        }

    }
}

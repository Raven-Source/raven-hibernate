package org.raven.hibernate.jpa;

import lombok.Getter;
import lombok.NonNull;
import org.hibernate.metamodel.model.domain.internal.AbstractAttribute;
import org.hibernate.query.sqm.tree.from.SqmAttributeJoin;
import org.raven.commons.util.StringUtils;
import org.raven.hibernate.util.ManagedTypeUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;

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
        if (from.getModel() instanceof EntityType<T>) {
            return getAttributePath((EntityType<T>) from.getModel(), from, attributeName);
        } else if (from instanceof SqmAttributeJoin<S, T>) {
            return getAttributePath((SqmAttributeJoin<S, T>) from, attributeName);
        }

        return from.get(attributeName);
    }

    @SuppressWarnings("unchecked")
    public <Y, X> Path<Y> getAttribute(Join<T, X> join, @NonNull String attributeName) {
        if (from instanceof SqmAttributeJoin<S, T>) {
            return getAttributePath((SqmAttributeJoin<S, T>) join, attributeName);
        }

        return from.get(attributeName);
    }

    public <Y> Path<Y> getJoinAttribute(@NonNull String joinName, @NonNull String attributeName) {
        return getAttribute(getJoin(joinName), attributeName);
    }

    @SuppressWarnings("unchecked")
    protected <X, Y> Path<Y> getAttributePath(ManagedType<X> managedType, Path<X> path, @NonNull String attributeName) {

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

    protected <X, Y> Path<Y> getAttributePath(SqmAttributeJoin<S, T> attributeJoin, @NonNull String attributeName) {

        Path<Y> attribute = null;

        try {
            attribute = attributeJoin.get(attributeName);
        } catch (Exception ignored) {
        }

        if (attribute != null) {
            return attribute;
        } else if (from.getModel() instanceof AbstractAttribute) {
            ManagedType<?> managedType = ((AbstractAttribute<?, ?, ?>) from.getModel()).getDeclaringType();
            String readAttributeName = ManagedTypeUtils.getAttributeName(managedType, attributeName);
            if (StringUtils.isNotBlank(readAttributeName)) {
                return attributeJoin.get(readAttributeName);
            } else {
                return attributeJoin.get(attributeName);
            }
        }

        return null;

    }

}

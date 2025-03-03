package org.raven.hibernate.jpa;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.raven.hibernate.util.ManagedTypeUtils;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.BooleanSupplier;

/**
 * @author by yanfeng
 * date 2021/9/13 20:24
 */
public class UpdateSetBuilder<T> extends AbstractBuilder<T, T> {

    @Getter
    private CriteriaUpdate<T> update;

    public UpdateSetBuilder(CriteriaUpdate<T> update, Root<T> root, CriteriaBuilder builder) {
        super(root, builder);
        this.update = update;
    }

//    /**
//     * @param entityType
//     * @param builder
//     */
//    public UpdateSetBuilder(Class<T> entityType, CriteriaBuilder builder) {
//        super(entityType, builder);
//        this.update = builder.createCriteriaUpdate(entityType);
//    }

    /**
     * Update the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <X>       The type of value
     * @param <Y>       The type of the represented attribute
     * @return the modified update query
     */
    public <Y, X extends Y> UpdateSetBuilder<T> set(SingularAttribute<? super T, Y> attribute, X value) {

        update = update.set(attribute, value);

        return this;
    }

    /**
     * Update the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <Y>       The type of the represented attribute
     * @return the modified update query
     */
    public <Y> UpdateSetBuilder<T> set(SingularAttribute<? super T, Y> attribute, Expression<? extends Y> value) {

        update = update.set(attribute, value);

        return this;
    }

    /**
     * Update the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <X>       The type of value
     * @param <Y>       The type of the represented attribute
     * @return the modified update query
     */
    public <Y, X extends Y> UpdateSetBuilder<T> set(Path<Y> attribute, X value) {

        update = update.set(attribute, value);

        return this;
    }

    /**
     * Update the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <Y>       The type of the represented attribute
     * @return the modified update query
     */
    public <Y> UpdateSetBuilder<T> set(Path<Y> attribute, Expression<? extends Y> value) {

        update = update.set(attribute, value);

        return this;
    }

    /**
     * Update the value of the specified attribute.
     *
     * @param attributeName name of the attribute to be updated
     * @param value         new value
     * @return the modified update query
     */
    public UpdateSetBuilder<T> set(String attributeName, Object value) {

        update = update.set(getAttribute(attributeName), processCustomType(attributeName, value));
        return this;
    }

    /**
     * sum the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <X>       The type of value
     * @param <Y>       the type referenced by the path
     * @return the modified update query
     */
    public <Y extends Number, X extends Y> UpdateSetBuilder<T> sum(Path<Y> attribute, X value) {

        return set(attribute, builder.sum(attribute, value));
    }

    /**
     * sum the value of the specified attribute.
     *
     * @param attributeName name of the attribute to be updated
     * @param value         new value
     * @param <X>       The type of value
     * @return the modified update query
     */
    public <X extends Number> UpdateSetBuilder<T> sum(String attributeName, X value) {

        return sum(getAttribute(attributeName), value);
    }

    /**
     * diff the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <X>       The type of value
     * @param <Y>       the type referenced by the path
     * @return the modified update query
     */
    public <Y extends Number, X extends Y> UpdateSetBuilder<T> diff(Path<Y> attribute, X value) {

        return set(attribute, builder.diff(attribute, value));
    }

    /**
     * diff the value of the specified attribute.
     *
     * @param attributeName name of the attribute to be updated
     * @param value         new value
     * @param <X>       The type of value
     * @return the modified update query
     */
    public <X extends Number> UpdateSetBuilder<T> diff(String attributeName, X value) {

        return diff(getAttribute(attributeName), value);
    }

    /**
     * prod the value of the specified attribute.
     *
     * @param attribute attribute to be updated
     * @param value     new value
     * @param <X>       The type of value
     * @param <Y>       the type referenced by the path
     * @return the modified update query
     */
    public <Y extends Number, X extends Y> UpdateSetBuilder<T> prod(Path<Y> attribute, X value) {

        return set(attribute, builder.prod(attribute, value));
    }

    /**
     * prod the value of the specified attribute.
     *
     * @param attributeName name of the attribute to be updated
     * @param value         new value
     * @param <X>       The type of value
     * @return the modified update query
     */
    public <X extends Number> UpdateSetBuilder<T> prod(String attributeName, X value) {

        return prod(getAttribute(attributeName), value);
    }

    /**
     * @param condition           condition
     * @param updateSetExpression UpdateSetExpression
     * @return the modified update query
     */
    public UpdateSetBuilder<T> condition(boolean condition, UpdateSetExpression<T> updateSetExpression) {
        if (condition) {
            updateSetExpression.accept(this);
        }

        return this;
    }

    /**
     * @param supplier            condition
     * @param updateSetExpression UpdateSetExpression
     * @return the modified update query
     */
    public UpdateSetBuilder<T> condition(BooleanSupplier supplier, UpdateSetExpression<T> updateSetExpression) {
        if (supplier.getAsBoolean()) {
            updateSetExpression.accept(this);
        }

        return this;
    }

    protected Object processCustomType(String attributeName, Object value) {

        if (!String.class.equals(value.getClass()) && !Object.class.equals(value.getClass())) {
            AttributeDefinition attributeDefinition = ManagedTypeUtils.getAttributeDefinition((ManagedType<?>) from.getModel(), attributeName);
            if (attributeDefinition != null && attributeDefinition.getType() instanceof JsonStringType) {
                return ((JsonStringType) attributeDefinition.getType()).getJavaTypeDescriptor().toString(value);
            }
        }

        return value;
    }

}

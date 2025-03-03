package org.raven.hibernate.jpa;

import java.util.function.Consumer;

@FunctionalInterface
public interface UpdateSetExpression<T> extends Consumer<UpdateSetBuilder<T>> {
    default UpdateSet<T> toUpdateSet() {
        return (update, root, builder) -> accept(new UpdateSetBuilder<>(update, root, builder));
    }
}

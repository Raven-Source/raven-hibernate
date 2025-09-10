package org.raven.hibernate.jpa.test.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.descriptor.java.spi.JavaTypeRegistry;
import org.hibernate.type.internal.UserTypeSqlTypeAdapter;
import org.hibernate.type.spi.TypeConfiguration;
import org.raven.commons.data.StringType;
import org.raven.hibernate.convert.NumberValueType;
import org.raven.hibernate.convert.StringValueType;
import org.raven.hibernate.jpa.test.repository.Status;
import org.raven.hibernate.jpa.test.repository.StatusType;
import org.raven.hibernate.jpa.test.repository.UserType;

public class SerializableTypeContributor implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {

        TypeConfiguration typeConfig = typeContributions.getTypeConfiguration();
        JavaTypeRegistry javaTypeRegistry = typeConfig.getJavaTypeRegistry();

        // Status → NumberValueType
        typeConfig.getBasicTypeRegistry().register(
                new NumberValueType(),
                Status.class.getName(),
                StatusType.class.getName(),
                UserType.class.getName()
        );

        // Status → NumberValueType
//        var numberAdapter = new UserTypeSqlTypeAdapter<>(
//                new NumberValueType(),
//                javaTypeRegistry.getDescriptor(Status.class),
//                typeConfig
//        );
//        var statusType = new org.hibernate.type.CustomType<>(new NumberValueType(), typeConfig);
//
//        typeContributions.contributeType(statusAdapter, Status.class.getName(), StatusType.class.getName());

//        // StringType → StringValueType
//        typeConfig.getBasicTypeRegistry().register(
//                new StringValueType(), StringType.class.getName()
//        );
    }
}

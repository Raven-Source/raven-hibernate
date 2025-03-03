package org.raven.hibernate.persister;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.*;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.persister.spi.UnknownPersisterException;

@Slf4j
public class DefinePersisterClassResolver implements PersisterClassResolver {

    public DefinePersisterClassResolver() {
        log.info("load hibernate.persister.resolver: DefinePersisterClassResolver");
    }

    @Override
    public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
        // todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
        if (RootClass.class.isInstance(metadata)) {
            if (metadata.hasSubclasses()) {
                //If the class has children, we need to find of which kind
                metadata = (PersistentClass) metadata.getDirectSubclasses().next();
            } else {
                return singleTableEntityPersister();
            }
        }
        if (JoinedSubclass.class.isInstance(metadata)) {
            return joinedSubclassEntityPersister();
        } else if (UnionSubclass.class.isInstance(metadata)) {
            return unionSubclassEntityPersister();
        } else if (SingleTableSubclass.class.isInstance(metadata)) {
            return singleTableEntityPersister();
        } else {
            throw new UnknownPersisterException(
                    "Could not determine persister implementation for entity [" + metadata.getEntityName() + "]"
            );
        }
    }

    public Class<? extends EntityPersister> singleTableEntityPersister() {
        return CustomSingleTableEntityPersister.class;
    }

    public Class<? extends EntityPersister> joinedSubclassEntityPersister() {
        return JoinedSubclassEntityPersister.class;
    }

    public Class<? extends EntityPersister> unionSubclassEntityPersister() {
        return UnionSubclassEntityPersister.class;
    }

    @Override
    public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
        return metadata.isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
    }

    private Class<OneToManyPersister> oneToManyPersister() {
        return OneToManyPersister.class;
    }

    private Class<BasicCollectionPersister> basicCollectionPersister() {
        return BasicCollectionPersister.class;
    }
}


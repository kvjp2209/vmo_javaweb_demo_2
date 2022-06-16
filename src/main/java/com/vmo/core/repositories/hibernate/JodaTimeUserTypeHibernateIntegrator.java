package com.vmo.core.repositories.hibernate;

import com.vmo.core.repositories.hibernate.types.LocalDateTimeUserType;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.UserType;

public class JodaTimeUserTypeHibernateIntegrator implements Integrator {
    private static UserType[] userTypes = new UserType[] {
            new LocalDateTimeUserType()
    };

    @Override
    public void integrate(
            Metadata metadata,
            SessionFactoryImplementor sessionFactoryImplementor,
            SessionFactoryServiceRegistry sessionFactoryServiceRegistry
    ) {
        if (metadata instanceof MetadataImplementor) {
//            String isEnabled = (String)sessionFactoryImplementor.getProperties().get("core.usertype.joda.enable");

//            if (isEnabled != null && Boolean.valueOf(isEnabled)) {
                //type resolver will be different in hibernate 6
                TypeResolver typeResolver = ((MetadataImplementor) metadata).getTypeResolver();
                for (UserType userType : userTypes) {
                    typeResolver.registerTypeOverride(userType, new String[] { userType.returnedClass().getName() });
                }

//            }
        } else {
            throw new IllegalArgumentException("Metadata was not assignable to MetadataImplementor: " + metadata.getClass());
        }
    }

    @Override
    public void disintegrate(
            SessionFactoryImplementor sessionFactoryImplementor,
            SessionFactoryServiceRegistry sessionFactoryServiceRegistry
    ) {
    }
}

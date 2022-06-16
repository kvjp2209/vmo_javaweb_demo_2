package com.vmo.core.repositories.hibernate;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CoreIntegratorProvider implements IntegratorProvider {
    @Override
    public List<Integrator> getIntegrators() {
        return Arrays.asList(
                new JodaTimeUserTypeHibernateIntegrator()
        );
    }
}

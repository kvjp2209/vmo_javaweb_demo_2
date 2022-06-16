package com.vmo.core.common.config.auto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.config.scheduler.JobSchedulerProperties;
import com.vmo.core.repositories.scheduler.JpaJobRepository;
import com.vmo.core.repositories.scheduler.JpaJobRepositoryBean;
import org.springframework.batch.core.configuration.annotation.SimpleBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JpaBatchConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(
        prefix = CommonConstants.CONFIG_SCHEDULER,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
@EnableConfigurationProperties({JobSchedulerProperties.class})
@AutoConfigureBefore({ SimpleBatchConfiguration.class, BatchAutoConfiguration.class })
@ComponentScan({
        "com.vmo.core.scheduler"
})
public class JpaSchedulerConfiguration extends JpaBatchConfigurer {
    private final BatchProperties properties;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JobSchedulerProperties jobSchedulerProperties;

    protected JpaSchedulerConfiguration(
            BatchProperties properties,
            DataSource dataSource, @BatchDataSource ObjectProvider<DataSource> batchDataSource,
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers,
            EntityManagerFactory entityManagerFactory
    ) {
        super(properties, batchDataSource.getIfAvailable(() -> dataSource), transactionManagerCustomizers.getIfAvailable(), entityManagerFactory);

        this.properties = properties;
        this.dataSource = batchDataSource.getIfAvailable(() -> dataSource);
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    @Bean
    @Primary //simply override bean SimpleBatchConfiguration.jobRepository() too much effort to custom every configuration of Batch
    protected JpaJobRepository createJobRepository() throws Exception {
        JpaJobRepositoryBean factory = new JpaJobRepositoryBean(objectMapper, jobSchedulerProperties);
        PropertyMapper map = PropertyMapper.get();
        map.from(this.dataSource).to(factory::setDataSource);
        map.from(this::determineIsolationLevel).whenNonNull().to(factory::setIsolationLevelForCreate);

        //BatchProperties.Jdbc jdbc = this.properties.getJdbc(); //TODO new version of spring boot getJdbc(), 2.3 doesnt
        map.from(properties::getTablePrefix).whenHasText().to(factory::setTablePrefix);

        map.from(this::getTransactionManager).to(factory::setTransactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}

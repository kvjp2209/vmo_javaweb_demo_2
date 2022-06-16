package com.vmo.core.repositories.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.config.scheduler.JobSchedulerProperties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.support.PropertiesConverter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

public class JpaJobRepositoryBean extends JobRepositoryFactoryBean {
    private DataSource dataSource;
    private JdbcOperations jdbcOperations;
    private String databaseType;
    private String tablePrefix = "BATCH_";
    private DataFieldMaxValueIncrementerFactory incrementerFactory;

    private String isolationLevelForCreate = "ISOLATION_SERIALIZABLE";
    private boolean validateTransactionState = true;

    private ObjectMapper objectMapper;
    private JobSchedulerProperties jobSchedulerProperties;

    private ProxyFactory proxyFactory;
    private JpaJobInstanceRepository jobInstanceDao;

    public JpaJobRepositoryBean(ObjectMapper objectMapper, JobSchedulerProperties jobSchedulerProperties) {
        this.objectMapper = objectMapper;
        this.jobSchedulerProperties = jobSchedulerProperties;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        super.setDataSource(dataSource);
    }

    public void setValidateTransactionState(boolean validateTransactionState) {
        this.validateTransactionState = validateTransactionState;
        super.setValidateTransactionState(validateTransactionState);
    }

    public void setIsolationLevelForCreate(String isolationLevelForCreate) {
        this.isolationLevelForCreate = isolationLevelForCreate;
        super.setIsolationLevelForCreate(isolationLevelForCreate);
    }

    @Override
    public Class getObjectType() {
        return JpaJobRepository.class;
    }

    @Override
    public JpaJobRepository getObject() throws Exception {
        this.proxyFactory = new ProxyFactory();
        TransactionInterceptor advice = new TransactionInterceptor(
                getTransactionManager(),
                PropertiesConverter.stringToProperties("create*=PROPAGATION_REQUIRES_NEW," +
                        this.isolationLevelForCreate + "\ngetLastJobExecution*=PROPAGATION_REQUIRES_NEW," +
                        this.isolationLevelForCreate + "\n*=PROPAGATION_REQUIRED"));
        if (this.validateTransactionState) {
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MethodInterceptor() {
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    if (TransactionSynchronizationManager.isActualTransactionActive()) {
                        throw new IllegalStateException("Existing transaction detected in JobRepository. Please fix this and try again (e.g. remove @Transactional annotations from client).");
                    } else {
                        return invocation.proceed();
                    }
                }
            });
            NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
            pointcut.addMethodName("create*");
//            pointcut.addMethodName("update*");
            advisor.setPointcut(pointcut);
            this.proxyFactory.addAdvisor(advisor);
        }

        this.proxyFactory.addAdvice(advice);
        this.proxyFactory.setProxyTargetClass(true);
        this.proxyFactory.setTargetClass(JpaJobRepository.class);
//        this.proxyFactory.addInterface(JobRepository.class);
        this.proxyFactory.setTarget(new JpaJobRepository(
                createJobInstanceDao(), createJobExecutionDao(), createStepExecutionDao(), createExecutionContextDao(),
                objectMapper, jobSchedulerProperties
        ));

        return (JpaJobRepository)this.proxyFactory.getProxy(this.getClass().getClassLoader());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcOperations = new JdbcTemplate(this.dataSource);
        setJdbcOperations(jdbcOperations);

        incrementerFactory = new DefaultDataFieldMaxValueIncrementerFactory(this.dataSource);
        setIncrementerFactory(incrementerFactory);

        databaseType = DatabaseType.fromMetaData(this.dataSource).name();
        setDatabaseType(databaseType);

        setTablePrefix(tablePrefix);

        super.afterPropertiesSet();
    }

    @Override
    protected JpaJobInstanceRepository createJobInstanceDao() throws Exception {
        jobInstanceDao = new JpaJobInstanceRepository();
        jobInstanceDao.setJdbcTemplate(this.jdbcOperations);
        jobInstanceDao.setJobIncrementer(this.incrementerFactory.getIncrementer(this.databaseType, this.tablePrefix + "JOB_SEQ"));
        jobInstanceDao.setTablePrefix(this.tablePrefix);
        jobInstanceDao.afterPropertiesSet();

        return jobInstanceDao;
    }
}

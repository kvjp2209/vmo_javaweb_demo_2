package com.vmo.core;

import com.vmo.core.sync.MyHouse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

//public class CoreSpringApplication extends SpringApplication {
//
//    public CoreSpringApplication(Class<?>... primarySources) {
//        super((ResourceLoader) null, primarySources);
//    }
//
//    @Override
//    public ConfigurableApplicationContext run(String... args) {
//        CoreUtils.setSpringApplication(true);
//
//        return super.run(args);
//    }
//
//    public static ConfigurableApplicationContext run(Class primarySource, String... args) {
//        return new CoreSpringApplication(new Class[]{primarySource}).run(args);
//    }
//}


@SpringBootApplication
@SecurityScheme(name = "javainuseapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@EnableCaching
public class CoreSpringApplication {
    @Autowired
    MyHouse myHouse;

    private static final Logger LOGGER = LogManager.getLogger(CoreSpringApplication.class);


    public static void main(String[] args) {
//        LOGGER.info("Sample info message");
        SpringApplication.run(CoreSpringApplication.class, args);
        LOGGER.info("Info level log message");
        LOGGER.debug("Debug level log message");
        LOGGER.error("Error level log message");
    }


//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return args -> {
//
//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
//
//        };
//    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            System.out.println(Thread.currentThread().getName() + ": Loda đi tới cửa nhà !!!");
            System.out.println(Thread.currentThread().getName() + ": => Loda bấm chuông và khai báo họ tên!");
            // gõ cửa
            myHouse.rangDoorbellBy("Loda");
            System.out.println(Thread.currentThread().getName() +": Loda quay lưng bỏ đi");
        };
    }
}

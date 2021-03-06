package io.fanyun.kettle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySources({@PropertySource("classpath:/config/app/app-common.properties"),
        @PropertySource("classpath:/config/app/app-${spring.profiles.active}.properties")})
public class KettleManagerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(KettleManagerApplication.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(KettleManagerApplication.class);
    }
}

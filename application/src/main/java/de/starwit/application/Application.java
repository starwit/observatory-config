package de.starwit.application;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

import de.starwit.persistence.repository.CustomRepositoryImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main SpringApplication to start the whole project
 */
@SpringBootApplication(scanBasePackages = {
        "de.starwit.rest",
        "de.starwit.service",
        "de.starwit.persistence",
        "de.starwit.application.config"
})
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
@EnableAsync
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(new Class[] { Application.class }, args);
    }

    @Bean
    public ObjectMapper mapper() {
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("filterId", SimpleBeanPropertyFilter.filterOutAllExcept("id"));
        filterProvider.addFilter("filterIdName", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "title"));
        filterProvider.addFilter("filterCamera", SimpleBeanPropertyFilter.filterOutAllExcept("id", "saeStreamKey"));
        return JsonMapper.builder()
                .filterProvider(filterProvider)
                .build();
    }
}

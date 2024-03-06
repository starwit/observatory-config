package de.starwit.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.starwit.persistence.repository.CustomRepositoryImpl;

@SpringBootApplication(scanBasePackages = "de.starwit.persistence")
@EnableJpaRepositories (repositoryBaseClass = CustomRepositoryImpl.class)
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

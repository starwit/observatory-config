package de.starwit.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.starwit.persistence.repository.CustomRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories (repositoryBaseClass = CustomRepositoryImpl.class)
public class PersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceApplication.class, args);
    }

}

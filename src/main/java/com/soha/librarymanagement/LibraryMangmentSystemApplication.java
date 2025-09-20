package com.soha.librarymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableJpaAuditing
@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)  // <-- required for @PreAuthorize
public class LibraryMangmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryMangmentSystemApplication.class, args);
    }

}

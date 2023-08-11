package com.example.securebusiness;

import com.example.securebusiness.model.Role;
import com.example.securebusiness.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static com.example.securebusiness.enums.RoleType.*;

@SpringBootApplication
public class SecureBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureBusinessApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
        return args -> {

//            Role roleUser = new Role();
//            roleUser.setName(ROLE_USER.name());
//            roleUser.setPermission("READ:USER, READ:CUSTOMER");
//            roleRepository.save(roleUser);
//            Role roleManager = new Role();
//            roleManager.setName(ROLE_MANAGER.name());
//            roleManager.setPermission("READ:USER, READ:CUSTOMER, UPDATE:USER, UPDATE:CUSTOMER");
//            roleRepository.save(roleManager);
//            Role roleAdmin = new Role();
//            roleAdmin.setName(ROLE_ADMIN.name());
//            roleAdmin.setPermission("READ:USER, READ:CUSTOMER, UPDATE:USER, UPDATE:CUSTOMER, CREATE:USER, CREATE:CUSTOMER");
//            roleRepository.save(roleAdmin);
//            Role roleSysAdmin = new Role();
//            roleSysAdmin.setName(ROLE_SYSADMIN.name());
//            roleSysAdmin.setPermission("READ:USER, READ:CUSTOMER, UPDATE:USER, UPDATE:CUSTOMER, CREATE:USER, CREATE:CUSTOMER, DELETE:USER, DELETE:CUSTOMER");
//            roleRepository.save(roleSysAdmin);
        };
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
//        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Jwt-Token", "Authorization", "Origin", "Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "File-Name"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}

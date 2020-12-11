package com.ms.customerservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner start(
            CustomerRepository customerRepository,
            RepositoryRestConfiguration restConfiguration){
        restConfiguration.exposeIdsFor(Customer.class);
        return args -> {
            customerRepository.save(new Customer(null,"OULAHYANE Kaoutar","kaoutar.oulahyane@gmail.com"));
            customerRepository.save(new Customer(null,"OULAHYANE Zaina","zaina.oulahyane@gmail.com"));
            customerRepository.findAll().forEach(customer -> System.out.println(customer));
        };
    }
}

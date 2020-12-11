package com.ms.billingservice;

import com.ms.billingservice.entities.Bill;
import com.ms.billingservice.entities.ProductItem;
import com.ms.billingservice.feign.CustomerRestClient;
import com.ms.billingservice.feign.ProductItemRestClient;
import com.ms.billingservice.model.Customer;
import com.ms.billingservice.model.Product;
import com.ms.billingservice.repositories.BillRepository;
import com.ms.billingservice.repositories.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(
            BillRepository billRepository,
            CustomerRestClient customerRestClient,
            ProductItemRepository productItemRepository,
            ProductItemRestClient productItemRestClient){

        return args -> {
            Customer customer = customerRestClient.getCustomerById(1L);
            System.out.println(customer);
            Product product = productItemRestClient.getProductById(1L);
            System.out.println(product );
            Bill bill1 = new Bill(null, new Date(),null,customer.getId(),null);
            billRepository.save(bill1);
            PagedModel<Product> productPagedModel = productItemRestClient.pageProducts(1,1);
            productPagedModel.forEach( p -> {
                ProductItem productItem = new ProductItem();
                productItem.setPrice(p.getPrice());
                productItem.setQuantity(1+new Random().nextInt(100));
                productItem.setProductID(p.getId());
                productItem.setBill(bill1);
                System.out.println(productItem);
                productItemRepository.save(productItem);
            });
        };
    }
}

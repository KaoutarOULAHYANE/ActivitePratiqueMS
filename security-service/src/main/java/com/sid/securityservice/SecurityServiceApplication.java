package com.sid.securityservice;

import com.sid.securityservice.dao.entities.AppRole;
import com.sid.securityservice.dao.entities.AppUser;
import com.sid.securityservice.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityServiceApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        /*BCrypt est un algo pour le hachage des mots de passe*/
        /*non pas symétrique */
        /*plus sécurisé que MD5*/
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner start(AccountService accountService){
        return args -> {

            AppUser appUser1 = new AppUser(null,"OULAHYANE","1234",null);
            AppUser appUser2 = new AppUser(null,"CHAKIR","1234",null);
            AppUser appUser3 = new AppUser(null,"USER","1234",null);

            AppRole appRole1 = new AppRole(null,"USER");
            AppRole appRole2 = new AppRole(null,"ADMIN");
            AppRole appRole3 = new AppRole(null,"CUSTOMER_MANAGER");
            AppRole appRole4 = new AppRole(null,"PRODUCT_MANAGER");
            AppRole appRole5 = new AppRole(null,"BILL_MANAGER");

            accountService.addNewUser(appUser1);
            accountService.addNewUser(appUser2);

            accountService.addNewRole(appRole1);
            accountService.addNewRole(appRole2);
            accountService.addNewRole(appRole3);
            accountService.addNewRole(appRole4);

            accountService.addRoleToUser(appRole3.getName(),appUser1.getUsername());
            accountService.addRoleToUser(appRole2.getName(),appUser1.getUsername());
            accountService.addRoleToUser(appRole4.getName(),appUser1.getUsername());
            accountService.addRoleToUser(appRole4.getName(),appUser2.getUsername());
        };
    }

}

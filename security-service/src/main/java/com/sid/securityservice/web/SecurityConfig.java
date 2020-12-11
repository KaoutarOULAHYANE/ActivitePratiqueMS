package com.sid.securityservice.web;

import com.sid.securityservice.dao.entities.AppUser;
import com.sid.securityservice.filters.JwtAuthenticationFilter;
import com.sid.securityservice.filters.JwtAuthorizationFilter;
import com.sid.securityservice.service.AccountService;
import com.sid.securityservice.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailServiceImpl userDetailService;
    private AccountService accountService;

    public SecurityConfig(UserDetailServiceImpl userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*Propre méthode d'Authentication*/
        /*Une fois l'utilisateur saisie son username et son password, le serveur fait appel à loadUserByUsername()*/
        /*auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                AppUser appUser = accountService.loadUserByUsername(username);
                Collection<GrantedAuthority> authorities =
                        appUser.getAppRoles().stream()
                        .map(appRole -> new SimpleGrantedAuthority(appRole.getName())).collect(Collectors.toList());
                User user = new User(appUser.getUsername(), appUser.getPassword(), authorities);
                return user;
            }
        });*/
        auth.userDetailsService(userDetailService);
    }

    /*Spécifier les droits d'accès*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*Par défaut, Spring Security applique l'authentification statfull*/
        /*il active la protection CSRF*/
        /*cette config, empeche d'accéder à la base de données*/
        /*le formulaire remplie n'a pas le champs caché SynchronizerToken -> refus d'accès à la BD*/
        http.csrf().disable();
        /*Spring Security par défaut n'autorise pas l'utiliation des frames ( une sorte de faille )*/
        /*Alors que H2 est une page JSP qui a des frames*/
        http.headers().frameOptions().disable();
        /*Autoriser l'accès à toutes les fonctionnalités sans authentification*/
        /*http.authorizeRequests().anyRequest().permitAll();*/

        /*http.authorizeRequests().antMatchers(HttpMethod.POST,"/appUsers/**")
                .hasAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/appUsers/**")
                .hasAuthority("USER");*/
        http.authorizeRequests().antMatchers("/h2-console/**","/refreshToken/**","/login/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        /*Par défault le mode stateless qui est activé, donc il fait demander d'afficher le formulaire*/
        /*Si utilisateur n'est authentifié, afficher ce form*/
        /*http.formLogin();*/

        /*Authentification statless*/
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilter(new JwtAuthenticationFilter(authenticationManager()));
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}

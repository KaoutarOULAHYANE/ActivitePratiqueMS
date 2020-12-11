package com.sid.securityservice.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sid.securityservice.dao.entities.AppRole;
import com.sid.securityservice.dao.entities.AppUser;
import com.sid.securityservice.dto.RoleUserForm;
import com.sid.securityservice.service.AccountService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountRestController {

    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/appUsers")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppUser> appUsers(){
        return accountService.getUsers();
    }

    @PostMapping(path = "/appUsers")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppUser saveUser(@RequestBody AppUser appUser){
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "/appRoles")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    public void saveRole(@RequestBody RoleUserForm roleUserForm){
        accountService.addRoleToUser(
                roleUserForm.getRoleName(), roleUserForm.getUsername()
        );
    }

    @GetMapping(path = "/refreshToken")
    public void refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String authToken = servletRequest.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer")){
            try{
                /*Ignorer les premiers 7 caractères du JWT "Bearer "*/
                String jwt = authToken.substring(JWTUtil.PREFIX.length()+1);
                Algorithm algorithm = Algorithm.HMAC256("mySecret1234");
                JWTVerifier jwtVerifier =
                        JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String username = decodedJWT.getSubject();

                AppUser appUser = accountService.loadUserByUsername(username);
                String accessToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXPIRE_ACCESS_TOKEN))
                        .withIssuer(servletRequest.getRequestURL().toString())
                        .withClaim("roles",appUser.getAppRoles().stream().map(
                                role -> role.getName()
                        ).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String,String> idToken = new HashMap<>();
                idToken.put("access-token",accessToken);
                idToken.put("refresh-token",jwt);

                /*Convertir en format JSON*/
                new ObjectMapper().writeValue(servletResponse.getOutputStream(),idToken);
            }
            catch (Exception e){
                /*servletResponse.setHeader("error-message",e.getMessage());
                servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);*/
                throw new RuntimeException(e.getMessage());
            }
        }
        else {
            throw new RuntimeException("Refresh T oken required !");
        }
    }

    @GetMapping(path = "/profile")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppUser profile(Principal principal){
        return accountService.loadUserByUsername(principal.getName());
    }
}


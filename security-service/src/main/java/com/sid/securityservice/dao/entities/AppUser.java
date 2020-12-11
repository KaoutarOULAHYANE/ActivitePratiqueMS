package com.sid.securityservice.dao.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor @AllArgsConstructor @Data @ToString

@Entity
public class AppUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 30, unique = true)
    private String username;
    /*Pour protéger le mot de passe*/
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /*A user can have many roles*/
    /*Mode LAZY : si je charge User, il ne va pas charger automatiquement appRoles en mémoire */
    /*Mode EAGER : si je charge User, il va charger automatiquement appRoles en mémoire*/
    /*Prob de EAGER : risque de charger une grande clollection ( bcp de données unitules ) */
    /*Avec EAGER, il faut initial la collection*/
    /*Pour éviter l'error de type NullPointerError*/
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppRole> appRoles = new ArrayList<>();
}

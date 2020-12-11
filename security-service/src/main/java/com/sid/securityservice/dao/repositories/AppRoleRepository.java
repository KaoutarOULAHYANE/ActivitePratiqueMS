package com.sid.securityservice.dao.repositories;

import com.sid.securityservice.dao.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation
        .RepositoryRestResource;

@RepositoryRestResource
public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    AppRole findAppRoleByName(String name);
}

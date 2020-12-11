package com.sid.securityservice.service;

import com.sid.securityservice.dao.entities.AppRole;
import com.sid.securityservice.dao.entities.AppUser;

import java.util.List;

public interface AccountService {
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String roleName, String username);
    AppUser loadUserByUsername(String username);
    List<AppUser> getUsers();
}

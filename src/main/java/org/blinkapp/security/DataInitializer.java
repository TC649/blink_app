package org.blinkapp.security;

import jakarta.annotation.PostConstruct;
import org.blinkapp.entity.Permission;
import org.blinkapp.entity.Role;
import org.blinkapp.enums.ERole;
import org.blinkapp.repository.PermissionRepository;
import org.blinkapp.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public DataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @PostConstruct
    public void seedRolesAndPermissions() {
        if(roleRepository.findByName(ERole.ADMIN).isPresent()) return;

        Permission readUser = permissionRepository.save(new Permission(null, "READ_USER"));
        Permission deletePost = permissionRepository.save(new Permission(null, "DELETE_POST"));

        Role admin = new Role();
        admin.setName(ERole.ADMIN);
        admin.setPermissions(Set.of(deletePost));
        roleRepository.save(admin);

        Role developer = new Role();
        developer.setName(ERole.DEVELOPER);
        developer.setPermissions(Set.of(readUser));
        developer.setPermissions(Set.of(deletePost));
        roleRepository.save(developer);


    }
}

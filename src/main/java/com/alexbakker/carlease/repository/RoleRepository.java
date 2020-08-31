package com.alexbakker.carlease.repository;

import com.alexbakker.carlease.Security.RoleType;
import com.alexbakker.carlease.Security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByType(RoleType type);
}
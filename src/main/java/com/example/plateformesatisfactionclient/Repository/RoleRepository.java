package com.example.plateformesatisfactionclient.Repository;

import com.example.plateformesatisfactionclient.Entity.ERole;
import com.example.plateformesatisfactionclient.Entity.Role;
import com.example.plateformesatisfactionclient.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}

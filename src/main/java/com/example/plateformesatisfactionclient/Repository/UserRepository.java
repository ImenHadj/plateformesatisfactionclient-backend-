package com.example.plateformesatisfactionclient.Repository;

import com.example.plateformesatisfactionclient.Entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.plateformesatisfactionclient.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   // Optional<User> findByUsername(String username);

    Optional<User> findByUsername(String username);



    User findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    public List<User> findByRoles_Name(ERole role);


}
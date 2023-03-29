package com.shamine.teamsmessagingbackend.repositories;

import com.shamine.teamsmessagingbackend.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(String email);

    User findByUserId(int userId);

    List<User> findFirst20ByUsernameContainingOrNameContainingOrderByUsernameAsc(String username, String name);

    List<User> findAllByUserIdIn(List<Integer> userIds);
}

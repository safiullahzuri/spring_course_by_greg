package com.example.truedemo.repositories;

import com.example.truedemo.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByUsername(String userName);
}

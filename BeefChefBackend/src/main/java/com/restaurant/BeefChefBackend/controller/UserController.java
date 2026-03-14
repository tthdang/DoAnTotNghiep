package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.UserCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.UserUpdateRequest;
import com.restaurant.BeefChefBackend.entity.User;
import com.restaurant.BeefChefBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping()
    public User created (@RequestBody UserCreateRequest request){
        return service.createUser(request);
    }

    @GetMapping()
    public List<User> getUsers(){
        return service.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId){
        return service.getUser(userId);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Integer  userId, @RequestBody UserUpdateRequest request){
        return service.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Integer userId){
        service.deleteUser(userId);
        return "User has been deleted!";
    }

}

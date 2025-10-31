package com.ra2.users.spring_jdbc_users.model.repository.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ra2.users.spring_jdbc_users.model.User;
import com.ra2.users.spring_jdbc_users.model.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @PostMapping("/users")
    public String createUser(@RequestBody User u) {
        int res = userRepository.save(u);
        return String.format("Has afegit %d registre", res);
    }

    @PostMapping("/users/batch")
    public String insertUsersBatch() {
        int numReg = userRepository.insertUsersBatch();
        return String.format("Has afegit %d registres", numReg);
    }

    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User u) {
        int res = userRepository.update(id, u);
        if (res > 0) {
            return "Usuari actualitzat correctament";
        } else {
            return "No s’ha trobat cap usuari amb aquest ID";
        }
    }

    @PatchMapping("/users/{id}/name")
    public String updateUserName(@PathVariable Long id, @RequestParam String name) {
        int res = userRepository.updateName(id, name);
        if (res > 0) {
            return "Nom actualitzat correctament";
        } else {
            return "No s’ha trobat cap usuari amb aquest ID";
        }
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        int res = userRepository.delete(id);
        if (res > 0) {
            return "Usuari eliminat correctament";
        } else {
            return "No s’ha trobat cap usuari amb aquest ID";
        }
    }
}

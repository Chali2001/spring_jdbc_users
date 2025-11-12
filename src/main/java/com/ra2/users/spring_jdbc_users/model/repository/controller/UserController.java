package com.ra2.users.spring_jdbc_users.model.repository.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ra2.users.spring_jdbc_users.model.User;
import com.ra2.users.spring_jdbc_users.model.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ra2.users.spring_jdbc_users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/users")
    public String createUser(@RequestBody User u) {
        int res = userService.create(u);
        return String.format("Has afegit %d registre", res);
    }

    @PostMapping("/users/batch")
    public String insertUsersBatch() {
        int numReg = userService.insertUsersBatch();
        return String.format("Has afegit %d registres", numReg);
    }

    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User u) {
        int res = userService.update(id, u);
        if (res > 0) {
            return "Usuari actualitzat correctament";
        } else {
            return "No s’ha trobat cap usuari amb aquest ID";
        }
    }

    @PatchMapping("/users/{id}/name")
    public String updateUserName(@PathVariable Long id, @RequestParam String name) {
        int res = userService.updateName(id, name);
        if (res > 0) {
            return "Nom actualitzat correctament";
        } else {
            return "No s’ha trobat cap usuari amb aquest ID";
        }
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        int res = userService.delete(id);
        if (res > 0) {
            return "Usuari eliminat correctament";
        } else {
            return "No s’ha trobat cap usuari amb aquest ID";
        }
    }

    @PostMapping("/users/{user_id}/image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long user_id,
            @RequestParam MultipartFile imageFile) {

        try {
            String url = userService.uploadImage(user_id, imageFile);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

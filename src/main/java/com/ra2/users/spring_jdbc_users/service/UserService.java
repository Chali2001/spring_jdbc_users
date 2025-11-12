package com.ra2.users.spring_jdbc_users.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.ra2.users.spring_jdbc_users.model.User;
import com.ra2.users.spring_jdbc_users.model.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id);
    }

    public int create(User u) {
        return userRepository.save(u);
    }

    public int update(Long id, User u) {
        return userRepository.update(id, u);
    }

    public int updateName(Long id, String name) {
        return userRepository.updateName(id, name);
    }

    public int delete(Long id) {
        return userRepository.delete(id);
    }

    public String uploadImage(Long userId, MultipartFile imageFile) throws IOException {

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("Usuari no trobat");
        }

        Path uploadDir = Paths.get("uploads/images");
        Files.createDirectories(uploadDir);

        String fileName = "user_" + userId + "_" + imageFile.getOriginalFilename();
        Path finalPath = uploadDir.resolve(fileName);

        try (InputStream input = imageFile.getInputStream()) {
            Files.copy(input, finalPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String imagePath = "/images/" + fileName;
        userRepository.updateImagePath(userId, imagePath);

        return imagePath;
    }
    public int insertUsersBatch() {
        return userRepository.insertUsersBatch();
    }
}

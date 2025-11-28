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

    public int processCsv(MultipartFile csvFile) throws IOException {
        List<String> lines = Files.readAllLines(Files.createTempFile("tempCSV", ".csv"));
        Path tempCsv = Files.createTempFile("users_temp", ".csv");
        Files.copy(csvFile.getInputStream(), tempCsv, StandardCopyOption.REPLACE_EXISTING);

        List<String> rows = Files.readAllLines(tempCsv);
        int totalInserted = 0;

        for(String row : rows) {
            String[] parts = row.split(",");

            User u = new User();
            u.setName(parts[0]);
            u.setDescription(parts[1]);
            u.setEmail(parts[2]);
            u.setPassword(parts[3]);
            u.setUltimAcces(null);
            u.setImagePath(null);

            totalInserted += userRepository.save(u);
        }

        Path processedDir = Paths.get("csv_processed");
        Files.createDirectories(processedDir);

        Path finalLocation = processedDir.resolve(csvFile.getOriginalFilename());
        Files.copy(csvFile.getInputStream(), finalLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return totalInserted;
    }

    public int processJson(MultipartFile jsonFile) throws IOException {
        Path tempJson = Files.createTempFile("users_json_temp", ".json");
        Files.copy(jsonFile.getInputStream(), tempJson, StandardCopyOption.REPLACE_EXISTING);
        String content = Files.readString(tempJson);
        content = content.replace("\n", "").replace("\r", "").trim();

        if(!content.contains("\"control\":\"OK\"")) {
            throw new RuntimeException("Control no és OK");
        }
        int count = Integer.parseInt(content.split("\"count\":")[1].split(",")[0].trim());

        String usersBlock = content.split("\"users\":")[1];
        usersBlock = usersBlock.substring(usersBlock.indexOf("["));
        usersBlock = usersBlock.substring(1, usersBlock.indexOf("]")).trim();

        String[] userLines = usersBlock.split("\\},\\s*\\{");        if (userLines.length != count) {
            throw new RuntimeException("El count no coincideix amb el número d'usuaris");
        }
        int totalInserted = 0;

        for (String ustr : userLines) {
            ustr = ustr.replace("{", "").replace("}", "");
            String[] fields = ustr.split(",");
            User u = new User();
            u.setName(fields[0].split(":")[1].replace("\"", ""));                         
            u.setDescription(fields[1].split(":")[1].replace("\"", ""));                    
            u.setEmail(fields[2].split(":")[1].replace("\"", ""));                         
            u.setPassword(fields[3].split(":")[1].replace("\"", ""));                      
            u.setUltimAcces(null);                                          
            u.setImagePath(null); 
            totalInserted += userRepository.save(u);
        }

        Path processedDir = Paths.get("json_processed");
        Files.createDirectories(processedDir);

        Path finalLocation = processedDir.resolve(jsonFile.getOriginalFilename());
        Files.copy(jsonFile.getInputStream(), finalLocation, StandardCopyOption.REPLACE_EXISTING);
        return totalInserted;
    }
}

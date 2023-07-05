package io.github.carlinhoshk.APITV.controller;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.github.carlinhoshk.APITV.model.user.AuthenticationDTO;
import io.github.carlinhoshk.APITV.model.user.LoginResponseDTO;
import io.github.carlinhoshk.APITV.model.user.RegisterDTO;
import io.github.carlinhoshk.APITV.model.user.UserModel;
import io.github.carlinhoshk.APITV.model.video.Video;
import io.github.carlinhoshk.APITV.repository.UserRepository;
import io.github.carlinhoshk.APITV.repository.VideoRepository;
import io.github.carlinhoshk.APITV.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("video")
public class VideoController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository VideoRepository;

    @Value("${azure.storage.connection-string}")
    private String azureConnectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;


    @PostMapping(value = "/username-video")
    public String username(@RequestBody @Valid AuthenticationDTO data) {
        var user = this.userRepository.findByLogin(data.login());
        return user.getUsername();
    }


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadVideo(@RequestBody @Valid AuthenticationDTO data, @RequestParam("file") MultipartFile file) {
        try {
            var user = this.userRepository.findByLogin(data.login());
            var login = user.getUsername();

            if (login == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
            }

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(azureConnectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            containerClient.createIfNotExists();

            String blobName = generateBlobName(login, file.getOriginalFilename());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize());

            String blobUrl = blobClient.getBlobUrl();

            Video video = new Video();
            video.setUserId(login);
            video.setUrl(blobUrl);

            this.VideoRepository.save(video);

            return ResponseEntity.ok(blobUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private String generateBlobName(String login, String filename) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);

        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();

        String blobName = login + "_" + timestamp + "_" + uniqueId + "_" + filename;
        return blobName;

    }

}


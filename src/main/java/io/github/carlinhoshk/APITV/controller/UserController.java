package io.github.carlinhoshk.APITV.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class UserController {

    @Autowired
    private VideoRepository videoRepository;

    @Value("${azure.storage.connection-string}")
    private String azureConnectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("name") String name,
                                              @RequestParam("title") String title) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(azureConnectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            containerClient.createIfNotExists();

            String blobName = generateBlobName(userId, file.getOriginalFilename());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize());

            String blobUrl = blobClient.getBlobUrl();

            Video video = new Video();
            video.setName(name);
            video.setTitle(title);
            video.setUserId(userId);
            video.setBlobName(blobName);
            video.setBlobUrl(blobUrl);

            videoRepository.save(video);

            return ResponseEntity.ok(blobUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String generateBlobName(String userId, String filename) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);

        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();

        return userId + "_" + timestamp + "_" + uniqueId + "_" + filename;
    }
}

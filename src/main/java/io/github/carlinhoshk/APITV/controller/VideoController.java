package io.github.carlinhoshk.APITV.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.github.carlinhoshk.APITV.model.user.UserModel;
import io.github.carlinhoshk.APITV.model.video.Video;
import io.github.carlinhoshk.APITV.repository.UserRepository;
import io.github.carlinhoshk.APITV.repository.VideoRepository;
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
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${azure.storage.connection-string}")
    private String azureConnectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("title") String title) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            UserModel user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
            }

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(azureConnectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            containerClient.createIfNotExists();

            String blobName = generateBlobName(user.getId(), file.getOriginalFilename());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize());

            String blobUrl = blobClient.getBlobUrl();

            Video video = new Video();
            video.setName(title);
            video.setUserId(user.getId());
            video.setUrl(blobUrl);

            videoRepository.save(video);

            return ResponseEntity.ok(blobUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String generateBlobName(String user, String filename) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);

        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();

        String blobName = user + "_" + timestamp + "_" + uniqueId + "_" + filename;
        return blobName;
    }
}

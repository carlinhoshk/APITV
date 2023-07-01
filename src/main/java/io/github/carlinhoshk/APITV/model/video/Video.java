package io.github.carlinhoshk.APITV.model.video;

import io.github.carlinhoshk.APITV.model.user.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "videos")
@Entity(name = "videos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private LocalDateTime uploadDate;
    private String blobName;
    private String blobUrl;

    @ManyToOne
    private UserModel user;

    public Video(String name, LocalDateTime uploadDate, String blobName, String blobUrl, UserModel user) {
        this.name = name;
        this.uploadDate = uploadDate;
        this.blobName = blobName;
        this.blobUrl = blobUrl;
        this.user = user;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public void setUrl(String blobUrl) {
        this.blobUrl = blobUrl;
    }

    // Getters and Setters
    // ID do usuário é obtido através de UserModel associado ao vídeo
    public String getUserId() {
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    // ID do usuário é definido através de UserModel associado ao vídeo
    public void setUserId(String userId) {
        if (user == null) {
            user = new UserModel();
        }
        user.setId(userId);
    }

    public void setName(String title) {
    }
}

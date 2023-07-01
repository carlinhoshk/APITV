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


}

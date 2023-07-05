package io.github.carlinhoshk.APITV.model.video;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "videos")
@Entity(name = "videos")
@Getter

@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String url;

    public Video() {
    }

    public Video(String login, String url) {
        this.login = login;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getlogin() {
        return login;
    }

    public void setUserId(String login) {
        this.login = login;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String blobUrl) {
        this.url = url;
    }
}

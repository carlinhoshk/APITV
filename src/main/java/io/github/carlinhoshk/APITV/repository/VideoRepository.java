package io.github.carlinhoshk.APITV.repository;

import io.github.carlinhoshk.APITV.model.user.UserModel;
import io.github.carlinhoshk.APITV.model.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, String> {

    // @Query("SELECT v FROM videos v WHERE v.user = :user")
    // List<Video> findByUser(@Param("user") UserModel user);
}

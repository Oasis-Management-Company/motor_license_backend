package com.app.IVAS.repository;

import com.app.IVAS.entity.ImageResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageResolutionDao extends JpaRepository<ImageResolution, Long> {
    @Query("SELECT im FROM ImageResolution as im WHERE im.fileCode = ?1 and im.width = ?2")
    ImageResolution findByFileCodeAndWidth(String code, int width);

    @Query("SELECT im FROM ImageResolution as im WHERE im.fileCode = ?1 and im.height = ?2")
    ImageResolution findByFileCodeAndHeight(String code, int height);

    ImageResolution findByFileCodeAndOriginal(String code, boolean isOriginal);

    @Query("SELECT im FROM ImageResolution as im WHERE im.fileCode = ?1")
    List<ImageResolution> findByFileCode(String fileCode);

}

package com.app.IVAS.repository;

import com.app.IVAS.entity.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileUploadDao extends JpaRepository<FileModel, Long> {
    @Query("SELECT f FROM FileModel as f WHERE f.code = ?1")
    FileModel findByCode(String code);
}

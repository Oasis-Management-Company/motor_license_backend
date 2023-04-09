package com.app.IVAS.service;


import com.app.IVAS.dto.FileUploadDto;
import com.app.IVAS.entity.FileModel;
import com.app.IVAS.entity.ImageResolution;

public interface FileUploadService {

    FileModel save(FileUploadDto fileUploadDto, String parentFileCode);

    FileModel findByCode(String code);

//    FileModel findByPortalAccount(String portalUserId);

    ImageResolution findByCodeAndWidthAndHeight(String code, int width, int height);

    void refresh(FileModel fileModel);
}

package com.app.IVAS.service;

import com.app.IVAS.Enum.DataStorageConstant;

import java.io.File;
import java.io.IOException;

public interface FileUploadToRemoteServerService {
    DataStorageConstant getName();

    String upload(File localFile) throws IOException;
}

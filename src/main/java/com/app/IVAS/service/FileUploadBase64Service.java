package com.app.IVAS.service;
import com.app.IVAS.Enum.DataStorageConstant;
import com.app.IVAS.Utils.Base64ToMultipartFile;
import com.app.IVAS.dto.Nimc.ImageResolutionDto;
import com.app.IVAS.entity.FileModel;
import com.app.IVAS.service.cloud.FileLocationConstant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class FileUploadBase64Service {

    private final Logger logger = Logger.getLogger(FileUploadBase64Service.class.getSimpleName());

    private final InitializeFileUploadService initializeFileUploadService;

    public FileUploadBase64Service(InitializeFileUploadService initializeFileUploadService) {
        this.initializeFileUploadService = initializeFileUploadService;
    }

    public FileModel upload(String base64) throws Exception {

        try {

            String dataStorageType = "";
            String fileLocation = "";
            String fileCode = "";

            try {
                dataStorageType = DataStorageConstant.DROP_BOX.toString();
                fileLocation = FileLocationConstant.MAIN_FOLDER_LOCATION_SERVER;

            } catch (Exception e) {
                e.printStackTrace();
            }

            List<ImageResolutionDto> imageResolutionDtos = new ArrayList<ImageResolutionDto>();
            fileCode = this.initializeFileUploadService.generateFileUploadSequence();

            String dataUir = "data:image/png;base64";
            base64 = base64.replace("\n", "")
                    .replace("\r", "");
            base64 = base64.replaceAll(" ", "");
            // logger.info(base64);
            MultipartFile multipartFile = new Base64ToMultipartFile(base64, dataUir);
            logger.info(multipartFile.getContentType());
            logger.info(multipartFile.getOriginalFilename());

            return initializeFileUploadService.saveFile(
                    multipartFile,
                    "file_server",
                    imageResolutionDtos,
                    DataStorageConstant.valueOf(dataStorageType),
                    fileLocation,
                    fileCode);

        } catch (Exception e) {
            logger.info(e.getMessage());
            throw e;
        }

    }
}

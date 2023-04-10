package com.app.IVAS.service;

import com.app.IVAS.Enum.DataStorageConstant;
import com.app.IVAS.Enum.FileTypeConstant;
import com.app.IVAS.dto.Nimc.ImageResolutionDto;
import com.app.IVAS.entity.FileModel;
import com.app.IVAS.repository.FileUploadDao;
import com.app.IVAS.repository.ImageResolutionDao;
import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
public class InitializeFileUploadService {

    private final Logger logger = Logger.getLogger(InitializeFileUploadService.class.getSimpleName());

    @Autowired
    private ImageResizeAndFileUploadService imageResizeAndFileUploadService;

    @Autowired
    private Gson gson;

    @Autowired
    private ImageResolutionDao imageResolutionDao;

    @Autowired
    private FileUploadDao fileUploadDao;

    // @Value("${dropbox.access.token}")
    private String accessToken;

    @Autowired
    @FileUploadSequence
    private FileUploadSequenceService fileUploadSequenceService;

    private String fileCode;

    public FileModel saveFile(MultipartFile multipartFile,
                              String folderName,
                              List<ImageResolutionDto> imageResolutionDtos,
                              DataStorageConstant dataStorageConstant,
                              String serverResourceUrl,
                              String fileCode) throws RuntimeException {


        this.fileCode = fileCode;
        String directoryName = "";
        if (multipartFile == null) {
            return null;
        }

        directoryName = serverResourceUrl + File.separator + folderName;
        File directory = new File(directoryName);

        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = null;

        try {

            logger.info("file code: " + fileCode);

            file = convertMultipartToFile(multipartFile, directoryName, fileCode);

            return getFileModel(multipartFile,
                    file,
                    directoryName,
                    imageResolutionDtos,
                    dataStorageConstant);

        } catch (IOException e) {
            e.printStackTrace();
            logger.info("could not get or create file!");
            return null;
        }
    }


    public String generateFileUploadSequence() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.format("FILE_%04d%02d%02d%07d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                timestamp.getTime(),
                fileUploadSequenceService.getNextId()
        );
    }


    private MultipartFile fileToMultipart(File file) {

        try {
            return new MockMultipartFile(file.getName(), new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getFileTypeFromMultipartFile(MultipartFile file) {

        if (file.isEmpty()) {
            return null;
        }

        try {
            String data = file.getContentType();
            // logger.info("data: " + data);
            boolean contains = data.toLowerCase().contains(FileTypeConstant.IMAGE.toString().toLowerCase());

            if (contains) {

                // logger.info("xxxx" + contains);
                return FileTypeConstant.IMAGE.toString();
            } else {
                // logger.info("failed1");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // logger.info("failed");
            return null;
        }
    }

    private FileModel fetchFileModel(String fileExtension,
                                     String directoryName,
                                     File file,
                                     DataStorageConstant dataStorageConstant) {

        return imageResizeAndFileUploadService.fileUpload(file,
                directoryName,
                fileExtension,
                "",
                dataStorageConstant,
                this.fileCode);
    }

    private FileModel createOrUpdate(String data,
                                     File file,
                                     String fileExtension,
                                     String directoryName,
                                     List<ImageResolutionDto> imageResolutionDtoList,
                                     DataStorageConstant dataStorageConstant) {

        try {

            if (data == null || StringUtils.isBlank(data)) {
                return this.fetchFileModel(fileExtension, directoryName, file, dataStorageConstant);
            } else if (FileTypeConstant.IMAGE.equals(FileTypeConstant.valueOf(data.toUpperCase()))) {

                return imageResizeAndFileUploadService.resizeAndUpload(
                        file,
                        directoryName,
                        fileExtension,
                        "",
                        dataStorageConstant.toString(),
                        imageResolutionDtoList,
                        this.fileCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    private static File convertMultipartToFile(MultipartFile multipartFile,
                                               String directoryName,
                                               String fileName) throws IllegalStateException, IOException {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String filePath = directoryName + File.separator + fileName + "." + extension;
        Path fileToDeletePath = Paths.get(filePath);
        boolean result = Files.deleteIfExists(fileToDeletePath);
        File file = new File(filePath);
        multipartFile.transferTo(file);
        return file;
    }


    private static File convertBase64ToFile(String base64ImageValue, String directoryName, String fileName) {

        String extension = "jpg";

        String base64Image = base64ImageValue.split(",")[1];

        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

        String filePath = directoryName + File.separator + fileName + "." + extension;

        Path fileToDeletePath = Paths.get(filePath);

        try {
            boolean result = Files.deleteIfExists(fileToDeletePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new FileOutputStream(filePath).write(imageBytes);
            return new File(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private FileModel getFileModel(MultipartFile multipartFile,
                                   File file,
                                   String directoryName,
                                   List<ImageResolutionDto> imageResolutionDtos,
                                   DataStorageConstant dataStorageConstant) {
        try {

            FileModel fileModel = null;

            String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

            String fileType = getFileTypeFromMultipartFile(multipartFile);

            logger.info("data: " + fileType);

            if (file == null) {
                return null;
            }

            if (fileType == null) {
                return null;
            }

            fileModel = this.createOrUpdate(fileType, file, fileExtension, directoryName, imageResolutionDtos, dataStorageConstant);

            return fileModel;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

}

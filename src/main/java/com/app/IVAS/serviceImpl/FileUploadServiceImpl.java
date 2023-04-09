package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.FileUploadDto;
import com.app.IVAS.dto.Nimc.ImageResolutionDto;
import com.app.IVAS.entity.FileModel;
import com.app.IVAS.entity.ImageResolution;
import com.app.IVAS.repository.FileUploadDao;
import com.app.IVAS.service.FileUploadSequence;
import com.app.IVAS.service.FileUploadSequenceService;
import com.app.IVAS.service.FileUploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FileUploadServiceImpl implements FileUploadService {
    private final FileUploadDao fileUploadDao;

    @PersistenceContext
    private EntityManager entityManager;

    @FileUploadSequence
    private final FileUploadSequenceService fileUploadSequenceService;

    public FileUploadServiceImpl(FileUploadDao fileUploadDao,
                                 FileUploadSequenceService fileUploadSequenceService) {
        this.fileUploadDao = fileUploadDao;
        this.fileUploadSequenceService = fileUploadSequenceService;
    }

    @Override
    @Transactional
    public void refresh(FileModel fileModel) {

        entityManager.refresh(fileModel);
    }

    @Override
    public FileModel save(FileUploadDto fileUploadDto, String parentFileCode) {

        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        List<ImageResolution> imageResolutionList = new ArrayList<>();
        List<ImageResolutionDto> imageResolutionDtoList = new ArrayList<>(fileUploadDto.getImageResolutionDtos());

        FileModel fileModel = null;

        if (StringUtils.isNotBlank(parentFileCode)) {

            fileModel = findByCode(parentFileCode);

            if (fileModel != null) {

                for (ImageResolutionDto imageResolutionDto : imageResolutionDtoList) {

                    //String code = generateImageResolutionSequence();
                    ImageResolution imageResolution = new ImageResolution();
                    imageResolution.setCode(imageResolutionDto.getCode());
                    imageResolution.setName(imageResolutionDto.getCode());
                    imageResolution.setOriginal(imageResolutionDto.isOriginal());
                    imageResolution.setHeight(imageResolutionDto.getHeight());
                    imageResolution.setWidth(imageResolutionDto.getWidth());
                    imageResolution.setUrl(imageResolutionDto.getFileOutputLocation());
                    imageResolution.setDateCreated(currentTimeStamp);
                    imageResolution.setDateUpdated(currentTimeStamp);
                    imageResolution.setDataStorageConstant(imageResolutionDto.getDataStorageConstant());

                    if (fileModel.getImageResolutions() != null) {

                        if (fileModel.getImageResolutions().size() <= 0) {
                            imageResolutionList.add(imageResolution);
                        } else {
                            imageResolutionList.add(imageResolution);

                            fileModel.setImageResolutions(imageResolutionList);
                        }
                    } else {

                        imageResolutionList.add(imageResolution);

                        fileModel.setImageResolutions(imageResolutionList);
                    }
                }
                entityManager.persist(fileModel);
                entityManager.close();

            } else {

                fileModel = new FileModel();
                fileModel.setCode(fileUploadDto.getCode());
                fileModel.setUrl(fileUploadDto.getFileOutputLocation());
                fileModel.setDataStorageConstant(fileUploadDto.getDataStorageConstant());
                fileModel.setName(fileUploadDto.getCode());
                fileModel.setDateCreated(currentTimeStamp);
                fileModel.setDateUpdated(currentTimeStamp);
                entityManager.persist(fileModel);

                for (ImageResolutionDto imageResolutionDto : imageResolutionDtoList) {

//                 ImageResolution imageResolution = new ImageResolution();
                    ImageResolution imageResolution = new ImageResolution();
                    imageResolution.setCode(imageResolutionDto.getCode());
                    imageResolution.setName(imageResolutionDto.getCode());
                    imageResolution.setDataStorageConstant(imageResolutionDto.getDataStorageConstant());
                    imageResolution.setOriginal(imageResolutionDto.isOriginal());
                    imageResolution.setHeight(imageResolutionDto.getHeight());
                    imageResolution.setWidth(imageResolutionDto.getWidth());
                    imageResolution.setUrl(imageResolutionDto.getFileOutputLocation());
                    imageResolution.setDateCreated(currentTimeStamp);
                    imageResolution.setDateUpdated(currentTimeStamp);

                    if (fileModel.getImageResolutions() != null) {

                        if (fileModel.getImageResolutions().size() <= 0) {
                            imageResolutionList.add(imageResolution);
                        } else {
                            imageResolutionList.add(imageResolution);

                            fileModel.setImageResolutions(imageResolutionList);
                        }
                    } else {

                        imageResolutionList.add(imageResolution);

                        fileModel.setImageResolutions(imageResolutionList);
                    }
                }
                entityManager.persist(fileModel);
                entityManager.close();
            }
        } else {

            fileModel = new FileModel();
            fileModel.setCode(fileUploadDto.getCode());
            fileModel.setName(fileUploadDto.getCode());
            fileModel.setUrl(fileUploadDto.getFileOutputLocation());
            fileModel.setDataStorageConstant(fileUploadDto.getDataStorageConstant());
            fileModel.setDateCreated(currentTimeStamp);
            fileModel.setDateUpdated(currentTimeStamp);
            entityManager.persist(fileModel);

            for (ImageResolutionDto imageResolutionDto : imageResolutionDtoList) {

                ImageResolution imageResolution = new ImageResolution();
                imageResolution.setCode(imageResolutionDto.getCode());
                imageResolution.setName(imageResolutionDto.getCode());
                imageResolution.setOriginal(imageResolutionDto.isOriginal());
                imageResolution.setHeight(imageResolutionDto.getHeight());
                imageResolution.setWidth(imageResolutionDto.getWidth());
                imageResolution.setUrl(imageResolutionDto.getFileOutputLocation());
                imageResolution.setDateCreated(currentTimeStamp);
                imageResolution.setDateUpdated(currentTimeStamp);
                imageResolution.setDataStorageConstant(imageResolutionDto.getDataStorageConstant());

                if (fileModel.getImageResolutions() != null) {

                    if (fileModel.getImageResolutions().size() <= 0) {
                        imageResolutionList.add(imageResolution);
                    } else {
                        imageResolutionList.add(imageResolution);

                        fileModel.setImageResolutions(imageResolutionList);
                    }
                } else {

                    imageResolutionList.add(imageResolution);

                    fileModel.setImageResolutions(imageResolutionList);
                }

            }
            entityManager.persist(fileModel);
            entityManager.close();
        }

        return fileModel;
    }


    @Override
    public FileModel findByCode(String code) {
        return fileUploadDao.findByCode(code);
    }

    @Override
    public ImageResolution findByCodeAndWidthAndHeight(String code, int width, int height) {
        return null;
    }


//    public String generateImageResolutionSequence() {
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//        return String.format("IMG_%04d%02d%02d%07d%05d",
//                LocalDate.now().getYear(),
//                LocalDate.now().getMonthValue(),
//                LocalDate.now().getDayOfMonth(),
//                timestamp.getTime(),
//                imageResolutionSequenceService.getNextId()
//        );
//    }


    public List<ImageResolution> getListImageResolutionDto(List<ImageResolutionDto> imageResolutionDtoList,
                                                           Timestamp currentTimeStamp,
                                                           FileModel fileModel,
                                                           List<ImageResolution> imageResolutionList, int i) {

        ImageResolution imageResolution = new ImageResolution();
        imageResolution.setCode(imageResolutionDtoList.get(i).getCode());
        imageResolution.setName(imageResolutionDtoList.get(i).getCode());
        imageResolution.setOriginal(imageResolutionDtoList.get(i).isOriginal());
        imageResolution.setHeight(imageResolutionDtoList.get(i).getHeight());
        imageResolution.setWidth(imageResolutionDtoList.get(i).getWidth());
        imageResolution.setUrl(imageResolutionDtoList.get(i).getFileOutputLocation());
        imageResolution.setDateCreated(currentTimeStamp);
        imageResolution.setDateUpdated(currentTimeStamp);

        if (fileModel.getImageResolutions().size() <= 0) {
            imageResolutionList.add(imageResolution);
        } else {
            fileModel.getImageResolutions().add(imageResolution);
        }

        return imageResolutionList;

    }
}

package com.app.IVAS.dto;

import com.app.IVAS.Enum.DataStorageConstant;
import com.app.IVAS.dto.Nimc.ImageResolutionDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

public class FileUploadDto {
    private String fileOutputLocation;

    private String code;

    private List<ImageResolutionDto> imageResolutionDtos;

    @Enumerated(EnumType.STRING)
    private DataStorageConstant dataStorageConstant;

    public String getFileOutputLocation() {
        return fileOutputLocation;
    }

    public void setFileOutputLocation(String fileOutputLocation) {
        this.fileOutputLocation = fileOutputLocation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ImageResolutionDto> getImageResolutionDtos() {
        return imageResolutionDtos;
    }

    public void setImageResolutionDtos(List<ImageResolutionDto> imageResolutionDtos) {
        this.imageResolutionDtos = imageResolutionDtos;
    }

    public DataStorageConstant getDataStorageConstant() {
        return dataStorageConstant;
    }

    public void setDataStorageConstant(DataStorageConstant dataStorageConstant) {
        this.dataStorageConstant = dataStorageConstant;
    }
}

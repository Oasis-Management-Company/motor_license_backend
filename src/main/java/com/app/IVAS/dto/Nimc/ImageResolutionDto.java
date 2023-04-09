package com.app.IVAS.dto.Nimc;

import com.app.IVAS.Enum.DataStorageConstant;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.awt.image.BufferedImage;

public class ImageResolutionDto {
    private int width;

    private int height;

    private boolean original;

    private BufferedImage bufferedImage;

    private String fileOutputLocation;

    private String imageType;

    private String code;

    private int calculatedHeight;

    private int calculatedWidth;

    @Enumerated(EnumType.STRING)
    private DataStorageConstant dataStorageConstant;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getFileOutputLocation() {
        return fileOutputLocation;
    }

    public void setFileOutputLocation(String fileOutputLocation) {
        this.fileOutputLocation = fileOutputLocation;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCalculatedHeight() {
        return calculatedHeight;
    }

    public void setCalculatedHeight(int calculatedHeight) {
        this.calculatedHeight = calculatedHeight;
    }

    public int getCalculatedWidth() {
        return calculatedWidth;
    }

    public void setCalculatedWidth(int calculatedWidth) {
        this.calculatedWidth = calculatedWidth;
    }

    public DataStorageConstant getDataStorageConstant() {
        return dataStorageConstant;
    }

    public void setDataStorageConstant(DataStorageConstant dataStorageConstant) {
        this.dataStorageConstant = dataStorageConstant;
    }
}

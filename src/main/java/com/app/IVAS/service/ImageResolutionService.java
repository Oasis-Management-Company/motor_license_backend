package com.app.IVAS.service;

import com.app.IVAS.entity.ImageResolution;

import java.util.List;

public interface ImageResolutionService {
    ImageResolution findByFileCodeAndWidth(String code, int width);

    ImageResolution findByFileCodeAndHeight(String code, int height);

    ImageResolution findByFileCode(String code);

    List<ImageResolution> findByCode(String code);
}

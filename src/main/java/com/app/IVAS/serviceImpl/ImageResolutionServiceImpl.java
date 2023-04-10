package com.app.IVAS.serviceImpl;

import com.app.IVAS.entity.ImageResolution;
import com.app.IVAS.repository.ImageResolutionDao;
import com.app.IVAS.service.ImageResolutionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageResolutionServiceImpl implements ImageResolutionService {
    private final ImageResolutionDao imageResolutionDao;

    public ImageResolutionServiceImpl(ImageResolutionDao imageResolutionDao) {
        this.imageResolutionDao = imageResolutionDao;
    }

    @Override
    public ImageResolution findByFileCodeAndWidth(String code, int width) {
        return this.imageResolutionDao.findByFileCodeAndWidth(code, width);
    }

    @Override
    public ImageResolution findByFileCodeAndHeight(String code, int height) {
        return this.imageResolutionDao.findByFileCodeAndHeight(code, height);
    }

    @Override
    public ImageResolution findByFileCode(String code) {
        return this.imageResolutionDao.findByFileCodeAndOriginal(code, true);
    }

    @Override
    public List<ImageResolution> findByCode(String code) {
        return this.imageResolutionDao.findByFileCode(code);
    }
}

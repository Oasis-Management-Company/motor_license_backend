package com.app.IVAS.service;


import org.springframework.stereotype.Component;

@ImageResolutionSequence
@Component
public class ImageResolutionSequenceService extends SequenceServiceImp {
    public ImageResolutionSequenceService() {
        super("image_resolution_sequence");
    }
}

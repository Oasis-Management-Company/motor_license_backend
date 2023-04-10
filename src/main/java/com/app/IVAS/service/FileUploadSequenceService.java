package com.app.IVAS.service;

import org.springframework.stereotype.Component;

@FileUploadSequence
@Component
public class FileUploadSequenceService extends SequenceServiceImp {
    public FileUploadSequenceService() {
        super("file_upload_sequence");
    }
}
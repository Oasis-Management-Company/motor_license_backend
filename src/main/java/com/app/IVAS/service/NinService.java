package com.app.IVAS.service;

import com.app.IVAS.entity.FindByNinDataResponsePojo;
import com.app.IVAS.entity.NIN;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface NinService {

    NIN findByNIN(String nin);

    NIN findByPhoneNumber(String phoneNumber);

    NIN save(FindByNinDataResponsePojo findByNinDataResponsePojo,
             String photoURL,
             String signatureURL,
             String fileCodeForPhoto,
             String fileCodeForSignature) throws ParseException;

    NIN saves(FindByNinDataResponsePojo findByNinDataResponsePojo) throws ParseException;
}

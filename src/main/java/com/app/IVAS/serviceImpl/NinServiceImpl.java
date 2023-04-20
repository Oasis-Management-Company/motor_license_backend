package com.app.IVAS.serviceImpl;

import com.app.IVAS.entity.FindByNinDataResponsePojo;
import com.app.IVAS.entity.NIN;
import com.app.IVAS.repository.NinRepository;
import com.app.IVAS.service.NinService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.logging.Logger;

@Service
public class NinServiceImpl implements NinService {

    private final NinRepository ninDao;

    private final Logger logger = Logger.getLogger(NinServiceImpl.class.getSimpleName());

    public NinServiceImpl(NinRepository ninDao) {
        this.ninDao = ninDao;
    }

    public NIN findByNIN(String nin) {
        logger.info(nin);
        return this.ninDao.findByNin(nin);
    }

//
//    public NIN save(FindByNinDataResponsePojo findByNinDataResponsePojo,
//                    String photoURL,
//                    String signatureURL,
//                    String fileCodeForPhoto,
//                    String fileCodeForSignature) throws ParseException {
//       NIN nin = NINDtoToEntityMapper.INSTANCE.dtoToEntity(findByNinDataResponsePojo);
//       if(findByNinDataResponsePojo.getGender().equalsIgnoreCase("m")) {
//           nin.setGender(Gender.MALE);
//       } else {
//           nin.setGender(Gender.FEMALE);
//       }
//        String pattern = "dd-MM-yyyy";
//        Date date = Utils.formatDate(findByNinDataResponsePojo.getBirthDate(), pattern);
//        nin.setBirthDate(date);
//        nin.setPhoto(photoURL);
//        nin.setSignature(signatureURL);
//        nin.setSignatureFileCode(fileCodeForSignature);
//        nin.setPhotoFileCode(fileCodeForPhoto);
//       return this.ninDao.save(nin);
//    }


    public NIN findByPhoneNumber(String phoneNumber) {
        return this.ninDao.findByTelePhoneNumber(phoneNumber);
    }

    @Override
    public NIN save(FindByNinDataResponsePojo findByNinDataResponsePojo, String photoURL, String signatureURL, String fileCodeForPhoto, String fileCodeForSignature) throws ParseException {
        return null;
    }

    @Override
    public NIN saves(FindByNinDataResponsePojo findByNinDataResponsePojo) throws ParseException {
        return null;
    }

}

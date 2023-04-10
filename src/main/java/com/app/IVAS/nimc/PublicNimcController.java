package com.app.IVAS.nimc;


import com.app.IVAS.Utils.MessageUtil;
import com.app.IVAS.Utils.OkHttp3Util;
import com.app.IVAS.Utils.ResourceUtil;
import com.app.IVAS.Utils.Utils;
import com.app.IVAS.api_response.ApiResponseServiceImpl;
import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.Nimc.CreateTokenDto;
import com.app.IVAS.dto.Nimc.SearchWithNINDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.errors.ApiError;
import com.app.IVAS.nimc.others.PublicService;
import com.app.IVAS.nin.creat_token.InnerCreateTokenResponsePojo;
import com.app.IVAS.nin.search_by_nin.InnerFindByNINResponsePojo;
import com.app.IVAS.repository.NinRepository;
import com.app.IVAS.service.FileUploadBase64Service;
import com.app.IVAS.service.ImageResolutionService;
import com.app.IVAS.service.NinSearchService;
import com.app.IVAS.service.NinService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", maxAge = 5600)
@RestController
public class PublicNimcController {

    @Value("${nin.domain}")
    private String ninDomain;

    @Value("${nin.domain_enum}")
    private String ninDomainEnum;

    @Value("${username}")
    private String uname;

    @Value("${password}")
    private String password;

    @Value("${hash.password}")
    private String hashPassword;

    @Value("${org.id}")
    private String orgId;

    private final OkHttp3Util okHttp3Util;

    private final MessageUtil messageUtil;

    private final ApiResponseServiceImpl apiResponseService;
    private final Logger logger = Logger.getLogger(PublicNimcController.class.getName());

    private final NinService ninService;

    private final FileUploadBase64Service fileUploadBase64Service;

    private final NinSearchService ninSearchService;

    private final ImageResolutionService imageResolutionService;
//    private final PortalAccountService portalAccountService;

    private final Environment environment;

    @Autowired
    private NinRepository ninRepository;
    @Autowired
    private PublicService publicService;


    public PublicNimcController(OkHttp3Util okHttp3Util, MessageUtil messageUtil,
                                FileUploadBase64Service fileUploadBase64Service,
                                ApiResponseServiceImpl apiResponseService,
                                NinService ninService,
                                ImageResolutionService imageResolutionService,
                                NinSearchService ninSearchService,
                                Environment environment) {
        this.okHttp3Util = okHttp3Util;
        this.messageUtil = messageUtil;
        this.apiResponseService = apiResponseService;
        this.ninService = ninService;
        this.ninSearchService = ninSearchService;
        this.imageResolutionService= imageResolutionService;
        this.fileUploadBase64Service = fileUploadBase64Service;
        this.environment = environment;
    }

    @PostMapping("/nimc/create-token")
    public ResponseEntity<?> creteToken() {
        ApiError apiError = null;

        try {

            CreateTokenDto createTokenDto = new CreateTokenDto();
            createTokenDto.setOrgId(orgId);
            createTokenDto.setUsername("MAkinyele");
            createTokenDto.setPassword(hashPassword);

            Gson gson = new Gson();

            String json = gson.toJson(createTokenDto);
            String url = ninDomain+"/create-token";
            String xmlString = "";
            xmlString = this.okHttp3Util.post(url, json);

            if(xmlString.isEmpty()) {
                apiError = new ApiError(HttpStatus.OK.value(),
                        HttpStatus.OK,
                        messageUtil.getMessage("nimc.response.failed", "en"),
                        false,
                        new ArrayList<>(),
                        null);

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            InnerCreateTokenResponsePojo innerCreateTokenResponsePojo = this.ninSearchService.createToken(xmlString);
            if(innerCreateTokenResponsePojo.getLoginObject() == null) {
                apiError = new ApiError(HttpStatus.OK.value(),
                        HttpStatus.OK,
                        messageUtil.getMessage("nimc.response.failed", "en"),
                        false,
                        new ArrayList<>(),
                        null);

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            String token = Utils.removeNewLineAndSpace(innerCreateTokenResponsePojo.getLoginString());

            apiError = new ApiError(HttpStatus.OK.value(),
                    HttpStatus.OK,
                    messageUtil.getMessage("nimc.response", "en"),
                    true,
                    new ArrayList<>(),
                    token);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            return apiResponseService.internalServerError();
        }
    }

    @PostMapping("/nimc/search-with-nin/{nin_number}")
    public NIN searchWithNIN(@PathVariable("nin_number") String ninNumber) {

        ApiError apiError = null;

        if(ninNumber.isEmpty()) {
            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST,
                    messageUtil.getMessage("nimc.nin.required", "en"),
                    false,
                    new ArrayList<>(),
                    null);

            return null;
        }

        try {
            NIN nin = this.ninService.findByNIN(ninNumber);

            System.out.println(nin);

            if(nin == null) {
                CreateTokenDto createTokenDto = new CreateTokenDto();
                createTokenDto.setOrgId(orgId);
                createTokenDto.setUsername("MAkinyele");
                createTokenDto.setPassword(hashPassword);

                Gson gson = new Gson();

                String jsonForToken = gson.toJson(createTokenDto);
                String url = ninDomain+"/create-token";

                String xmlStringForToken;
                if(environment.acceptsProfiles(Profiles.of("local-nin"))) {
                    // String path = ResourceUtil.getResourceAsUrl("nin/create-token.txt").getPath();
                    xmlStringForToken = IOUtils.toString(ResourceUtil.getResourceAsStream("nin/create-token.txt"));
                } else {
                    xmlStringForToken = this.okHttp3Util.post(url, jsonForToken);
                }
                System.out.println(xmlStringForToken);
                if(xmlStringForToken == null || xmlStringForToken.isEmpty()) {
                    System.out.println(xmlStringForToken + " is null from the error");
                    apiError = new ApiError(HttpStatus.OK.value(),
                            HttpStatus.OK,
                            messageUtil.getMessage("nimc.response.failed ", "en"),
                            false,
                            new ArrayList<>(),
                            null);

                    return null;
                }


                InnerCreateTokenResponsePojo innerCreateTokenResponsePojo = this.ninSearchService.createToken(xmlStringForToken);
                if(innerCreateTokenResponsePojo.getLoginObject() == null) {
                    apiError = new ApiError(HttpStatus.OK.value(),
                            HttpStatus.OK,
                            messageUtil.getMessage("nimc.response.failed ", "en"),
                            false,
                            new ArrayList<>(),
                            null);

                    return null;
                }


                String token = Utils.removeNewLineAndSpace(innerCreateTokenResponsePojo.getLoginString());
                SearchWithNINDto searchWithNINDto = new SearchWithNINDto();
                searchWithNINDto.setNin(ninNumber);
                searchWithNINDto.setToken(token);

                String json = gson.toJson(searchWithNINDto);

                String xmlString;
                if(environment.acceptsProfiles(Profiles.of("local-nin"))) {
                    xmlString = IOUtils.toString(ResourceUtil.getResourceAsUrl("nin/nin-user-search-response.txt"));
                } else {
                    xmlString = this.okHttp3Util.post(ninDomain+"/search-with-nin", json);
                }

                if(xmlString == null || xmlString.isEmpty()) {
                    apiError = new ApiError(HttpStatus.OK.value(),
                            HttpStatus.OK,
                            messageUtil.getMessage("nimc.response.failed", "en"),
                            false,
                            new ArrayList<>(),
                            null);

                    return null;
                }

                InnerFindByNINResponsePojo innerFindByNINResponsePojo = this.ninSearchService.ninSearch(xmlString);
                if(!innerFindByNINResponsePojo.getReturnMessage().equalsIgnoreCase("success")) {
                    apiError = new ApiError(HttpStatus.OK.value(),
                            HttpStatus.OK,
                            innerFindByNINResponsePojo.getReturnMessage(),
                            false,
                            new ArrayList<>(),
                            null);

                    return null;
                }
                com.app.IVAS.nin.search_by_nin.FindByNinDataResponsePojo findByNinDataResponsePojo = innerFindByNINResponsePojo.getData();

                //Upload image
                ImageResolution imageResolutionForSignature = null;
                ImageResolution imageResolutionForPhoto = null;

                String fileCodeForPhoto;
                String fileCodeForSignature = null;

                try {
                    FileModel fileModelForSignature = this.fileUploadBase64Service.upload(findByNinDataResponsePojo.getSignature());
                    imageResolutionForSignature = this.imageResolutionService.findByFileCode(fileModelForSignature.getCode());
                    fileCodeForSignature = fileModelForSignature.getCode();
                } catch(IllegalArgumentException iae) {
                    logger.info(iae.getMessage());
//                    return this.apiResponseService.photoNotFoundWithNIN();
                }

                try {
                    FileModel fileModelForPhoto = this.fileUploadBase64Service.upload(findByNinDataResponsePojo.getPhoto());
                    imageResolutionForPhoto = this.imageResolutionService.findByFileCode(fileModelForPhoto.getCode());
                    fileCodeForPhoto = fileModelForPhoto.getCode();
                } catch(IllegalArgumentException iae) {
                    logger.info(iae.getMessage());
                    return null;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");


                NIN nnin = new NIN();

                nnin.setTitle(findByNinDataResponsePojo.getTitle());
                nnin.setSignature(imageResolutionForSignature.getUrl());
                nnin.setResidenceLga(findByNinDataResponsePojo.getResidenceLga());
                nnin.setPhoto(imageResolutionForPhoto.getUrl());
                nnin.setNin(findByNinDataResponsePojo.getNin());
                nnin.setProfession(findByNinDataResponsePojo.getProfession());
                nnin.setTelePhoneNumber(findByNinDataResponsePojo.getTelePhoneNumber());
                nnin.setFirstName(findByNinDataResponsePojo.getFirstName());
                nnin.setMiddleName(findByNinDataResponsePojo.getMiddleName());
                nnin.setSurname(findByNinDataResponsePojo.getSurname());
                nnin.setBirthLGA(findByNinDataResponsePojo.getBirthLGA());
                nnin.setSelfOriginPlace(findByNinDataResponsePojo.getSelfOriginPlace());
                nnin.setBirthCountry(findByNinDataResponsePojo.getBirthCountry());
                nnin.setBirthState(findByNinDataResponsePojo.getBirthState());
                nnin.setCentralId(findByNinDataResponsePojo.getCentralId());
                nnin.setEducationalLevel(findByNinDataResponsePojo.getEducationalLevel());
                nnin.setResidenceAddressLine1(findByNinDataResponsePojo.getResidenceAddressLine1());
                nnin.setBirthState(findByNinDataResponsePojo.getBirthState());
                if (findByNinDataResponsePojo.getEmail() != null){
                    nnin.setEmail(findByNinDataResponsePojo.getEmail());
                }else{
                    nnin.setEmail(findByNinDataResponsePojo.getNin()+"@gmail.com");
                }

                nnin.setSelfOriginLga(findByNinDataResponsePojo.getSelfOriginLga());
                nnin.setReligion(findByNinDataResponsePojo.getReligion());
                nnin.setMaritalStatus(findByNinDataResponsePojo.getMaritalStatus());
                nnin.setResidenceTown(findByNinDataResponsePojo.getResidenceTown());
                nnin.setResidenceState(findByNinDataResponsePojo.getResidenceState());
                nnin.setSelfOriginState(findByNinDataResponsePojo.getSelfOriginState());
                nnin.setGender(findByNinDataResponsePojo.getGender());
//                Date date = formatter.parse(findByNinDataResponsePojo.getBirthDate());
                nnin.setBirthDate(findByNinDataResponsePojo.getBirthDate());
                NIN found = ninRepository.save(nnin);

                return found;

            }

            return nin;

        } catch (Exception e) {
            logger.info("error found "  + e.getMessage());
            return null;
        }

    }

    @PostMapping("/nimc/search-with-phone-number")
    public ResponseEntity<?> searchWithPhoneNumber() {
        ApiError apiError = null;

        try {

            this.okHttp3Util.post(ninDomain+"/create-token");

            apiError = new ApiError(HttpStatus.OK.value(),
                    HttpStatus.OK,
                    messageUtil.getMessage("nationalities.fetched", "en"),
                    true,
                    new ArrayList<>(),
                    null);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        } catch (Exception e) {
            logger.info(e.getMessage());
            return apiResponseService.internalServerError();
        }
    }

    @PostMapping("/nimc/number/{number}")
    public AsinDto saveUpdatePerson(@PathVariable String  number) throws IOException {
        NIN nin = this.ninService.findByNIN(number);
        AsinDto dto = new AsinDto();
        try{
            if (nin == null){
                String result = this.okHttp3Util.get(ninDomainEnum+number);

                ObjectMapper objectMappe = new ObjectMapper();
                FindByNinDataResponsePojo nins= objectMappe.readValue(result, FindByNinDataResponsePojo.class);
                if (nins.getResponseData() != null){
                    NIN saved = ninRepository.save(nins.getResponseData());
                    dto.setPhoto(saved.getPhoto());
                    dto.setEmail(saved.getEmail());
                    dto.setPhoneNumber(saved.getTelePhoneNumber());
                    dto.setName(saved.getFirstName());
                    dto.setAddress(saved.getResidenceAddressLine1());
                    dto.setNinId(saved.getId());
                    return dto;
                }else{
                    UserDemographicIndividual individual =  publicService.getInformalSectioFromTax(number);
                    System.out.println(individual);
                    dto.setPhoto(individual.getPhoto());
                    dto.setEmail(individual.getEmail());
                    dto.setPhoneNumber(individual.getPhoneNumber());
                    dto.setName(individual.getName());
                    dto.setAddress(individual.getAddress());
                    dto.setFirstname(individual.getFirstname());
                    dto.setLastname(individual.getLastname());
                    dto.setLga(individual.getLga());

                    return dto;
                }

            }
            dto.setPhoto(nin.getPhoto());
            dto.setEmail(nin.getEmail());
            dto.setPhoneNumber(nin.getTelePhoneNumber());
            dto.setName(nin.getFirstName());
            dto.setAddress(nin.getResidenceAddressLine1());
            dto.setNinId(nin.getId());
            return  dto;
        }catch (Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    @PostMapping("/nimc/ucode/{number}")
    public AsinDto getUpdatePerson(@PathVariable String  number) throws IOException {
        System.out.println(number);
        AsinDto dto = new AsinDto();
        NIN nin = this.ninService.findByNIN(number);
        try{
            if (nin == null){
                String result = this.okHttp3Util.get(ninDomainEnum+number);

                ObjectMapper objectMappe = new ObjectMapper();
                FindByNinDataResponsePojo nins= objectMappe.readValue(result, FindByNinDataResponsePojo.class);
                if (nins.getResponseData() != null){

                    NIN saved = ninRepository.save(nins.getResponseData());
                    dto.setPhoto(saved.getPhoto());
                    dto.setEmail(saved.getEmail());
                    dto.setPhoneNumber(saved.getTelePhoneNumber());
                    return dto;
                }


            }
            assert nin != null;
            dto.setPhoto(nin.getPhoto());
            dto.setEmail(nin.getEmail());
            dto.setPhoneNumber(nin.getTelePhoneNumber());
            return dto;
        }catch (NullPointerException ex){
            return null;
        }
    }

}

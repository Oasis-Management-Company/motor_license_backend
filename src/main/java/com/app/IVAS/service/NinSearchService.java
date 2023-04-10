package com.app.IVAS.service;

import com.app.IVAS.nin.creat_token.CreateTokenResponsePojo;
import com.app.IVAS.nin.creat_token.InnerCreateTokenResponsePojo;
import com.app.IVAS.nin.search_by_nin.CreateFIndByNINResponsePojo;
import com.app.IVAS.nin.search_by_nin.InnerFindByNINResponsePojo;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class NinSearchService {
    public InnerFindByNINResponsePojo ninSearch(String xmlString) throws JAXBException, SOAPException, IOException {
        SOAPMessage message = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(xmlString.getBytes()));
        JAXBContext jc = JAXBContext.newInstance(CreateFIndByNINResponsePojo.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CreateFIndByNINResponsePojo createFIndByNINResponsePojo = (CreateFIndByNINResponsePojo) unmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
        // logger.info(new Gson().toJson(createFIndByNINResponsePojo));
        return createFIndByNINResponsePojo.getReturnObj();
    }


    public InnerCreateTokenResponsePojo createToken(String xmlString) throws JAXBException, SOAPException, IOException {
        SOAPMessage message = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(xmlString.getBytes()));
        JAXBContext jc = JAXBContext.newInstance(CreateTokenResponsePojo.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CreateTokenResponsePojo createTokenResponsePojo = (CreateTokenResponsePojo) unmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
        // logger.info(new Gson().toJson(createFIndByNINResponsePojo));
        return createTokenResponsePojo.getReturnObj();
    }
}

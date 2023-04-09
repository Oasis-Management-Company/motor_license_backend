package com.app.IVAS.nin.creat_token;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@XmlRootElement(name = "createTokenResponse", namespace="http://IdentitySearch.nimc/")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CreateTokenResponsePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    protected InnerCreateTokenResponsePojo returnObj;

    @XmlElement(name = "return")
    public InnerCreateTokenResponsePojo getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(InnerCreateTokenResponsePojo returnObj) {
        this.returnObj = returnObj;
    }
}
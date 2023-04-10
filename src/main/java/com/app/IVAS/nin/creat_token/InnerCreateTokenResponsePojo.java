package com.app.IVAS.nin.creat_token;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InnerCreateTokenResponsePojo {

    private TokenBodyResponsePojo loginObject;

    private String loginString;

    @XmlElement(name = "loginString")
    public String getLoginString() {
        return loginString;
    }

    public void setLoginString(String loginString) {
        this.loginString = loginString;
    }

    @XmlElement(name = "loginObject")
    public TokenBodyResponsePojo getLoginObject() {
        return loginObject;
    }

    public void setLoginObject(TokenBodyResponsePojo loginObject) {
        this.loginObject = loginObject;
    }
}

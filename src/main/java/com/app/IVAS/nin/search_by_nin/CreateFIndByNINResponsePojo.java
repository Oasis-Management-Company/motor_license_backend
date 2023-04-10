package com.app.IVAS.nin.search_by_nin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "searchByNINResponse", namespace="http://IdentitySearch.nimc/")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CreateFIndByNINResponsePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    protected InnerFindByNINResponsePojo returnObj;

    @XmlElement(name = "return")
    public InnerFindByNINResponsePojo getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(InnerFindByNINResponsePojo returnObj) {
        this.returnObj = returnObj;
    }
}

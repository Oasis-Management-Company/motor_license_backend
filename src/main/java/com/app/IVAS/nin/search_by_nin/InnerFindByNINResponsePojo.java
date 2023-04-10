package com.app.IVAS.nin.search_by_nin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class InnerFindByNINResponsePojo implements Serializable {

    private FindByNinDataResponsePojo data;

    private String returnMessage;

    @XmlElement(name = "returnMessage")
    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    @XmlElement(name = "data")
    public FindByNinDataResponsePojo getData() {
        return data;
    }

    public void setData(FindByNinDataResponsePojo data) {
        this.data = data;
    }
}

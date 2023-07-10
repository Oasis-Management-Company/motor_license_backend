package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopParentRequest
{
    private ParentRequest parentRequest;
    private List<ParentRequest> childRequestSingle;
    private List<ParentRequest> data;
}

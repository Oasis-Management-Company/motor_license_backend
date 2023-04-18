package com.app.IVAS.dto;

import com.app.IVAS.Enum.ActivityStatusConstant;
import lombok.Data;

@Data
public class ActivityLogPojo {
    private String actor;
    private String description;
    private String createdAt;
    private ActivityStatusConstant action;
}

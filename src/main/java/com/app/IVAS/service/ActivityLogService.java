package com.app.IVAS.service;

import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.dto.ActivityLogPojo;
import com.app.IVAS.entity.ActivityLog;

import java.util.List;

public interface ActivityLogService {

    List<ActivityLogPojo> get(List<ActivityLog> activityLogs);

    void createActivityLog(String description, ActivityStatusConstant action);
}

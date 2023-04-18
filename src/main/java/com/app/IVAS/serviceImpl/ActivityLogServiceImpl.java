package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.dto.ActivityLogPojo;
import com.app.IVAS.entity.ActivityLog;
import com.app.IVAS.repository.ActivityLogRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final JwtService jwtService;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public List<ActivityLogPojo> get(List<ActivityLog> activityLogs) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");

        return activityLogs.stream().map(activityLog -> {
            ActivityLogPojo pojo = new ActivityLogPojo();
            pojo.setActor(activityLog.getCreatedBy().getDisplayName());
            pojo.setDescription(activityLog.getDescription());
            pojo.setCreatedAt(activityLog.getCreatedAt().format(df));
            pojo.setAction(activityLog.getAction());
            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public void createActivityLog(String description, ActivityStatusConstant action) {
        ActivityLog log = new ActivityLog();
        log.setDescription(description);
        log.setAction(action);
        log.setStatus(GenericStatusConstant.ACTIVE);
        log.setCreatedBy(jwtService.user);
        activityLogRepository.save(log);
    }
}

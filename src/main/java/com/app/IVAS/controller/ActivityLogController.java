package com.app.IVAS.controller;


import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.ActivityLogPojo;
import com.app.IVAS.dto.filters.ActivityLogSearchFilter;
import com.app.IVAS.entity.ActivityLog;
import com.app.IVAS.entity.QActivityLog;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.ActivityLogService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity-log")
public class ActivityLogController {

    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final ActivityLogService activityLogService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/search")
    @Transactional
    public QueryResults<ActivityLogPojo> searchActivityLog(ActivityLogSearchFilter filter){

        JPAQuery<ActivityLog> activityLogJPAQuery = appRepository.startJPAQuery(QActivityLog.activityLog)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            activityLogJPAQuery.where(QActivityLog.activityLog.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            activityLogJPAQuery.where(QActivityLog.activityLog.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QActivityLog.activityLog);
        QueryResults<ActivityLog> activityLogQueryResults = activityLogJPAQuery.select(QActivityLog.activityLog).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(activityLogService.get(activityLogQueryResults.getResults()), activityLogQueryResults.getLimit(), activityLogQueryResults.getOffset(), activityLogQueryResults.getTotal());
    }
}

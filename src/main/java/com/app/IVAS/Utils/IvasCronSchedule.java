package com.app.IVAS.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("default")
public class IvasCronSchedule {

    private final ExecutorService cronExecutor = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
    }

//    @Scheduled(fixedDelay = 3600 * DateUtils.MILLIS_PER_SECOND, initialDelay = DateUtils.MILLIS_PER_MINUTE)
//    public void sendVerificationTransaction() {
//        cronExecutor.execute(() -> {
//            try {
//                cardService.deleteFiles();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

    @PreDestroy
    public void deInit(){this.cronExecutor.shutdown();}
}

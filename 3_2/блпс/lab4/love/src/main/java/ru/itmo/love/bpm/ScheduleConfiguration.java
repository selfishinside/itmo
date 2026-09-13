package ru.itmo.love.bpm;

import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;

@Configuration
public class ScheduleConfiguration {
    @Bean public String checkInCron(@Value("${app.scheduling.check-in-reminder-cron}") String value) { return quartz(value); }
    @Bean public String ratingCron(@Value("${app.scheduling.rating-update-cron}") String value) { return quartz(value); }
    @Bean public String retryCycle(@Value("${app.scheduling.notification-retry-rate}") long value) {
        if(value <= 0) throw new IllegalArgumentException("Retry interval must be positive");
        return "R/"+Duration.ofMillis(value);
    }
    private String quartz(String cron) {
        String[] fields=cron.split("\\s+");
        if(fields.length == 6 && fields[5].equals("*")) fields[5]="?";
        return String.join(" ",fields);
    }
}

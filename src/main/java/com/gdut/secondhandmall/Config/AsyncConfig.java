package com.gdut.secondhandmall.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/10-10:59
 * @description 配置线程池
 **/
@Configuration
public class AsyncConfig {
    private static int processors = Runtime.getRuntime().availableProcessors();
    private static int maximum = processors + 3;

    @Bean("taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maximum);
        taskExecutor.setCorePoolSize(processors);
        taskExecutor.setThreadNamePrefix("async-task-thread-pool");
        taskExecutor.initialize();
        return taskExecutor;
    }
}

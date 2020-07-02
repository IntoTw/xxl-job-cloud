package com.xxl.job.core.cloud;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chenxiang
 *
 * @generator: IntelliJ IDEA
 * @description:
 * @project: xxl-job-cloud
 * @package: com.xxl.job.core.cloud
 * @date: 2020年07月02日 16时11分
 */
@Configuration
public class BizThreadPoolExecutorConfig {
    @Bean("bizThreadPool")
    ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                0,
                200,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                r -> new Thread(r, "xxl-rpc, EmbedServer bizThreadPool-" + r.hashCode()),
                (r, executor) -> {
                    throw new RuntimeException("xxl-job, EmbedServer bizThreadPool is EXHAUSTED!");
                });
    }
}

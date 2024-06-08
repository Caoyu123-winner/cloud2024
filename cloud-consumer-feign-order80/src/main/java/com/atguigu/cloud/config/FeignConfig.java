package com.atguigu.cloud.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY; //Feign默认配置是不走重试策略的

        // 最大请求次数3(1+2)，初始间隔为100ms， 重试间最大间隔为1s
//        return new Retryer.Default(100, 1, 3);
    }

    /**
     * 配置日志的Bean
     * @return
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}

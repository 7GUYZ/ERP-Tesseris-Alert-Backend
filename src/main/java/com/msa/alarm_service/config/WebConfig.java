package com.msa.alarm_service.config;


import com.msa.alarm_service.interceptor.AppIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AppIdInterceptor appIdInterceptor;

    public WebConfig(AppIdInterceptor appIdInterceptor) {
        this.appIdInterceptor = appIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 모든 요청에 대해 AppIdInterceptor를 실행합니다.
        registry.addInterceptor(appIdInterceptor)
                .addPathPatterns("/**"); // 모든 URL에 대해 interceptor 실행
    }
}
package com.tripmate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://tripmate-bucket.s3-website.ap-northeast-2.amazonaws.com",  // S3 버킷 URL
                        "http://localhost:3000"  // 로컬 개발 환경 추후 배포 시 삭제
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);  // 자격 증명 허용
    }
}

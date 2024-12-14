package cn.xyt.codehub.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能 
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }


    // 配置跨域允许
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 前端域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}

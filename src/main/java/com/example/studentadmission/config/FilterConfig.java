package com.example.studentadmission.config;

import com.example.studentadmission.filter.AuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> loggingFilter() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthenticationFilter());

        // Apply security to these URL patterns
        registrationBean.addUrlPatterns("/admissions/*");
        registrationBean.addUrlPatterns("/courses/*");
        registrationBean.addUrlPatterns("/institutes/*");

        // Note: We don't add "/api/students/*" here broadly because Login/Register are there.
        // If you want to protect getting student profile, the filter logic handles the exclusions.

        return registrationBean;
    }
}

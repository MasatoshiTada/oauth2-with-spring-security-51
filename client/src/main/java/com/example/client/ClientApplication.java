package com.example.client;

import com.example.client.security.oauth2.OAuth2TokenService;
import com.example.client.web.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class ClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean loggingFilter() {
        LoggingFilter loggingFilter = new LoggingFilter();
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>(loggingFilter);
        // フィルターの順番を一番最初に指定
        registrationBean.setOrder(Integer.MIN_VALUE);
        // url-patternを指定
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, OAuth2TokenService oAuth2TokenService) {
        return builder.setConnectTimeout(Duration.ofMillis(500))
                .setReadTimeout(Duration.ofMillis(500))
                .additionalRequestCustomizers(addAccessTokenToHeader(oAuth2TokenService))
                .build();
    }

    private RestTemplateRequestCustomizer addAccessTokenToHeader(OAuth2TokenService oAuth2TokenService) {
        return request -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 認証不要またはOAuth2以外なら何もしない
            if (authentication == null || !(authentication instanceof OAuth2AuthenticationToken)) {
                return;
            }
            // OAuth2の場合はAuthorizationヘッダーに"Bearer アクセストークン"をセットする
            request.getHeaders().setBearerAuth(oAuth2TokenService.getAccessTokenValue());
        };
    }
}

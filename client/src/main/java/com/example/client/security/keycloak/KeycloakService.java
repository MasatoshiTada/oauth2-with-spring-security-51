package com.example.client.security.keycloak;

import com.example.client.security.oauth2.OAuth2TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloakに対する操作を提供するクラス。
 */
@Service
public class KeycloakService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    private final WebClient webClient;
    private final OAuth2TokenService oAuth2TokenService;
    private final OAuth2ClientProperties.Registration registration;
    private final KeycloakProperties keycloakProperties;

    public KeycloakService(WebClient webClient,
                           OAuth2TokenService oAuth2TokenService,
                           OAuth2ClientProperties oAuth2ClientProperties,
                           KeycloakProperties keycloakProperties) {
        this.webClient = webClient;
        this.oAuth2TokenService = oAuth2TokenService;
        this.registration = oAuth2ClientProperties.getRegistration().get("todo");
        this.keycloakProperties = keycloakProperties;
    }

    /**
     * Keycloakからログアウトする。
     * https://www.keycloak.org/docs/latest/securing_apps/index.html#logout-endpoint
     */
    public void logout() {
        // POSTするリクエストパラメーターを作成
        Map<String, String> params = new HashMap<>();
        params.put("client_id", registration.getClientId());
        params.put("client_secret", registration.getClientSecret());
        params.put("refresh_token", oAuth2TokenService.getRefreshTokenValue());
        // ログアウトリクエスト送信
        ClientResponse response = webClient.post()
                .uri(keycloakProperties.getLogoutUri())
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(params)
                .exchange()
                .block();
        // ログ出力
        logger.info("{}", response.statusCode());
    }
}
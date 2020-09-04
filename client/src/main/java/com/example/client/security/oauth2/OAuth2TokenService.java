package com.example.client.security.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

@Service
public class OAuth2TokenService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2TokenService.class);

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final DefaultRefreshTokenTokenResponseClient tokenResponseClient;

    public OAuth2TokenService(OAuth2AuthorizedClientService authorizedClientService, RestTemplateBuilder restTemplateBuilder) {
        this.authorizedClientService = authorizedClientService;
        this.tokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(300))
                .setReadTimeout(Duration.ofMillis(300))
                .errorHandler(new OAuth2ErrorResponseErrorHandler())
                .messageConverters(
                        new OAuth2AccessTokenResponseHttpMessageConverter(),
                        new OAuth2ErrorHttpMessageConverter(),
                        new FormHttpMessageConverter())
                .build();
        tokenResponseClient.setRestOperations(restTemplate);
    }

    /**
     * アクセストークンの値を取得する。
     */
    public String getAccessTokenValue() {
        OAuth2AccessToken accessToken = getAuthorizedClient().getAccessToken();
        // アクセストークンが期限切れだったらリフレッシュ
        if (isExpired(accessToken)) {
            logger.debug("Access token was expired!");
            accessToken = refresh();
        }
        String tokenValue = accessToken.getTokenValue();
        logger.debug("access_token = {}", tokenValue);
        return tokenValue;
    }

    /**
     * リフレッシュトークンの値を取得する。
     */
    public String getRefreshTokenValue() {
        OAuth2RefreshToken refreshToken = getAuthorizedClient().getRefreshToken();
        String tokenValue = refreshToken.getTokenValue();
        return tokenValue;
    }

    /**
     * アクセストークンが期限切れならばtrueを返す。
     */
    private boolean isExpired(OAuth2AccessToken accessToken) {
        return accessToken.getExpiresAt().isBefore(Instant.now());
    }

    /**
     * リフレッシュトークンでアクセストークンを再取得する。
     */
    private OAuth2AccessToken refresh() {
        // トークンをリフレッシュ
        OAuth2AuthorizedClient currentAuthorizedClient = getAuthorizedClient();
        ClientRegistration clientRegistration = currentAuthorizedClient.getClientRegistration();
        OAuth2RefreshTokenGrantRequest tokenRequest =
                new OAuth2RefreshTokenGrantRequest(clientRegistration,
                        currentAuthorizedClient.getAccessToken(),
                        currentAuthorizedClient.getRefreshToken());
        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(tokenRequest);
        // インメモリから既存のトークンを削除
        authorizedClientService.removeAuthorizedClient(
                clientRegistration.getRegistrationId(),
                currentAuthorizedClient.getPrincipalName());
        // インメモリに新しいトークンを登録
        OAuth2AuthenticationToken authentication = getAuthentication();
        OAuth2AuthorizedClient newAuthorizedClient = new OAuth2AuthorizedClient(
                clientRegistration, authentication.getName(),
                tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
        authorizedClientService.saveAuthorizedClient(newAuthorizedClient, authentication);
        logger.debug("Refreshing token completed");
        return tokenResponse.getAccessToken();
    }

    /**
     * OAuth2AuthorizedClientを取得する。
     */
    public OAuth2AuthorizedClient getAuthorizedClient() {
        // OAuth2AuthenticationTokenはAuthenticationインタフェース実装クラス
        OAuth2AuthenticationToken authentication = getAuthentication();
        // OAuth2AuthorizedClientを取得
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());
        return authorizedClient;
    }

    /**
     * OAuth2AuthenticationTokenを取得する。
     */
    public OAuth2AuthenticationToken getAuthentication() {
        OAuth2AuthenticationToken authentication =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

}
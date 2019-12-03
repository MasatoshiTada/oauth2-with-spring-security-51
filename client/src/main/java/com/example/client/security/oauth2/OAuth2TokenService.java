package com.example.client.security.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.RefreshTokenOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OAuth2TokenService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2TokenService.class);

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RefreshTokenOAuth2AuthorizedClientProvider refreshTokenOAuth2AuthorizedClientProvider;

    public OAuth2TokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.refreshTokenOAuth2AuthorizedClientProvider = new RefreshTokenOAuth2AuthorizedClientProvider();
    }

    /**
     * アクセストークンの値を取得する。
     */
    public String getAccessTokenValue() {
        OAuth2AccessToken accessToken = getAuthorizedClient().getAccessToken();
        // アクセストークンが期限切れだったらリフレッシュ
        if (isExpiredToken(accessToken)) {
            logger.debug("Access token was expired!");
            accessToken = getRefreshedAccessToken();
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
    private boolean isExpiredToken(OAuth2AccessToken accessToken) {
        return accessToken.getExpiresAt().isBefore(Instant.now());
    }

    /**
     * リフレッシュトークンでアクセストークンを再取得する。
     */
    private OAuth2AccessToken getRefreshedAccessToken() {
        OAuth2AuthenticationToken authentication = getAuthentication();
        OAuth2AuthorizedClient currentAuthorizedClient = getAuthorizedClient();
        OAuth2AuthorizationContext context =
                OAuth2AuthorizationContext.withAuthorizedClient(currentAuthorizedClient)
                        .principal(authentication)
                        .build();
        // トークンをリフレッシュ
        OAuth2AuthorizedClient refreshedAuthorizedClient = refreshTokenOAuth2AuthorizedClientProvider.authorize(context);
        // インメモリから既存のトークンを削除
        authorizedClientService.removeAuthorizedClient(
                currentAuthorizedClient.getClientRegistration().getRegistrationId(),
                currentAuthorizedClient.getPrincipalName());
        // インメモリに新しいトークンを登録
        authorizedClientService.saveAuthorizedClient(refreshedAuthorizedClient, authentication);
        logger.debug("Refreshing token completed");
        return refreshedAuthorizedClient.getAccessToken();
    }

    /**
     * OAuth2AuthorizedClientを取得する。
     */
    private OAuth2AuthorizedClient getAuthorizedClient() {
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
    private OAuth2AuthenticationToken getAuthentication() {
        OAuth2AuthenticationToken authentication =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }
}
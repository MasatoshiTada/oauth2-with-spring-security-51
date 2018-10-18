package com.example.client.security.oauth2;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class OAuth2TokenService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate;

    public OAuth2TokenService(OAuth2AuthorizedClientService authorizedClientService, RestTemplate restTemplate) {
        this.authorizedClientService = authorizedClientService;
        this.restTemplate = restTemplate;
    }

    /**
     * アクセストークンの値を取得する。
     */
    public String getAccessTokenValue() {
        OAuth2AccessToken accessToken = getAuthorizedClient().getAccessToken();
        // アクセストークンが期限切れだったらリフレッシュ
        if (isExpiredToken(accessToken)) {
            refresh();
        }
        // FIXME JWTが期限切れ時にリフレッシュトークンでアクセストークンを再取得する
        String tokenValue = accessToken.getTokenValue();
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
    private void refresh() {
        // TODO 未実装
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
     * OAuth2AuthorizedClientを取得する。
     */
    private OAuth2AuthorizedClient getAuthorizedClient() {
        // OAuth2AuthenticationTokenはAuthenticationインタフェース実装クラス
        OAuth2AuthenticationToken authentication =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        // OAuth2AuthorizedClientを取得
        OAuth2AuthorizedClient authorizedClient =
                this.authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
        return authorizedClient;
    }
}
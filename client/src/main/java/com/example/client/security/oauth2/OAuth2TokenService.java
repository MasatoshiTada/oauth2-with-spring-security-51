package com.example.client.security.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
public class OAuth2TokenService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2TokenService.class);

    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2TokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
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

    public Consumer<Map<String, Object>> oAuth2Attributes() {
        return ServletOAuth2AuthorizedClientExchangeFilterFunction
                .oauth2AuthorizedClient(getAuthorizedClient());
    }
}
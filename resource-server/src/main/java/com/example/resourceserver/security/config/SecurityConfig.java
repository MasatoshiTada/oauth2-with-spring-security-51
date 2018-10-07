package com.example.resourceserver.security.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/todos/**").hasAuthority("SCOPE_todo:read")
                .mvcMatchers("/todos/**").hasAuthority("SCOPE_todo:write")
                .anyRequest().authenticated();
        http.oauth2ResourceServer()
                .jwt();
    }
}

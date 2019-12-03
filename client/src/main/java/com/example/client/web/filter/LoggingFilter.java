package com.example.client.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class LoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("======================================");
        logger.debug("## REQUEST");
        String requestMethodAndUri = httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI();
        logger.debug(requestMethodAndUri);
        for (Enumeration<String> headerNames = httpServletRequest.getHeaderNames(); headerNames.hasMoreElements();) {
            String headerName = headerNames.nextElement();
            String headerValue = httpServletRequest.getHeader(headerName);
            logger.debug(headerName + ": " + headerValue);
        }
        logger.debug("======================================");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        logger.debug("======================================");
        logger.debug("## RESPONSE (for " + requestMethodAndUri + ")");
        logger.debug("{}", httpServletResponse.getStatus());
        for (String headerName : httpServletResponse.getHeaderNames()) {
            String headerValue = httpServletResponse.getHeader(headerName);
            logger.debug(headerName + ": " + headerValue);
        }
        logger.debug("======================================");
    }
}
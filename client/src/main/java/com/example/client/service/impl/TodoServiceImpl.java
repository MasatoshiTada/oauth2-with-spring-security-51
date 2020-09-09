package com.example.client.service.impl;

import com.example.client.security.oauth2.OAuth2TokenService;
import com.example.client.service.TodoService;
import com.example.client.service.dto.Todo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final WebClient webClient;
    private final OAuth2TokenService oAuth2TokenService;

    public TodoServiceImpl(WebClient webClient, OAuth2TokenService oAuth2TokenService) {
        this.webClient = webClient;
        this.oAuth2TokenService = oAuth2TokenService;
    }

    @Override
    public List<Todo> findAll() {
        return webClient.get()
                .uri("/todos")
                .attributes(oAuth2TokenService.oAuth2Attributes())
                .retrieve()
                .bodyToFlux(Todo.class)
                .collectList()
                .block();
    }

    @Override
    public void save(Todo todo) {
        webClient.post()
                .uri("/todos")
                .attributes(oAuth2TokenService.oAuth2Attributes())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(todo)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void updateDoneById(Integer id) {
        webClient.patch()
                .uri("/todos/{id}", id)
                .attributes(oAuth2TokenService.oAuth2Attributes())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void deleteById(Integer id) {
        webClient.delete()
                .uri("/todos/{id}", id)
                .attributes(oAuth2TokenService.oAuth2Attributes())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}

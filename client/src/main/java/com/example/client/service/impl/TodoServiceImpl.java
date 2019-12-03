package com.example.client.service.impl;

import com.example.client.service.TodoService;
import com.example.client.service.dto.Todo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final RestTemplate restTemplate;
    private final String resourceServerUri;

    public TodoServiceImpl(RestTemplate restTemplate,
                           @Value("${resource-server.uri}") String resourceServerUri) {
        this.restTemplate = restTemplate;
        this.resourceServerUri = resourceServerUri;
    }

    @Override
    public List<Todo> findAll() {
        ResponseEntity<List<Todo>> responseEntity = restTemplate.exchange(
                resourceServerUri + "/todos", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return responseEntity.getBody();
    }

    @Override
    public void save(Todo todo) {
        restTemplate.postForEntity(resourceServerUri + "/todos", todo, Void.class);
    }

    @Override
    public void updateDoneById(Integer id) {
        restTemplate.patchForObject(resourceServerUri + "/todos/{id}", null, Void.class, id);
    }

    @Override
    public void deleteById(Integer id) {
        restTemplate.delete(resourceServerUri + "/todos/{id}", id);
    }
}

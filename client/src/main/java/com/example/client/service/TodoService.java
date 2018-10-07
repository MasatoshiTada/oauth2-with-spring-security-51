package com.example.client.service;

import com.example.client.service.dto.Todo;

import java.util.List;

public interface TodoService {

    public List<Todo> findAll();

    public void save(Todo todo);

    public void updateDoneById(Integer id);

    public void deleteById(Integer id);
}

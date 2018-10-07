package com.example.resourceserver.service.impl;

import com.example.resourceserver.persistence.entity.Todo;
import com.example.resourceserver.persistence.respository.TodoRepository;
import com.example.resourceserver.service.TodoService;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Iterable<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public void save(Todo todo) {
        todoRepository.save(todo);
    }

    @Override
    public void updateDoneById(Integer id) {
        todoRepository.updateDoneById(id);
    }

    @Override
    public void deleteById(Integer id) {
        todoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return todoRepository.existsById(id);
    }
}

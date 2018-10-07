package com.example.resourceserver.web.controller;

import com.example.resourceserver.persistence.entity.Todo;
import com.example.resourceserver.service.TodoService;
import com.example.resourceserver.web.request.TodoRequest;
import com.example.resourceserver.web.response.TodoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> getAll() {
        List<TodoResponse> todoResponseList =
                StreamSupport.stream(todoService.findAll().spliterator(), false)
                .map(todo -> new TodoResponse(todo))
                .collect(Collectors.toList());
        return  todoResponseList;
    }

    @PostMapping
    public ResponseEntity post(@RequestBody TodoRequest todoRequest) {
        Todo todo = todoRequest.convertToEntity();
        todoService.save(todo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .pathSegment(todo.getId().toString())
                .buildAndExpand()
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateDoneById(@PathVariable Integer id) {
        if (todoService.existsById(id) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "todo not found");
        }
        todoService.updateDoneById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        if (todoService.existsById(id) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "todo not found");
        }
        todoService.deleteById(id);
    }
}

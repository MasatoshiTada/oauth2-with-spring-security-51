package com.example.resourceserver.web.response;

import com.example.resourceserver.persistence.entity.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodoResponse {

    private Integer id;

    private String description;

    private LocalDateTime createdAt;

    private LocalDate deadline;

    private Boolean done;

    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.description = todo.getDescription();
        this.createdAt = todo.getCreatedAt();
        this.deadline = todo.getDeadline();
        this.done = todo.getDone();
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Boolean getDone() {
        return done;
    }
}

package com.example.client.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Todo {

    private Integer id;

    private String description;

    private LocalDateTime createdAt;

    private LocalDate deadline;

    private Boolean done;

    public Todo() {}

    public Todo(String description, LocalDate deadline) {
        this.description = description;
        this.deadline = deadline;
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

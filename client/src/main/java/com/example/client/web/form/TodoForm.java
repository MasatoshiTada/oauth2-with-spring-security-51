package com.example.client.web.form;

import com.example.client.service.dto.Todo;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TodoForm {

    private final String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate deadline;

    public TodoForm(String description, LocalDate deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Todo convertToDto() {
        return new Todo(description, deadline);
    }
}

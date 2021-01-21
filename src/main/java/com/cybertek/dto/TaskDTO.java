package com.cybertek.dto;

import com.cybertek.enums.Status;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TaskDTO {

    private Long Id;
    private ProjectDTO project;
    private String taskSubject;
    private UserDTO assignedEmployee;
    @DateTimeFormat(pattern = "MM/dd/yyyy hh:mm")
    LocalDate assignedDate;

    private Status status;
    private String details;


}

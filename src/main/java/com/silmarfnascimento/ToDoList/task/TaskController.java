package com.silmarfnascimento.ToDoList.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.silmarfnascimento.ToDoList.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    taskModel.setIdUser((UUID) request.getAttribute("idUser"));
    // TaskModel createdTask = this.taskRepository.save(taskModel);

    LocalDateTime currentTime = LocalDateTime.now();
    if (currentTime.isAfter(taskModel.getStartAt()) || currentTime.isAfter(taskModel.getFinishAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("As datas de início e fim devem ser após a data atual");
    }
    if (taskModel.getStartAt().isAfter(taskModel.getFinishAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data inicial deve ser antes da data de término");
    }
    return ResponseEntity.status(HttpStatus.OK).body(taskModel);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    Object idUser = request.getAttribute("idUser");
    List<TaskModel> userTasks = this.taskRepository.findByIdUser((UUID) idUser);
    return userTasks;
  }

  @PutMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {

    TaskModel task = this.taskRepository.findById(id).orElse(null);
    Utils.copyNonNullProperties(taskModel, task);

    return this.taskRepository.save(task);
  }
}

package com.silmarfnascimento.ToDoList.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @Column(name = "id_user")
  private UUID idUser;

  @Column(length = 50)
  private String title;
  private String description;
  private LocalDateTime startAt;
  private LocalDateTime finishAt;

  @CreationTimestamp
  private LocalDateTime createdAt;
  private String priority;

  public void setTitle(String title) throws Exception {
    if (title.length() > 50) {
      throw new Exception("O campo Title deve conter no máximo 50 caracteres");
    }
    this.title = title;
  }
}

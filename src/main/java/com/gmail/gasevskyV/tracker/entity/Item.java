package com.gmail.gasevskyV.tracker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "trackitem")
@Data
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Task can't be empty")
    private String task;
    @NotBlank(message = "Description name can't be empty")
    private String description;
    @CreatedDate
    @Column(name = "created")
    private Date created;

    @LastModifiedDate
    @Column(name = "updated")
    private Date updated;

    private boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    public Item(String task, String description, User user){
        this.task = task;
        this.description = description;
        this.author = user;
    }
}

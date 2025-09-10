package org.raven.hibernate.jpa.test.repository;

import lombok.Data;

import jakarta.persistence.Column;
import java.io.Serializable;

@Data
public class MassTaskId implements Serializable {

    @Column(name = "task_id")
    private String taskId;
    @Column(name = "staff_id")
    private String staffId;
}

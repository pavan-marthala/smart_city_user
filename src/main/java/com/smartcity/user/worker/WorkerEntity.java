package com.smartcity.user.worker;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "worker")
public class WorkerEntity {

    @Id
    @Column("user_id")
    private String userId;

    @Column("skills")
    private String skills;

    @Column("availability")
    private boolean availability;
}

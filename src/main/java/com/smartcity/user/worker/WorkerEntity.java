package com.smartcity.user.worker;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "worker")
public class WorkerEntity {
    @Id
    @Column("id")
    private String id;
    @Column("name")
    private String name;
    @Column("email")
    private String email;
    @Column("village_id")
    private String villageId;
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
    @Column("updated_at")
    @LastModifiedDate
    private Instant updatedAt;
    @Version
    @Column("etag")
    private Long etag;
    @Column("skills")
    private String skills;
    @Column("availability")
    private boolean availability;
}

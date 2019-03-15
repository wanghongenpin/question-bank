package com.queries.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author wanghongen
 * 2018/5/2
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Subject {
    @Id
    private String id;
    private String ownerSpecialty; //归属专业
    private String name;
    private String semester; //学期
    private String createdUsername;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}

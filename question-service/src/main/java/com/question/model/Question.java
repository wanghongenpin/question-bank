package com.question.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghongen
 * 2018/5/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_owner_subject", columnList = "ownerSubject"),
        @Index(name = "idx_type_describe", columnList = "typeDescribe")
//        @Index(name = "idx_title", columnList = "title")
}, uniqueConstraints = @UniqueConstraint(columnNames = {"title", "answer"}))
public class Question {
    @Id
    @Column(unique = true)
    private String id;
    private String ownerSpecialty; //归属
    private String ownerSubject; //归属学科
    private String typeDescribe; //
    private String type; //
    @Column(length = 6000)
    private String title; //
    @Column(length = 2000)
    private String answer; //
    @Transient
    private List<Answer> answers; //
    private String createdUsername;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

}

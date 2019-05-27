package com.queries.models;

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
@Table(
        indexes = {
                @Index(name = "idx_ourse", columnList = "course"),
                @Index(name = "idx_type_describe", columnList = "typeDescribe")
        })
public class Question {
    @Id
    @Column(length = 32)
    private String id;
    private String course; //课程
    private String typeDescribe; //
    private String type; //
    @Column(length = 6000)
    private String question; //
    @Column(length = 2000)
    private String answer; //
    @Transient
    private List<Answer> answers; //
    private String createdUsername;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

}

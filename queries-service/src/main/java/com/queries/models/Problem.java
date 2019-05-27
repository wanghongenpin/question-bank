package com.queries.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghongen
 * 2019-05-19
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Problem {
    @Id
    @Column(length = 32)
    private String id;
    /**
     * 问题
     */
    private String question;
    /**
     * 标题
     */
    private String title;
    /**
     * 试题类型
     */
    private String type;
    /**
     * 回答
     */
    @Transient
    private List<Answer> answers;
    /**
     * 课程
     */
    private String course;
    /**
     * 问题状态
     *
     * @see com.queries.enums.ProblemStatus
     */
    private String status;
    /**
     * 创建用户
     */
    private String createUsername;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

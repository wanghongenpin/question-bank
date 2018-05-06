package com.question.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author wanghongen
 * 2018/5/4
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_question_id", columnList = "questionId"),
        @Index(name = "idx_answer_right", columnList = "answerRight")
})
public class Answer {
    @Id
    private String id;
    private String questionId;
    private String answer;
    private boolean answerRight;
}

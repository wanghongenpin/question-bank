package com.queries.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(length = 2000)
    private String answer;
    private boolean answerRight;
}

package com.question.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author wanghongen
 * 2018/5/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    private String username;
    private String name;
    private String password;
    private String gender;
    private String specialty; //专业
    private String teachingCenter;//教学中心
    private String enrolmentTime;//
    private String dateOfBirth;//
    private String identityCardNumber;//
    private String phone;//
    private String address;//
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

}

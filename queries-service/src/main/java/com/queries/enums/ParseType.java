package com.queries.enums;

import lombok.Getter;

/**
 * @author wanghongen
 * 2019-05-22
 */
@Getter
public enum ParseType {
    USER(1),//用户
    COURSE(2), //课程
    QUESTION(3), //问题
    ;
    private int type;

    ParseType(int type) {
        this.type = type;
    }
}

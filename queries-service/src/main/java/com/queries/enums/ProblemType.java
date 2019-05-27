package com.queries.enums;

/**
 * @author wanghongen
 * 2019-05-26
 */

public enum ProblemType {
    SINGLE_CHOICE("单选题"),
    MULTIPLE_CHOICE("多选题"),
    JUDGEMENT("判断题"),
    ;

    private String label;

    ProblemType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

package com.queries.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wanghongen
 * 2019-05-13
 */
@Getter
public enum ProblemStatus {
    /**
     * 待处理
     */
    OPEN("open", "打开"),
    /**
     * 提交的
     */
    SUBMITTED("submitted", "提交"),
//    /**
//     * 重新打开
//     */
//    REOPEN("reopen", "重新打开"),
    /**
     * 完成的
     */
    FINISHED("finished", "处理完成"),
    /**
     * 关闭的
     */
    CLOSED("closed", "关闭");

    private String status;
    private String statusLabel;

    ProblemStatus(String status, String statusLabel) {
        this.status = status;
        this.statusLabel = statusLabel;
    }

    private static Map<String, ProblemStatus> problemStatusMap = Stream.of(values()).collect(Collectors.toMap(ProblemStatus::getStatus, problemStatus -> problemStatus));

    public static ProblemStatus statusOf(String status) {
        return problemStatusMap.get(status);
    }
}

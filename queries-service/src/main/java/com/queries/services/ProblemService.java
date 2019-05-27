package com.queries.services;

import com.queries.models.Problem;
import com.queries.request.ProblemSubmit;

import java.util.Optional;

/**
 * @author wanghongen
 * 2019-05-22
 */
public interface ProblemService {
    /**
     * 创建问题
     *
     * @param problem Problem
     */
    Problem create(Problem problem);

    /**
     * 获取或创建
     *
     * @param problem Problem
     */
    Problem getProblemOrCreate(Problem problem);

    /**
     * 获取问题
     *
     * @param id id
     */
    Optional<Problem> getProblem(String id);
    /**
     * 打开问题
     */
    void open(Problem problem);
    /**
     * 提交
     */
    void submit(ProblemSubmit problemSubmit);

    /**
     * 完成
     */
    void finish(Problem problem);

}

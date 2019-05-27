package com.queries.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author wanghongen
 * 2019-05-27
 */
@Data
public class ProblemSubmit {
    @NotBlank(message = "ID不能为空")
    private String id;
    @NotNull(message = "回答不能为空")
    @Size(min = 1, message = "回答不能为空")
    private List<String> answerIds;
}

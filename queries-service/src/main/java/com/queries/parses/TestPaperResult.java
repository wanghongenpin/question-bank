package com.queries.parses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wanghongen
 * 2019-05-26
 */
@AllArgsConstructor
@Data
public class TestPaperResult {
    private String totalScore;
    private List<Boolean> results;
}

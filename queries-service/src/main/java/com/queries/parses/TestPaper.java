package com.queries.parses;

import lombok.Builder;
import lombok.Data;

/**
 * @author wanghongen
 * 2019-05-18
 */
@Builder
@Data
public class TestPaper {
    private String chapterName;
    private String chapterSerialNumber;
    private int score;
    private String paperId;
    private String ruid;
    private String token;
}

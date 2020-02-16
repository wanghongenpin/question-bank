package com.queries;

import com.queries.parses.TestPaper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wanghongen
 * 2020/2/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPaperTest {
    @Resource
    private Crawling crawling;

    @Test
    public void test() {
        TestPaper testPaper = TestPaper.builder().token("0275F9D9751E405289CF5EB54006DBBB")
                .chapterName("计算机网络概述")
                .chapterSerialNumber("08020301")
                .paperId("08020301")
                .ruid("162352201455")
                .build();
        crawling.crawlingTestPaper("19106606055","计算机网络(计算机)",testPaper);
    }


}

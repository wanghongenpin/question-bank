package com.queries;

import com.queries.parses.TestPaper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    public void test0() throws InterruptedException {
//        TestPaper testPaper = TestPaper.builder().token("BAFF14286AB64BA092B3E2F686505185")
//                .chapterName("数据处理")
//                .chapterSerialNumber("08180302")
//                .paperId("08180302")
//                .ruid("170232399298")
//                .build();
//        crawling.crawlingTestPaper("19106606055","微机原理及接口技术",testPaper);
//        TimeUnit.MINUTES.sleep(3L);
        TestPaper testPaper = TestPaper.builder().token("D16E6172E07947BD8877EE52BED45DEB")
                .chapterName("物理层")
                .chapterSerialNumber("08020302")
                .paperId("08020302")
                .ruid("170231056163")
                .build();
        crawling.crawlingTestPaper("19106606055","计算机网络(计算机)",testPaper);
        TimeUnit.MINUTES.sleep(3L);
    }


}

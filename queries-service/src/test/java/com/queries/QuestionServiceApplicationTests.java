package com.queries;

import com.common.utils.MD5;
import com.queries.api.ApiService;
import com.queries.models.Question;
import com.queries.parses.HtmlParse;
import com.queries.request.QuestionQuery;
import com.queries.services.CourseService;
import com.queries.services.QuestionService;
import com.queries.services.UserService;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionServiceApplicationTests {
    public static void main(String[] args) {
        String md5 = MD5.md5("4AAE443188A94A4DAA4A3438B238C199");
        System.out.println(md5);
        final String s = StringEscapeUtils.unescapeHtml4("p ＝ &amp;x");
        System.out.println(s);
    }

    @Resource
    private ApiService apiService;
    @Resource
    private HtmlParse parse;
    @Resource
    private QuestionService questionService;
    @Resource
    private CourseService courseService;
    @Resource
    private UserService userService;
    @Resource
    private Crawling crawling;

    @Test
    public void contextLoads() throws InterruptedException {
        final QuestionQuery questionQuery = new QuestionQuery();
        questionQuery.setQuestionType("判断题");

        questionQuery.setSize(500);
        for (int page = 8; ; page++) {
            questionQuery.setPage(page);
            final Page<Question> list = questionService.list(questionQuery);
            final ArrayList<Question> questions = new ArrayList<>(500);
            list.getContent().forEach(question -> apiService.getQuestion(question.getId(), "zdyj2web=B65312F7DA758D8D98DBE8AEE821BDBA")
                    .run(e -> {
                    }, right -> {
                        right.setCourse(question.getCourse());
                        right.setType(question.getType());
                        right.setCreatedUsername(question.getCreatedUsername());
                        questions.add(right);

                    }));
            questionService.batchSaveQuestion(questions);
            if (list.getTotalPages() < page) {
                break;
            }
        }
//        User user = userService.getUser("16212116009").get();
//        System.out.println(user);
//        Either<ApiException, String> apiExceptionStringEither = apiService.courseBankLogin("3C258E57F9DB4362A4C07ED135FF911B&");
//        System.out.println(apiExceptionStringEither);
//        apiService.learningCourses("2D35CF22CFB3450AA4CE46B0D0AE73BB").forEach(System.out::println);
//        String cookie = "zdyj2web=1EA6C9B87FD2414FB0E9A0070AA57F18";
//        crawling.crawlingTestPaper("19106606055", "网上学习导论", "698A86D026844600A86DEA432B250315", "00340104", "271731016987");
//        crawling.automaticOnlineTestPaper("1D3011818C54489BB2E9EA0DC2E1868A");
//        TimeUnit.MINUTES.sleep(3);
//        System.out.println("-------------------------------------
//        System.out.println(questionService.getQuestion("000103081008"));
//        ResponseEntity<String> courses = apiService.getQuestion("000103091010", "zdyj2web=66156CB052BC4FE66990329F85894F9D");
//        Question question = parse.parseQuestion(courses.getBody());
//        System.out.println(question.getAnswer());
    }

}

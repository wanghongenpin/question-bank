package com.queries;

import com.common.utils.Either;
import com.common.utils.MD5;
import com.queries.api.ApiService;
import com.queries.exceptions.ApiException;
import com.queries.models.Question;
import com.queries.parses.HtmlParse;
import com.queries.services.QuestionService;
import com.queries.services.SubjectService;
import com.queries.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import java.util.Collection;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class QuestionServiceApplicationTests {
    public static void main(String[] args) {
        String md5 = MD5.md5("4AAE443188A94A4DAA4A3438B238C199");
        System.out.println(md5);
    }

    @Resource
    private ApiService apiService;
    @Resource
    private HtmlParse parse;
    @Resource
    private QuestionService questionService;
    @Resource
    private SubjectService subjectService;
    @Resource
    private UserService userService;

    //    @Test
    public void contextLoads() throws InterruptedException {
//        User user = userService.getUser("16212116009").get();
//        System.out.println(user);
        Either<ApiException, String> apiExceptionStringEither = apiService.subjectBankLogin("3C258E57F9DB4362A4C07ED135FF911B&");
        System.out.println(apiExceptionStringEither);

//        String cookie = "zdyj2web=1EA6C9B87FD2414FB0E9A0070AA57F18";
        System.out.println("-------------------------------------");
        ResponseEntity<String> subjects = apiService.getSubjects(apiExceptionStringEither.getRight());
        System.out.println(subjects);
    }

    private void save(Collection<Question> questions) {
        if (questions.size() >= 50) {
            questionService.batchSaveQuestion(questions);
        }
    }

}

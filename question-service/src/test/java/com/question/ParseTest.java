package com.question;

import com.alibaba.fastjson.JSON;
import com.question.model.Answer;
import com.question.model.Question;
import com.question.model.Subject;
import com.question.model.User;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author wanghongen
 * 2018/5/2
 */
public class ParseTest {
    @Test
    public void testParseQuestion() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("question.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        String describe = StringUtils.substringAfter(doc.select("div.q-content div").first().text(), "题型描述:").trim();
        describe=StringUtils.substringBefore(describe," ");
        Elements titleElements = doc.select("h4.test-title");
        String title;
        if (titleElements.select("q").isEmpty()) {
            title = titleElements.first().text();
        } else {
            title = titleElements.select("q").first().text();
        }
        Elements elements = doc.select("div.shiti-item-left div");
        Elements answerIds = elements.select("p.answer");
        Elements answerNames = elements.select("div p").not("p.answer");
        List<Answer> answers = new ArrayList<>(answerIds.size());
        String questionAnswer = null;
        for (int i = 0; i < answerIds.size(); i++) {
            Element element = answerIds.get(i);
            String id = element.attr("data-o-id").trim();
            Element answerElement;
            String answer;
            if (answerNames.isEmpty()) {
                answerElement = elements.select("div").not("div.yizuo").not("div[style]").get(i);
            } else {
                answerElement = answerNames.get(i);
            }
            answer = answerElement.text();
            boolean right = "1".equals(element.attr("data-o-right-flag"));
            if (right) {
                questionAnswer = answer;
            }
            Answer a = Answer.builder().id(id).answer(answer).answerRight(right).build();
            answers.add(a);
        }
        Question question = Question.builder().answer(questionAnswer).typeDescribe(describe).title(title).answers(answers).build();
//        System.out.println(text);
        System.out.println(question);
    }

    @Test
    public void testParseQuestions() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("questions.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        Elements elements = doc.select("script[type=text/javascript]");
        Optional<Element> first = elements.stream().filter(element -> element.data().contains("var questionsJson = [")).findFirst();
        String data = first.get().data();
        String questionsJson = StringUtils.substringBetween(data, "var questionsJson = ", ";");
        List<Map> maps = JSON.parseArray(questionsJson, Map.class);
    }

    @Test
    public void testParseSubjectList() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("subjectList.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        try {
            Elements elements = doc.select("div[class=contain]").select("div[class=class-list-ner] ul");
            List<Subject> subjectList = elements.stream()
                    .filter(element -> !element.select("h3[class=class-list-title]").isEmpty())
                    .map(element -> {
                        String semester = element.select("h3[class=class-list-title]").text();
                        Elements subjects = element.select("li[class=class-list-li]");
                        return subjects.stream()
                                .map(subject -> {
                                    String href = subject.select("div[class=text_center] a").first().attr("href");
                                    String id = StringUtils.substringAfter(href, "studentCourseId=");
                                    String name = subject.select("p").first().text();
                                    return Subject.builder().id(id).name(name).semester(semester).build();
                                }).collect(toList());
                    })
                    .flatMap(Collection::stream)
                    .collect(toList());

            System.out.println(subjectList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseUser() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("user.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        try {
            Elements elements = doc.select("form[name=myeditform] table table table tr");
            String username = elements.eq(1).select("td font").last().text();
            String name = elements.eq(2).select("td font").last().text();
            String gender = elements.eq(3).select("td font").last().text();
            String specialty = elements.eq(4).select("td font").last().text();
            String teachingCenter = elements.eq(5).select("td font").last().text();
            String enrolmentTime = elements.eq(6).select("td font").last().text();
            String dateOfBirth = elements.eq(7).select("td font").last().text();
            String identityCardNumber = elements.eq(8).select("td font").last().text();
            String phone = elements.eq(32).select("input").val();
            String address = elements.eq(34).select("textarea").text();
            User user = User.builder().username(username).name(name).gender(gender).specialty(specialty)
                    .teachingCenter(teachingCenter).enrolmentTime(enrolmentTime).dateOfBirth(dateOfBirth)
                    .identityCardNumber(identityCardNumber).phone(phone).address(address).build();
            System.out.println(user);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testParseToken() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("login.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        String url = doc.select("input[name=gointo]").attr("onclick");
        String token = StringUtils.substringBetween(url, "/getmain?ptopid=", "&sid=");
        System.out.println(token);
    }
}

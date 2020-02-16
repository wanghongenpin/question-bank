package com.queries;

import com.alibaba.fastjson.JSON;
import com.queries.models.Answer;
import com.queries.models.Course;
import com.queries.models.Question;
import com.queries.models.User;
import com.queries.parses.HtmlParse;
import com.queries.parses.TestPaperResult;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author wanghongen
 * 2018/5/2
 */
public class ParseTest {
    private static String apply(Element img) {
        String src = img.attr("src");
        img.attr("src", "http://222.22.63.178" + src);
        return img.outerHtml();
    }

    @Test
    public void testParseResult() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("testPaperResult.html");
        final String html = StreamUtils.copyToString(inputStream, Charset.forName("GBK"));
        final HtmlParse htmlParse = new HtmlParse();
        final TestPaperResult testPaperResult = htmlParse.parseSubmitTestPaperResult(html);
        System.out.println(testPaperResult);
    }

    @Test
    public void testParseLearningCourses() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("homepage.html");
        final String html = StreamUtils.copyToString(inputStream, Charset.forName("GBK"));
        final HtmlParse htmlParse = new HtmlParse();
        htmlParse.parseLearningCourses(html);
    }

    @Test
    public void testParseTestPaper() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("testPaper.html");
        final String html = StreamUtils.copyToString(inputStream, Charset.forName("GBK"));
        final HtmlParse htmlParse = new HtmlParse();
        System.out.println(JSON.toJSONString(htmlParse.parseTestPaperProblems(html)));
    }

    @Test
    public void testParseQuestion() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("question.html");
        final String html = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
        final HtmlParse htmlParse = new HtmlParse();
        System.out.println(JSON.toJSONString(htmlParse.parseQuestion(html)));
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
        System.out.println(maps);
    }

    @Test
    public void testParseSubjectList() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("courseList.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        try {
            Elements elements = doc.select("div[class=contain]").select("div[class=class-list-ner] ul");
            List<Course> courseList = elements.stream()
                    .filter(element -> !element.select("h3[class=class-list-title]").isEmpty())
                    .map(element -> {
                        String semester = element.select("h3[class=class-list-title]").text();
                        Elements courses = element.select("li[class=class-list-li]");
                        return courses.stream()
                                .map(course -> {
                                    String href = course.select("div[class=text_center] a").first().attr("href");
                                    String id = StringUtils.substringAfter(href, "studentCourseId=");
                                    String name = course.select("p").first().text();
                                    return Course.builder().id(id).name(name).semester(semester).build();
                                }).collect(toList());
                    })
                    .flatMap(Collection::stream)
                    .collect(toList());

            System.out.println(courseList);

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

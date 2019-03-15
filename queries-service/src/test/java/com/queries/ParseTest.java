package com.queries;

import com.alibaba.fastjson.JSON;
import com.queries.models.Answer;
import com.queries.models.Question;
import com.queries.models.Subject;
import com.queries.models.User;
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
    private static String apply(Element img) {
        String src = img.attr("src");
        img.attr("src", "http://222.22.63.178" + src);
        return img.outerHtml();
    }

    @Test
    public void testParseQuestion() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("question.html");
        Document doc = Jsoup.parse(inputStream, "utf-8", "http://example.com/");
        String describe = StringUtils.substringAfter(doc.select("div.q-content div").first().text(), "题型描述:").trim();
        describe = StringUtils.substringBefore(describe, " ");
        Element titleElement = doc.getElementsByClass("test-title").first();
        String title;
        Elements pElements = titleElement.select("p");
        if (pElements.isEmpty()) {
            title = replaceImgSrc(titleElement);
        } else {
            title = replaceImgSrc(pElements);
        }
        Elements elements = doc.select("div.shiti-item-left div");
        Elements answerIds = elements.select("p.answer");
        List<Answer> answers = new ArrayList<>(answerIds.size());
        StringBuilder questionAnswer = new StringBuilder();

        Map<String, String> optionOwnerMap = new HashMap<>();
        String optionText = doc.select("div.tip-container b").text();

        for (int index = 0; index < answerIds.size(); index++) {

            Element element = answerIds.get(index);
            String id = element.attr("data-o-id").trim();
            String answer;

            answer = replaceImgSrc(element.nextElementSibling());
            String option;
            Elements span = element.select("span");
            if (span.isEmpty()) {
                option = index == 0 ? "A" : "B";
            } else {
                option = span.first().text().substring(0, 1);
            }

            if (StringUtils.isBlank(answer))
                answer = option;
            optionOwnerMap.put(option, answer);
            boolean right = optionText.contains(option);
            Answer a = Answer.builder().id(id).answer(answer).answerRight(right).build();
            answers.add(a);
        }
        String[] ownerOptions = " ".split(optionText);
        if (ownerOptions.length > 1) {
            for (int i = 0; i < ownerOptions.length; i++) {
                questionAnswer.append(i + 1)
                        .append(". ")
                        .append(optionOwnerMap.get(ownerOptions[i]));
                if (i < (ownerOptions.length - 1)) {
                    questionAnswer.append("<br/>");
                }
            }
        } else {
            String value = optionOwnerMap.get(optionText.trim());
            questionAnswer.append(value == null ? "" : value);
        }
        String answer = questionAnswer.toString();
        System.out.println(optionOwnerMap);
        System.out.println(answer);
        Question question = Question.builder().answer(answer).typeDescribe(describe).title(title).answers(answers).build();
        System.out.println(question);
    }

    private String replaceImgSrc(Elements elements) {
        Elements images = elements.select("img");
        if (images.isEmpty()) {
            if (elements.text().isEmpty())
                return elements.select("p").html();
            return elements.text();
        } else {
            return elements.text() + images.stream().map(ParseTest::apply).reduce("", String::concat);
        }
    }

    private String replaceImgSrc(Element element) {
        Elements images = element.select("img");
        if (images.isEmpty()) {
            if (element.text().isEmpty())
                return element.select("p").html();
            return element.text();
        }

        List<String> srcList = images.stream().map(img -> {
            System.out.println(img);
            return img.attr("src");
        }).collect(toList());
        String html = element.html().trim();
        for (String src : srcList) {
            html = html.replace(src, "http://222.22.63.178" + src);
        }
        return html;

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

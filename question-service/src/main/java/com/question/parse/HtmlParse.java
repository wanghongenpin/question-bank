package com.question.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.question.model.Answer;
import com.question.model.Question;
import com.question.model.Subject;
import com.question.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author wanghongen
 * 2018/5/1
 */
@Slf4j
@Component
public class HtmlParse {

    public Optional<String> parseToken(String html) {
        Document doc = Jsoup.parse(html);
        String url = doc.select("input[name=gointo]").attr("onclick");
        String token = StringUtils.substringBetween(url, "/getmain?ptopid=", "&sid=");
        return Optional.ofNullable(token);
    }

    public Optional<User> parseUser(String html) {
        Document doc = Jsoup.parse(html);
        User user = null;
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
            user = User.builder().username(username).name(name).gender(gender).specialty(specialty)
                    .teachingCenter(teachingCenter).enrolmentTime(enrolmentTime).dateOfBirth(dateOfBirth)
                    .identityCardNumber(identityCardNumber).phone(phone).address(address).build();
        } catch (Exception e) {
            log.error("解析用户失败 html={}, e={}", html, e);
        }
        return Optional.ofNullable(user);
    }

    public Set<Subject> parseSubjectList(String subjectListHtml) {
        Document doc = Jsoup.parse(subjectListHtml);
        Elements elements = doc.select("div[class=contain]").select("div[class=class-list-ner] ul");
        return elements.stream()
                .filter(element -> !element.select("h3[class=class-list-title]").isEmpty())
                .map(element -> {
                    String semester = element.select("h3[class=class-list-title]").text();
                    Elements subjects = element.select("li[class=class-list-li]");
                    return subjects.stream()
                            .map(subject -> {
                                String href = subject.select("div[class=text_center] a").first().attr("href");
                                String id = StringUtils.substringAfter(href, "studentCourseId=");
                                String name = subject.getElementsByTag("p").first().text();
                                return Subject.builder().id(id).name(name).semester(semester).build();
                            }).collect(toList());
                })
                .flatMap(Collection::stream)
                .collect(toSet());
    }


    public List<JSONObject> parseQuestions(String questionsHtml) {
        Document doc = Jsoup.parse(questionsHtml);
        Elements elements = doc.select("script[type=text/javascript]");
        Optional<Element> first = elements.stream().filter(element -> element.data().contains("var questionsJson = [")).findFirst();
        String data = first.get().data();
        String questionsJson = StringUtils.substringBetween(data, "var questionsJson = ", ";");
        return JSON.parseArray(questionsJson, JSONObject.class);
    }

    public Question parseQuestion(String questionHtml) {
        Document doc = Jsoup.parse(questionHtml);
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
        Elements answerNames = elements.select("div p").not("p.answer");
        List<Answer> answers = new ArrayList<>(answerIds.size());
        StringBuilder questionAnswer = new StringBuilder();

        int rightCount = 1;
        for (int i = 0; i < answerIds.size(); i++) {

            Element element = answerIds.get(i);
            String id = element.attr("data-o-id").trim();
            String answer;
            Element answerElement;
            if (answerNames.isEmpty()) {
                answerElement = elements.select("div").not("div.yizuo").not("div[style]").get(i);
            } else {
                answerElement = answerNames.get(i);
            }
            answer = replaceImgSrc(answerElement);
            boolean right = "1".equals(element.attr("data-o-right-flag"));
            if (right) {
                if (describe.contains("多选题")) {
                    questionAnswer.append(rightCount).append(". ").append(answer).append("<br/>");
                    rightCount++;
                } else {
                    questionAnswer.append(answer);
                }
            }
            Answer a = Answer.builder().id(id).answer(answer).answerRight(right).build();
            answers.add(a);
        }
        String answer;
        if (rightCount > 1) {
            answer = questionAnswer.substring(0, questionAnswer.length() - 5);
        } else {
            answer = questionAnswer.toString();
        }
        return Question.builder().typeDescribe(describe).answer(answer).title(title).answers(answers).build();
    }

    private String replaceImgSrc(Elements elements) {
        Elements images = elements.select("img");
        if (images.isEmpty()) {
            return elements.text();
        }

        return elements.text() + images.stream().map(img -> {
            String src = img.attr("src");
            img.attr("src", "http://222.22.63.178" + src);
            return img.outerHtml();
        }).reduce("", String::concat);
    }

    private String replaceImgSrc(Element element) {
        Elements images = element.select("img");
        if (images.isEmpty()) {
            return element.text();
        } else {
            return element.text() + images.stream().map(img -> {
                String src = img.attr("src");
                img.attr("src", "http://222.22.63.178" + src);
                return img.outerHtml();
            }).reduce("", String::concat);
        }
    }
}


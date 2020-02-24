package com.queries.parses;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.queries.enums.ProblemType;
import com.queries.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author wanghongen
 * 2018/5/1
 */
@Slf4j
@Component
public class HtmlParse {
    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .requestFactory(HttpComponentsClientHttpRequestFactory.class)
            .messageConverters(new StringHttpMessageConverter(Charset.forName("GBK")))
            .build();

    /**
     * 解析token
     */
    public Optional<String> parseToken(String html) {

        Document doc = Jsoup.parse(html);
        String url = doc.select("input[name=gointo]").attr("onclick");
        String token = StringUtils.substringBetween(url, "/getmain?ptopid=", "&sid=");
        return Optional.ofNullable(token);
    }

    /**
     * 解析学习课程
     */
    public List<LearningCourses> parseLearningCourses(String html) {
        Document doc = Jsoup.parse(html);

        final Elements earningCourses = doc.select("td.mytd1 p");
        //学习课程列表
        return earningCourses.eq(1).select("a").stream().map(a -> {
            //课程链接
            final String courseUrl = a.attr("href");
            //课程名字
            final String courseName = a.select("font font").text();
            //课程详情 获取在线考试链接
            final List<TestPaper> paperList = parse(courseUrl)
                    .select("a")
                    .stream()
                    .filter(link -> link.select("font").text().contains("在线测试"))
                    .findAny()
                    .map(e -> e.attr("href")) //在线考试链接
                    .filter(url -> url.startsWith("http"))
                    .map(url -> {
                        //获取课程考试列表数据
                        final Document testPaper = parse(url);
                        final String content = testPaper.select("meta").eq(1).attr("content");
                        //获取重定向url
                        final String testPaperUrl = StringUtils.substringBetween(content, ";url='", "'");
                        final Document document = parse(testPaperUrl);
                        final Elements testPapers = document.select("table tbody tr[align=center]").nextAll();
                        return testPapers.stream()
                                .map(paper -> {
                                    final Elements elements = paper.select("td");
                                    final String serialNumber = elements.eq(1).text();//编号
                                    final String name = elements.eq(3).text();//名称
                                    final String score = elements.eq(5).text();//最好分数
                                    final String link = elements.eq(8).select("a").attr("href");//链接
                                    //{ptopid=2D35CF22CFB3450AA4CE46B0D0AE73BB, ruid=191425216429, zhang=08170101}
                                    final Map<String, String> params = extractQueryParams(link);
                                    return TestPaper.builder()
                                            .chapterSerialNumber(serialNumber)
                                            .chapterName(name)
                                            .score(NumberUtils.toInt(score))
                                            .paperId(params.get("zhang"))
                                            .ruid(params.get("ruid"))
                                            .token(params.get("ptopid"))
                                            .build();
                                }).collect(toList());
                    }).orElse(Collections.emptyList());

            return new LearningCourses(courseName, paperList);
        }).collect(Collectors.toList());
    }

    /**
     * 解析用户信息
     */
    public Optional<User> parseUser(String html) {
        log.debug("查询用户信息  html={}", html);

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

    /**
     * 解析课程列表
     */
    public Set<Course> parseSubjectList(String courseListHtml) {
        log.debug("查询课程列表 {}", courseListHtml);
        Document doc = Jsoup.parse(courseListHtml);
        Elements elements = doc.select("div[class=contain]").select("div[class=class-list-ner] ul");

        return elements.stream()
                .filter(element -> !element.select("h3[class=class-list-title]").isEmpty())
                .map(element -> {
                    String semester = element.select("h3[class=class-list-title]").text();
                    Elements courses = element.select("li[class=class-list-li]");

                    return courses.stream()
                            .map(course -> {
                                String href = course.select("div[class=text_center] a").first().attr("href");
                                String id = StringUtils.substringAfter(href, "studentCourseId=");
                                String name = course.getElementsByTag("p").first().text();
                                return Course.builder().id(id).name(name).semester(semester).build();
                            }).collect(toList());
                })
                .flatMap(Collection::stream)
                .collect(toSet());
    }


    public List<JSONObject> parseQuestions(String questionsHtml) {
        log.debug("查询试题列表 {}", questionsHtml);

        Document doc = Jsoup.parse(questionsHtml);
        Elements elements = doc.select("script[type=text/javascript]");
        Optional<Element> first = elements.stream().filter(element -> element.data().contains("var questionsJson = [")).findFirst();

        return first.map(data -> JSON.parseArray(StringUtils.substringBetween(data.data(), "var questionsJson = ", ";"), JSONObject.class)).orElse(Collections.emptyList());

    }

    /**
     * 解析问题
     */
    public Question parseQuestion(String questionHtml) {
        log.debug("查询试题 {}", questionHtml);
        Document doc = Jsoup.parse(questionHtml);
        final String qid = doc.selectFirst("div.q-content").attr("data-q-id").trim();
        String describe = StringUtils.substringAfter(doc.select("div.q-content div").first().text(), "题型描述:").trim();
        describe = StringUtils.substringBefore(describe, " ");

        Element titleElement = doc.getElementsByClass("test-title").first();
        String question;
        Elements pElements = titleElement.select("p");
        if (pElements.isEmpty()) {
            question = replaceImgSrc(titleElement);
        } else {
            question = replaceImgSrc(pElements);
        }
        Elements elements = doc.select("div.shiti-item-left div");
        Elements answerIds = elements.select("p.answer");
        StringBuilder questionAnswer = new StringBuilder();

        Map<String, String> optionOwnerMap = new HashMap<>();
        String optionText = doc.select("div.tip-container b").text();

        AtomicBoolean first = new AtomicBoolean(true);
        final List<Answer> answers = answerIds.stream().map(element -> {
            String id = element.attr("data-o-id").trim();
            String answer = replaceImgSrc(element.nextElementSibling());
            String option;
            Elements span = element.select("span");
            if (span.isEmpty()) {
                option = first.get() ? "A" : "B";
                first.set(false);
            } else {
                option = span.first().text().substring(0, 1);
            }
            if (StringUtils.isBlank(answer))
                answer = option;
            optionOwnerMap.put(option, answer);
            boolean right = optionText.contains(option);
            return Answer.builder().id(id).answer(answer).answerRight(right).build();
        }).collect(toList());


        String[] ownerOptions = optionText.split(" ");
        if (ownerOptions.length > 1) {
            for (int i = 0; i < ownerOptions.length; i++) {
                questionAnswer.append(i + 1)
                        .append(". ")
                        .append(optionOwnerMap.get(ownerOptions[i]));
                if (i < ownerOptions.length - 1) {
                    questionAnswer.append("<br/>");
                }
            }
        } else {
            String value = optionOwnerMap.get(optionText.trim());
            questionAnswer.append(value == null ? "" : value);
        }
        String answer = questionAnswer.toString();
        return Question.builder().id(qid).typeDescribe(describe).answer(answer).question(question).answers(answers).build();
    }

    /**
     * 解析试卷问题
     *
     * @param testPaperHtml html
     * @return Collection<Problem>
     */
    public List<Problem> parseTestPaperProblems(String testPaperHtml) {
        log.debug("解析试卷 {}", testPaperHtml);
        final Document document = Jsoup.parse(testPaperHtml);
        final String title = document.selectFirst("div span").text();
        Elements elements = document.select("form > div > center > table > tbody > tr");
        //单选题
        final Elements singleChoice = elements.eq(2).select("td > table > tbody > tr");
        final List<Problem> singleChoiceProblems = getProblems(singleChoice, title, ProblemType.SINGLE_CHOICE);

        //多选题
        final Elements multipleChoice = elements.eq(4).select("td > table > tbody > tr");
        final List<Problem> multipleChoiceProblems = getProblems(multipleChoice, title, ProblemType.MULTIPLE_CHOICE);
        //判断题
        final Elements judgement = elements.eq(6).select("td > table > tbody > tr");
        final List<Problem> judgementProblems = getProblems(judgement, title, ProblemType.JUDGEMENT);

        final ArrayList<Problem> problems = new ArrayList<>();
        problems.addAll(singleChoiceProblems);
        problems.addAll(multipleChoiceProblems);
        problems.addAll(judgementProblems);

        return problems;
    }

    /**
     * 获取问题
     */
    private List<Problem> getProblems(Elements singleChoice, String title, ProblemType problemType) {
        List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < singleChoice.size(); i += 2) {
            String question = singleChoice.get(i).select("tr td").text().substring(2);
            for (int j = 1; j <= 5; j++) {
                if (question.startsWith(j + ".") || question.startsWith(j + "．")) {
                    question = question.substring(2).trim();
                }
            }
            if (question.endsWith("（ ）。") || question.endsWith("（ ）") || question.endsWith("( )")||question.endsWith("( )。")) {
                question = StringUtils.substringBeforeLast(question, "（ ）").trim();
                question = StringUtils.substringBeforeLast(question, "( )").trim();
                question = StringUtils.substringBeforeLast(question, "( )。").trim();
            }

            final List<Answer> answerList = singleChoice.get(i + 1).select("input")
                    .stream()
                    .map(e -> {
                        final String name = e.attr("name");
                        final String value = e.attr("value");
                        final String answer = e.nextSibling().toString().trim().replace(value + "、", "");
                        final char ch = name.charAt(name.length() - 1);
                        String id = name;
                        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
                            id = name.substring(0, name.length() - 1);
                        return Answer.builder().id(UUID.randomUUID().toString().replaceAll("-", ""))
                                .questionId(id).answer(StringEscapeUtils.unescapeHtml4(answer)).symbol(value).build();
                    }).collect(toList());
            //如果回答为空，代表无需答题
            if (!answerList.isEmpty()) {
                final Answer answer = answerList.get(0);
                final Problem problem = Problem.builder().id(answer.getQuestionId()).question(question)
                        .title(title).type(problemType.getLabel()).answers(answerList).build();
                problems.add(problem);
            }
        }

        return problems;
    }

    /**
     * 解析提交答案返回结果
     */
    public TestPaperResult parseSubmitTestPaperResult(String html) {
        final Document doc = Jsoup.parse(html);
        final String totalScore = doc.select("span span").text();
        final List<Boolean> results = doc.select("li font").stream()
                .filter(it -> !it.text().contains("无题，直接"))
                .map(it -> {
                    final String[] arr = StringUtils.substringsBetween(it.text(), "[", "]");
                    final String result = arr[1];
                    return ("对").equals(result);
                }).collect(toList());
        return new TestPaperResult(totalScore, results);
    }

    private String replaceImgSrc(Elements elements) {
        Elements images = elements.select("img");
        if (images.isEmpty()) {
            if (elements.text().isEmpty())
                return elements.select("p").html();
            return elements.text();
        }

        return elements.text() + replaceImagesSrc(images);
    }

    private String replaceImgSrc(Element element) {
        Elements images = element.select("img");
        if (images.isEmpty()) {
            if (element.text().isEmpty())
                return element.select("p").html();
            return element.text();
        } else {
            return element.text() + replaceImagesSrc(images);
        }
    }

    /**
     * 替换图片链接
     */
    private String replaceImagesSrc(Elements images) {

        return images.stream()
                .map(img -> {
                    String src = img.attr("src");
                    img.attr("src", "http://222.22.63.178" + src);
                    return img.outerHtml();
                })
                .reduce("", String::concat);
    }

    /**
     * 提取url查询参数
     *
     * @param url URL
     * @return Map<String, String>
     */
    private static Map<String, String> extractQueryParams(String url) {
        Map<String, String> result = new HashMap<>();
        String queryUrl = null;
        try {
            queryUrl = StringUtils.substringAfter(URLDecoder.decode(url, "GBK"), "?");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (queryUrl != null) {
            for (String query : org.springframework.util.StringUtils.tokenizeToStringArray(queryUrl, "&")) {
                if (query.contains("=")) {
                    final int index = query.indexOf("=");
                    String key = query.substring(0, index);
                    final String value = query.substring(index + 1);
                    result.put(key, value);
                } else {
                    result.put(query, null);
                }

            }
        }

        return result;
    }

    /**
     * 解析链接资源
     *
     * @param url url
     * @return Document
     */
    private Document parse(String url) {
        final String html = restTemplate.getForObject(url, String.class);
        if (html == null) {
            return new Document("");
        }
        return Jsoup.parse(html);
    }
}


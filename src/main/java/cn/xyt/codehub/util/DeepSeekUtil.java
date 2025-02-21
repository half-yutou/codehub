package cn.xyt.codehub.util;

import cn.xyt.codehub.dto.ChatRequest;
import cn.xyt.codehub.dto.ChatResponse;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class DeepSeekUtil {
    private static final String INSTRUMENT = "在下文中会给你一份作业的描述和学生提交的作业正文,请你用中文对学生作业进行一个简短的评价";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chatCompletion(String apiKey, String userMessage) {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 创建请求体
        ChatRequest request = new ChatRequest();
        request.setMessages(List.of(
            new ChatRequest.Message("system", INSTRUMENT),
            new ChatRequest.Message("user", userMessage)
        ));

        // 发送请求
        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ChatResponse> response = restTemplate.exchange(
            API_URL,
            HttpMethod.POST,
            entity,
            ChatResponse.class
        );

        // 处理响应
        if (response.getStatusCode().is2xxSuccessful() && 
            response.getBody() != null &&
            !response.getBody().getChoices().isEmpty()) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        }
        throw new RuntimeException("API 调用失败: " + response.getStatusCode());
    }
}
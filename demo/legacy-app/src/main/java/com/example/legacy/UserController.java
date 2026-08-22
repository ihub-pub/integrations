package com.example.legacy;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 遗留代码示例：典型的 Spring Boot 2.x 风格（javax.* + fastjson 1.x）。
 * <p>
 * 迁移到 Spring Boot 3+/4 时需要：
 * <ul>
 *   <li>{@code javax.servlet} → {@code jakarta.servlet}</li>
 *   <li>{@code fastjson 1.x} → Jackson 或 fastjson2（见 IHub catalog: utilities-json-jackson）</li>
 * </ul>
 */
@RestController
public class UserController {

    @GetMapping("/user")
    public String getUser(HttpServletRequest request) {
        Map<String, Object> user = Map.of("id", 1, "name", "alice");
        return JSON.toJSONString(user);
    }
}

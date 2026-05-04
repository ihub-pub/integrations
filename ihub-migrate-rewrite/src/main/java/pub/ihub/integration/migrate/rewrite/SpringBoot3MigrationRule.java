/*
 * Copyright (c) 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.ihub.integration.migrate.rewrite;

import pub.ihub.integration.migrate.core.AnalysisResult;
import pub.ihub.integration.migrate.core.MigrationRule;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot 2.x → 3.x 迁移规则。
 *
 * <p>检测项目是否使用 Spring Boot 2.x，并给出迁移方案。
 * 对应的 OpenRewrite Recipe 为 {@code UpgradeSpringBoot_3_4}。
 *
 * @author IHub
 * @since 0.1.0
 */
public class SpringBoot3MigrationRule implements MigrationRule {

    static final String SPRING_BOOT_GROUP = "org.springframework.boot:spring-boot-starter";

    @Override
    public String id() {
        return "spring-boot-2-to-3";
    }

    @Override
    public String description() {
        return "检测 Spring Boot 2.x 版本并提供 3.x 迁移指引（Jakarta EE + Security 6）";
    }

    @Override
    public RuleCategory category() {
        return RuleCategory.DEPENDENCY;
    }

    @Override
    public AnalysisResult analyze(ProjectContext context) {
        List<AnalysisResult.Issue> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        String springBootVersion = findSpringBootVersion(context.dependencies());

        if (springBootVersion != null && springBootVersion.startsWith("2.")) {
            issues.add(new AnalysisResult.Issue(
                AnalysisResult.Severity.CRITICAL,
                "检测到 Spring Boot " + springBootVersion + "，官方支持已于 2023-11-24 结束",
                SPRING_BOOT_GROUP + ":" + springBootVersion,
                "升级到 Spring Boot 3.x，运行 OpenRewrite Recipe: UpgradeSpringBoot_3_4"
            ));
            suggestions.add("将 spring-boot 版本升级至 3.4.x 或以上");
            suggestions.add("同步迁移 javax.* → jakarta.* 包名");
            suggestions.add("更新 Spring Security 配置：WebSecurityConfigurerAdapter → SecurityFilterChain");
        }

        return new AnalysisResult(id(), issues, suggestions);
    }

    private String findSpringBootVersion(Map<String, String> dependencies) {
        if (dependencies == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            if (entry.getKey().startsWith("org.springframework.boot:")) {
                return entry.getValue();
            }
        }
        return null;
    }
}

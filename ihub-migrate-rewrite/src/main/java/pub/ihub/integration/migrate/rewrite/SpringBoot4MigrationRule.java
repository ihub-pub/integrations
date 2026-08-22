/*
 * Copyright (c) 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with this project's license terms.
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
 * Spring Boot 3.x → 4.x 迁移规则。
 *
 * <p>检测项目是否使用 Spring Boot 3.x，并给出 4.x 迁移方案。
 * Spring Boot 4.0 基于 Spring Framework 7，引入 Jackson 3（tools.jackson）
 * 与模块化依赖结构，建议 Java 21。
 *
 * <p>对应 OpenRewrite Recipe：{@code UpgradeSpringBoot_4_0}
 * （需较新版本 rewrite-spring；当前锁定版本最高支持 3.3，
 * 见 {@link RecipeAdapter#springBoot4MigrationRecipes()}）。
 *
 * @author IHub
 * @since 0.2.5
 */
public class SpringBoot4MigrationRule implements MigrationRule {

    static final String SPRING_BOOT_GROUP = "org.springframework.boot:spring-boot-starter";

    @Override
    public String id() {
        return "spring-boot-3-to-4";
    }

    @Override
    public String description() {
        return "检测 Spring Boot 3.x 版本并提供 4.x 迁移指引（Framework 7 + Jackson 3）";
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

        if (springBootVersion != null && springBootVersion.startsWith("3.")) {
            issues.add(new AnalysisResult.Issue(
                AnalysisResult.Severity.WARNING,
                "检测到 Spring Boot " + springBootVersion + "，4.0 已 GA（2025-11），可规划升级",
                SPRING_BOOT_GROUP + ":" + springBootVersion,
                "升级到 Spring Boot 4.x，运行 OpenRewrite Recipe: UpgradeSpringBoot_4_0"
            ));
            suggestions.add("先将 Spring Boot 升级到 3.5.x 最新版（4.0 的直接前置版本）");
            suggestions.add("升级到 Spring Boot 4.0：基于 Spring Framework 7，注意 API 清理变更");
            suggestions.add("Jackson 2 (com.fasterxml) → Jackson 3 (tools.jackson)，序列化行为需回归测试");
            suggestions.add("Java 基线保持 17，推荐升级到 21（虚拟线程收益）");
            suggestions.add("检查 @MockBean/@SpyBean → @MockitoBean/@MockitoSpyBean 等测试注解迁移");
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

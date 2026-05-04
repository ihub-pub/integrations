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
package pub.ihub.integration.migrate.analyzer;

import pub.ihub.integration.migrate.core.AnalysisResult;
import pub.ihub.integration.migrate.core.MigrationRule;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 过时依赖检测规则。
 *
 * <p>识别项目中已废弃或不再维护的依赖，并匹配 IHub libs catalog 推荐的替代方案。
 *
 * @author IHub
 * @since 0.1.0
 */
public class ObsoleteDependencyRule implements MigrationRule {

    /**
     * 已知过时依赖及其建议替换项（group:artifact → 推荐替代）。
     */
    private static final Map<String, String> OBSOLETE_MAP = Map.of(
        "com.alibaba:fastjson", "使用 jackson-databind 或 gson（libs catalog: utilities-json-jackson）",
        "commons-lang:commons-lang", "升级到 org.apache.commons:commons-lang3",
        "log4j:log4j", "升级到 logback-classic 或 log4j2（Spring Boot 默认已使用 logback）",
        "javax.servlet:javax.servlet-api", "迁移到 jakarta.servlet:jakarta.servlet-api（Jakarta EE）",
        "junit:junit", "升级到 org.junit.jupiter:junit-jupiter（JUnit 5）"
    );

    @Override
    public String id() {
        return "obsolete-dependency";
    }

    @Override
    public String description() {
        return "检测项目中已废弃或不再推荐的依赖，匹配 IHub libs catalog 推荐替代方案";
    }

    @Override
    public RuleCategory category() {
        return RuleCategory.DEPENDENCY;
    }

    @Override
    public AnalysisResult analyze(ProjectContext context) {
        List<AnalysisResult.Issue> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (context.dependencies() == null) {
            return new AnalysisResult(id(), issues, suggestions);
        }

        for (Map.Entry<String, String> dep : context.dependencies().entrySet()) {
            String ga = dep.getKey();
            String replacement = OBSOLETE_MAP.get(ga);
            if (replacement != null) {
                issues.add(new AnalysisResult.Issue(
                    AnalysisResult.Severity.WARNING,
                    "使用了过时依赖: " + ga + ":" + dep.getValue(),
                    ga,
                    replacement
                ));
                suggestions.add("替换 " + ga + " → " + replacement);
            }
        }

        return new AnalysisResult(id(), issues, suggestions);
    }

    /**
     * 返回已知过时依赖列表（供外部查询）。
     */
    public static Map<String, String> knownObsoleteDependencies() {
        return OBSOLETE_MAP;
    }
}

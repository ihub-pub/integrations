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
 * Gradle Kotlin DSL 迁移规则。
 *
 * <p>检测项目是否仍在使用 Groovy DSL，并给出 Kotlin DSL 迁移建议。
 *
 * @author IHub
 * @since 0.1.0
 */
public class GradleKotlinDslRule implements MigrationRule {

    @Override
    public String id() {
        return "gradle-groovy-to-kotlin-dsl";
    }

    @Override
    public String description() {
        return "检测 Gradle Groovy DSL 使用情况，建议迁移到 Kotlin DSL（类型安全、IDE 友好）";
    }

    @Override
    public RuleCategory category() {
        return RuleCategory.BUILD;
    }

    @Override
    public AnalysisResult analyze(ProjectContext context) {
        List<AnalysisResult.Issue> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        String buildTool = context.buildTool();
        Object buildScript = context.metadata() != null ? context.metadata().get("build_script") : null;

        if ("gradle".equalsIgnoreCase(buildTool)) {
            boolean isGroovy = buildScript != null && buildScript.toString().endsWith(".gradle");
            if (isGroovy) {
                issues.add(new AnalysisResult.Issue(
                    AnalysisResult.Severity.WARNING,
                    "使用 Gradle Groovy DSL（build.gradle），建议迁移到 Kotlin DSL（build.gradle.kts）",
                    buildScript.toString(),
                    "将 build.gradle 重命名为 build.gradle.kts 并更新语法"
                ));
                suggestions.add("使用 IHub ihub-settings 插件快速接入 Kotlin DSL 规范");
                suggestions.add("参考 OpenRewrite Recipe: MigrateToGradleLocalJavaToolchains");
            }
        } else if ("maven".equalsIgnoreCase(buildTool)) {
            issues.add(new AnalysisResult.Issue(
                AnalysisResult.Severity.INFO,
                "使用 Maven 构建工具，建议长期迁移到 Gradle Kotlin DSL",
                "pom.xml",
                "评估迁移到 Gradle 的收益；可参考 IHub plugins 体系"
            ));
            suggestions.add("迁移到 Gradle Kotlin DSL 可获得 IHub 插件生态的完整支持");
        }

        return new AnalysisResult(id(), issues, suggestions);
    }
}

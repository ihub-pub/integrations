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
import java.util.Set;

/**
 * Java 版本兼容性检测规则。
 *
 * <p>检测项目的 Java 版本是否过低，并给出升级建议。
 * IHub 生态要求最低 Java 17（LTS）。
 *
 * @author IHub
 * @since 0.1.0
 */
public class JavaVersionRule implements MigrationRule {

    private static final int MIN_SUPPORTED_VERSION = 17;
    private static final Set<Integer> LTS_VERSIONS = Set.of(17, 21);

    @Override
    public String id() {
        return "java-version-check";
    }

    @Override
    public String description() {
        return "检测 Java 版本是否满足 IHub 生态最低要求（Java 17+），推荐升级到 LTS 版本（17/21）";
    }

    @Override
    public RuleCategory category() {
        return RuleCategory.BUILD;
    }

    @Override
    public AnalysisResult analyze(ProjectContext context) {
        List<AnalysisResult.Issue> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        String javaVersionStr = context.javaVersion();
        if (javaVersionStr == null || javaVersionStr.isBlank()) {
            issues.add(new AnalysisResult.Issue(
                AnalysisResult.Severity.WARNING,
                "未检测到 Java 版本信息，无法验证兼容性",
                "project metadata",
                "确保 build.gradle.kts 中配置了 java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }"
            ));
            return new AnalysisResult(id(), issues, suggestions);
        }

        int version;
        try {
            version = Integer.parseInt(javaVersionStr.trim());
        } catch (NumberFormatException e) {
            return new AnalysisResult(id(), issues, suggestions);
        }

        if (version < MIN_SUPPORTED_VERSION) {
            issues.add(new AnalysisResult.Issue(
                AnalysisResult.Severity.CRITICAL,
                "Java " + version + " 低于 IHub 生态最低要求（Java " + MIN_SUPPORTED_VERSION + "）",
                "java.version=" + version,
                "升级到 Java 21（当前 LTS）"
            ));
            suggestions.add("将 Java 版本升级至 21（LTS）");
            suggestions.add("更新 GitHub Actions 中的 java-version 配置");
        } else if (!LTS_VERSIONS.contains(version)) {
            issues.add(new AnalysisResult.Issue(
                AnalysisResult.Severity.INFO,
                "Java " + version + " 为非 LTS 版本，建议使用 LTS 版本（17 或 21）",
                "java.version=" + version,
                "切换到 Java 21（最新 LTS）"
            ));
            suggestions.add("推荐切换到 Java 21 以获得最长期的安全支持");
        }

        return new AnalysisResult(id(), issues, suggestions);
    }
}

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
package pub.ihub.integration.migrate.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MigrationAnalyzerTest {

    @Test
    void analyzeWithNoIssues() {
        MigrationAnalyzer analyzer = new MigrationAnalyzer(List.of(
            ctx -> new AnalysisResult("no-op", List.of(), List.of("一切正常"))
        ));
        ProjectContext ctx = new ProjectContext("test-project", "/tmp/test", "gradle", "17",
            Map.of(), Map.of());

        AnalysisReport report = analyzer.analyze(ctx);

        assertEquals("test-project", report.projectName());
        assertEquals(0, report.totalIssues());
        assertFalse(report.hasBlockers());
        assertTrue(report.summary().contains("未发现迁移问题"));
    }

    @Test
    void analyzeWithBlocker() {
        MigrationAnalyzer analyzer = new MigrationAnalyzer(List.of(
            ctx -> new AnalysisResult("test-rule", List.of(
                new AnalysisResult.Issue(
                    AnalysisResult.Severity.BLOCKER,
                    "使用了已删除的 API javax.servlet",
                    "com.example:web:2.7.0",
                    "替换为 jakarta.servlet"
                )
            ), List.of())
        ));
        ProjectContext ctx = new ProjectContext("legacy-app", "/tmp/legacy", "maven", "11",
            Map.of("javax.servlet:javax.servlet-api", "4.0.1"), Map.of());

        AnalysisReport report = analyzer.analyze(ctx);

        assertEquals(1, report.totalIssues());
        assertTrue(report.hasBlockers());
        assertTrue(report.summary().contains("阻断"));
    }

    @Test
    void analyzeWithMultipleRulesAndMixedSeverity() {
        MigrationAnalyzer analyzer = new MigrationAnalyzer(List.of(
            ctx -> new AnalysisResult("rule-1", List.of(
                new AnalysisResult.Issue(AnalysisResult.Severity.CRITICAL, "严重问题", "loc1", "fix1"),
                new AnalysisResult.Issue(AnalysisResult.Severity.WARNING, "警告", "loc2", null)
            ), List.of("建议1")),
            ctx -> new AnalysisResult("rule-2", List.of(
                new AnalysisResult.Issue(AnalysisResult.Severity.INFO, "信息", "loc3", "fix3")
            ), List.of())
        ));
        ProjectContext ctx = new ProjectContext("multi-issue", "/tmp/multi", "gradle", "17",
            Map.of(), Map.of());

        AnalysisReport report = analyzer.analyze(ctx);

        assertEquals(3, report.totalIssues());
        assertFalse(report.hasBlockers());
        assertTrue(report.summary().contains("3"));
    }

    @Test
    void analysisResultWithNullIssues() {
        AnalysisResult result = new AnalysisResult("rule", null, List.of());
        assertFalse(result.hasIssues());
    }

    @Test
    void analysisResultWithEmptyIssues() {
        AnalysisResult result = new AnalysisResult("rule", List.of(), List.of());
        assertFalse(result.hasIssues());
    }

    @Test
    void migrationRuleDefaultMethods() {
        // 测试 @FunctionalInterface 的默认方法
        MigrationRule rule = ctx -> new AnalysisResult("x", List.of(), List.of());
        assertNotNull(rule.id());
        assertNotNull(rule.description());
        assertEquals(MigrationRule.RuleCategory.DEPENDENCY, rule.category());
    }

    @Test
    void migrationRuleAllCategories() {
        for (MigrationRule.RuleCategory cat : MigrationRule.RuleCategory.values()) {
            assertNotNull(cat.name());
        }
    }
}

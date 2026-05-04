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

import org.junit.jupiter.api.Test;
import pub.ihub.integration.migrate.core.AnalysisReport;
import pub.ihub.integration.migrate.core.AnalysisResult;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProjectAnalyzerTest {

    // ---- ProjectAnalyzer ----

    @Test
    void defaultRulesNotEmpty() {
        assertFalse(ProjectAnalyzer.defaultRules().isEmpty());
    }

    @Test
    void analyzeCleanProject() {
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectContext ctx = new ProjectContext("clean-app", "/app", "gradle", "21",
            Map.of(), Map.of());
        AnalysisReport report = analyzer.analyze(ctx);
        assertNotNull(report);
        assertEquals("clean-app", report.projectName());
        assertFalse(report.hasBlockers());
    }

    @Test
    void analyzeDetectsObsoleteDep() {
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectContext ctx = new ProjectContext("legacy-app", "/app", "gradle", "21",
            Map.of("junit:junit", "4.13"), Map.of());
        AnalysisReport report = analyzer.analyze(ctx);
        assertTrue(report.totalIssues() > 0);
    }

    @Test
    void analyzeDetectsOldJava() {
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectContext ctx = new ProjectContext("old-app", "/app", "gradle", "11",
            Map.of(), Map.of());
        AnalysisReport report = analyzer.analyze(ctx);
        assertTrue(report.totalIssues() > 0);
    }

    // ---- ObsoleteDependencyRule ----

    @Test
    void knownObsoleteDepsNotEmpty() {
        assertFalse(ObsoleteDependencyRule.knownObsoleteDependencies().isEmpty());
    }

    @Test
    void detectsFastjson() {
        ObsoleteDependencyRule rule = new ObsoleteDependencyRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "17",
            Map.of("com.alibaba:fastjson", "1.2.83"), Map.of());
        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.WARNING, result.issues().get(0).severity());
    }

    @Test
    void noIssueForModernDeps() {
        ObsoleteDependencyRule rule = new ObsoleteDependencyRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "17",
            Map.of("com.fasterxml.jackson.core:jackson-databind", "2.17.0"), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void noIssueForNullDependencies() {
        ObsoleteDependencyRule rule = new ObsoleteDependencyRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "17",
            null, Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void obsoleteDependencyRuleMetadata() {
        ObsoleteDependencyRule rule = new ObsoleteDependencyRule();
        assertEquals("obsolete-dependency", rule.id());
        assertNotNull(rule.description());
    }

    // ---- JavaVersionRule ----

    @Test
    void java8IsCritical() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "8", Map.of(), Map.of());
        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.CRITICAL, result.issues().get(0).severity());
    }

    @Test
    void java11IsCritical() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "11", Map.of(), Map.of());
        assertTrue(rule.analyze(ctx).hasIssues());
    }

    @Test
    void java17IsAccepted() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "17", Map.of(), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void java21IsAccepted() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "21", Map.of(), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void java20IsNonLtsInfo() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "20", Map.of(), Map.of());
        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.INFO, result.issues().get(0).severity());
    }

    @Test
    void nullJavaVersionWarning() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", null, Map.of(), Map.of());
        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.WARNING, result.issues().get(0).severity());
    }

    @Test
    void blankJavaVersionWarning() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "  ", Map.of(), Map.of());
        assertTrue(rule.analyze(ctx).hasIssues());
    }

    @Test
    void invalidJavaVersionNoIssue() {
        JavaVersionRule rule = new JavaVersionRule();
        ProjectContext ctx = new ProjectContext("app", "/app", "gradle", "lts", Map.of(), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void javaVersionRuleMetadata() {
        JavaVersionRule rule = new JavaVersionRule();
        assertEquals("java-version-check", rule.id());
        assertNotNull(rule.description());
    }
}

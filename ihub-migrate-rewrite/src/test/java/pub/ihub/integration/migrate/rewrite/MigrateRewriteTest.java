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

import org.junit.jupiter.api.Test;
import pub.ihub.integration.migrate.core.AnalysisResult;
import pub.ihub.integration.migrate.core.MigrationRule;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MigrateRewriteTest {

    // ---- RecipeAdapter ----

    @Test
    void springBoot3RecipesNotEmpty() {
        List<String> recipes = RecipeAdapter.springBoot3MigrationRecipes();
        assertFalse(recipes.isEmpty());
        assertTrue(recipes.stream().anyMatch(r -> r.contains("SpringBoot")));
    }

    @Test
    void gradleKotlinDslRecipesNotEmpty() {
        List<String> recipes = RecipeAdapter.gradleKotlinDslRecipes();
        assertFalse(recipes.isEmpty());
    }

    @Test
    void toRecipeNamesForDependencyRule() {
        MigrationRule rule = ctx -> new AnalysisResult("test", List.of(), List.of());
        // default category = DEPENDENCY
        List<String> names = RecipeAdapter.toRecipeNames(rule);
        assertFalse(names.isEmpty());
    }

    @Test
    void toRecipeNamesForBuildRule() {
        MigrationRule rule = new GradleKotlinDslRule();
        List<String> names = RecipeAdapter.toRecipeNames(rule);
        assertFalse(names.isEmpty());
    }

    @Test
    void toRecipeNamesForConfigRule() {
        MigrationRule rule = new MigrationRule() {
            @Override public RuleCategory category() { return RuleCategory.CONFIG; }
            @Override public AnalysisResult analyze(ProjectContext ctx) {
                return new AnalysisResult(id(), List.of(), List.of());
            }
        };
        List<String> names = RecipeAdapter.toRecipeNames(rule);
        assertTrue(names.isEmpty());
    }

    // ---- SpringBoot3MigrationRule ----

    @Test
    void detectsSpringBoot2() {
        SpringBoot3MigrationRule rule = new SpringBoot3MigrationRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "17",
            Map.of("org.springframework.boot:spring-boot-starter", "2.7.18"), Map.of());

        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.CRITICAL, result.issues().get(0).severity());
        assertFalse(result.suggestions().isEmpty());
    }

    @Test
    void noIssueForSpringBoot3() {
        SpringBoot3MigrationRule rule = new SpringBoot3MigrationRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "21",
            Map.of("org.springframework.boot:spring-boot-starter", "3.4.0"), Map.of());

        AnalysisResult result = rule.analyze(ctx);
        assertFalse(result.hasIssues());
    }

    @Test
    void noIssueWhenNoDependencies() {
        SpringBoot3MigrationRule rule = new SpringBoot3MigrationRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "17",
            Map.of(), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void noIssueWhenDependenciesNull() {
        SpringBoot3MigrationRule rule = new SpringBoot3MigrationRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "17",
            null, Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void springBoot3RuleMetadata() {
        SpringBoot3MigrationRule rule = new SpringBoot3MigrationRule();
        assertEquals("spring-boot-2-to-3", rule.id());
        assertEquals(MigrationRule.RuleCategory.DEPENDENCY, rule.category());
        assertNotNull(rule.description());
    }

    // ---- GradleKotlinDslRule ----

    @Test
    void detectsGroovyDsl() {
        GradleKotlinDslRule rule = new GradleKotlinDslRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "17",
            Map.of(), Map.of("build_script", "build.gradle"));

        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.WARNING, result.issues().get(0).severity());
    }

    @Test
    void kotlinDslNoIssue() {
        GradleKotlinDslRule rule = new GradleKotlinDslRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "17",
            Map.of(), Map.of("build_script", "build.gradle.kts"));

        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void mavenBuildInfoIssue() {
        GradleKotlinDslRule rule = new GradleKotlinDslRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "maven", "17",
            Map.of(), Map.of());

        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertEquals(AnalysisResult.Severity.INFO, result.issues().get(0).severity());
    }

    @Test
    void nonGradleNonMavenNoIssue() {
        GradleKotlinDslRule rule = new GradleKotlinDslRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "sbt", "17",
            Map.of(), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void gradleWithNullMetadataNoIssue() {
        GradleKotlinDslRule rule = new GradleKotlinDslRule();
        ProjectContext ctx = new ProjectContext("my-app", "/project", "gradle", "17",
            Map.of(), null);
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void gradleKotlinDslRuleMetadata() {
        GradleKotlinDslRule rule = new GradleKotlinDslRule();
        assertEquals("gradle-groovy-to-kotlin-dsl", rule.id());
        assertEquals(MigrationRule.RuleCategory.BUILD, rule.category());
        assertNotNull(rule.description());
    }

    // ---- SpringBoot4MigrationRule ----

    @Test
    void springBoot4RecipesNotEmpty() {
        List<String> recipes = RecipeAdapter.springBoot4MigrationRecipes();
        assertFalse(recipes.isEmpty());
        assertTrue(recipes.stream().anyMatch(r -> r.contains("SpringBoot")));
    }

    @Test
    void toRecipeNamesForBoot4Rule() {
        MigrationRule rule = new SpringBoot4MigrationRule();
        List<String> names = RecipeAdapter.toRecipeNames(rule);
        assertTrue(names.contains("org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0"));
    }

    @Test
    void boot3ProjectDetectedByBoot4Rule() {
        SpringBoot4MigrationRule rule = new SpringBoot4MigrationRule();
        ProjectContext ctx = new ProjectContext("legacy-app", "/project", "maven", "17",
            Map.of("org.springframework.boot:spring-boot-starter-web", "3.3.5"), Map.of());

        AnalysisResult result = rule.analyze(ctx);
        assertTrue(result.hasIssues());
        assertTrue(result.issues().get(0).description().contains("3.3.5"));
        assertFalse(result.suggestions().isEmpty());
    }

    @Test
    void boot2ProjectNotFlaggedByBoot4Rule() {
        SpringBoot4MigrationRule rule = new SpringBoot4MigrationRule();
        ProjectContext ctx = new ProjectContext("old-app", "/project", "maven", "8",
            Map.of("org.springframework.boot:spring-boot-starter", "2.7.18"), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void springBoot4RuleMetadata() {
        SpringBoot4MigrationRule rule = new SpringBoot4MigrationRule();
        assertEquals("spring-boot-3-to-4", rule.id());
        assertEquals(MigrationRule.RuleCategory.DEPENDENCY, rule.category());
        assertNotNull(rule.description());
    }

    @Test
    void boot4RuleNoSpringBootDepsReturnsNoIssues() {
        SpringBoot4MigrationRule rule = new SpringBoot4MigrationRule();
        // 无 spring-boot 依赖 → findSpringBootVersion 返回 null → 不报 issue
        ProjectContext ctx = new ProjectContext("plain-java", "/project", "maven", "17",
            Map.of("org.apache.commons:commons-lang3", "3.14.0"), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void boot4RuleEmptyDependenciesReturnsNoIssues() {
        SpringBoot4MigrationRule rule = new SpringBoot4MigrationRule();
        // 空依赖 map → 循环零次 → 返回 null → 不报 issue
        ProjectContext ctx = new ProjectContext("empty", "/project", "maven", "17",
            Map.of(), Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }

    @Test
    void boot4RuleNullDependenciesReturnsNoIssues() {
        SpringBoot4MigrationRule rule = new SpringBoot4MigrationRule();
        // null dependencies → dependencies == null 短路 → 不报 issue
        ProjectContext ctx = new ProjectContext("null-deps", "/project", "maven", "17",
            null, Map.of());
        assertFalse(rule.analyze(ctx).hasIssues());
    }
}

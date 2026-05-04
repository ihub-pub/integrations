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

import org.openrewrite.Recipe;
import org.openrewrite.config.DeclarativeRecipe;
import pub.ihub.integration.migrate.core.MigrationRule;

import java.util.List;

/**
 * OpenRewrite Recipe 集成适配器。
 *
 * <p>将 {@link MigrationRule} 转换为可执行的 OpenRewrite {@link Recipe}，
 * 也提供基于配置的声明式 Recipe 构建工厂。
 *
 * @author IHub
 * @since 0.1.0
 */
public final class RecipeAdapter {

    private RecipeAdapter() {}

    /**
     * 构建 IHub Spring Boot 3.x 迁移 Recipe（组合式声明）。
     *
     * <p>包含：
     * <ul>
     *   <li>Spring Boot 2.x → 3.x 版本升级</li>
     *   <li>Javax → Jakarta 命名空间迁移</li>
     *   <li>Spring Security 5 → 6 配置 DSL 迁移</li>
     * </ul>
     *
     * @return 组合 Recipe 的名称列表（供 OpenRewrite 执行）
     */
    public static List<String> springBoot3MigrationRecipes() {
        return List.of(
            "org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_4",
            "org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta",
            "org.openrewrite.java.spring.security6.UpgradeSpringSecurity_6_0"
        );
    }

    /**
     * 构建 Gradle Kotlin DSL 迁移 Recipe 名称列表。
     *
     * @return Recipe 名称列表
     */
    public static List<String> gradleKotlinDslRecipes() {
        return List.of(
            "org.openrewrite.gradle.MigrateToGradleLocalJavaToolchains"
        );
    }

    /**
     * 将迁移规则 ID 映射到对应的 OpenRewrite Recipe 名称列表。
     *
     * @param rule 迁移规则
     * @return 对应的 Recipe 名称列表；若无映射则返回空列表
     */
    public static List<String> toRecipeNames(MigrationRule rule) {
        return switch (rule.category()) {
            case DEPENDENCY, CODE_PATTERN -> springBoot3MigrationRecipes();
            case BUILD -> gradleKotlinDslRecipes();
            case CONFIG -> List.of();
        };
    }
}

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

import java.util.List;

/**
 * 迁移规则抽象接口。
 *
 * <p>每条规则描述一类可检测、可修复的迁移问题，如 Spring Boot 版本升级、
 * 依赖坐标变更、API 弃用替换等。规则可独立运行，也可组合为迁移套件。
 *
 * <p>该接口为函数式接口，核心抽象方法为 {@link #analyze(ProjectContext)}，
 * 其余方法提供默认实现，支持 lambda 表达式快速创建规则（多用于测试）。
 *
 * @author IHub
 * @since 0.1.0
 */
@FunctionalInterface
public interface MigrationRule {

    /**
     * 规则唯一标识，如 {@code "spring-boot-2-to-3"}。
     * <p>默认返回实现类简单类名的 kebab-case 形式。
     */
    default String id() {
        return getClass().getSimpleName()
                .replaceAll("([A-Z])", "-$1")
                .toLowerCase()
                .replaceFirst("^-", "");
    }

    /**
     * 面向 AI 的规则描述，说明该规则检测什么、建议如何修复。
     */
    default String description() {
        return "Migration rule: " + id();
    }

    /**
     * 规则分类。默认为 {@link RuleCategory#DEPENDENCY}。
     */
    default RuleCategory category() {
        return RuleCategory.DEPENDENCY;
    }

    /**
     * 对给定项目上下文执行分析，返回发现的问题列表。
     *
     * @param context 项目上下文（包含依赖、代码结构、配置等）
     * @return 分析结果，含问题列表与建议
     */
    AnalysisResult analyze(ProjectContext context);

    /**
     * 规则分类枚举。
     */
    enum RuleCategory {
        /** 依赖版本升级 / 坐标变更 */
        DEPENDENCY,
        /** 代码模式重写（API 调用、注解、类型等） */
        CODE_PATTERN,
        /** 配置文件变更（application.yml、Spring XML 等） */
        CONFIG,
        /** 构建脚本变更（Gradle / Maven） */
        BUILD
    }
}

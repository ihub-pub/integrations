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
 * 单次规则分析的结果。
 *
 * @param ruleId      对应规则 ID
 * @param issues      发现的问题列表
 * @param suggestions 改进建议列表
 * @author IHub
 * @since 0.1.0
 */
public record AnalysisResult(
        String ruleId,
        List<Issue> issues,
        List<String> suggestions
) {

    /**
     * 返回是否存在需要处理的问题。
     */
    public boolean hasIssues() {
        return issues != null && !issues.isEmpty();
    }

    /**
     * 分析发现的单个问题。
     *
     * @param severity    严重程度
     * @param description 面向 AI 的问题描述
     * @param location    问题位置（文件路径 / 依赖坐标等）
     * @param fix         推荐修复方式（可为 null）
     */
    public record Issue(
            Severity severity,
            String description,
            String location,
            String fix
    ) {}

    /**
     * 问题严重程度。
     */
    public enum Severity {
        /** 阻断：必须修复，否则无法运行 */
        BLOCKER,
        /** 严重：影响稳定性或安全性 */
        CRITICAL,
        /** 警告：建议修复 */
        WARNING,
        /** 信息：仅供参考 */
        INFO
    }
}

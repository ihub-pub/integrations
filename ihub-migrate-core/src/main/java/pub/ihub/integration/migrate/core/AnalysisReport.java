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

import java.time.Instant;
import java.util.List;

/**
 * 项目迁移分析报告（AI 友好格式）。
 *
 * <p>由 {@link MigrationAnalyzer} 汇总所有规则的分析结果后生成。
 * 可序列化为 JSON（供 MCP Server 返回）或 Markdown（供开发者阅读）。
 *
 * @param projectName   项目名称
 * @param projectPath   项目路径
 * @param analyzedAt    分析时间
 * @param results       各规则的分析结果
 * @param summary       AI 可读的摘要描述
 * @author IHub
 * @since 0.1.0
 */
public record AnalysisReport(
        String projectName,
        String projectPath,
        Instant analyzedAt,
        List<AnalysisResult> results,
        String summary
) {

    /**
     * 统计所有问题总数。
     */
    public long totalIssues() {
        return results.stream()
                .filter(AnalysisResult::hasIssues)
                .mapToLong(r -> r.issues().size())
                .sum();
    }

    /**
     * 返回是否有阻断级别的问题。
     */
    public boolean hasBlockers() {
        return results.stream()
                .flatMap(r -> r.issues().stream())
                .anyMatch(i -> i.severity() == AnalysisResult.Severity.BLOCKER);
    }
}

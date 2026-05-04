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
import java.util.ArrayList;
import java.util.List;

/**
 * 迁移分析引擎：汇总多个 {@link MigrationRule} 的分析结果，生成 {@link AnalysisReport}。
 *
 * <p>用法示例：
 * <pre>{@code
 * MigrationAnalyzer analyzer = new MigrationAnalyzer(List.of(
 *     new SpringBootVersionRule(),
 *     new ObsoleteDepRule()
 * ));
 * AnalysisReport report = analyzer.analyze(context);
 * }</pre>
 *
 * @author IHub
 * @since 0.1.0
 */
public class MigrationAnalyzer {

    private final List<MigrationRule> rules;

    public MigrationAnalyzer(List<MigrationRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /**
     * 对给定项目上下文运行所有规则，汇总结果。
     *
     * @param context 项目上下文
     * @return 完整的分析报告
     */
    public AnalysisReport analyze(ProjectContext context) {
        List<AnalysisResult> results = new ArrayList<>();
        for (MigrationRule rule : rules) {
            results.add(rule.analyze(context));
        }

        long totalIssues = results.stream()
                .filter(AnalysisResult::hasIssues)
                .mapToLong(r -> r.issues().size())
                .sum();

        String summary = buildSummary(context.projectName(), totalIssues, results);

        return new AnalysisReport(
                context.projectName(),
                context.projectPath(),
                Instant.now(),
                results,
                summary
        );
    }

    private String buildSummary(String projectName, long totalIssues, List<AnalysisResult> results) {
        long blockers = results.stream()
                .flatMap(r -> r.issues().stream())
                .filter(i -> i.severity() == AnalysisResult.Severity.BLOCKER)
                .count();
        long rules = results.stream().filter(AnalysisResult::hasIssues).count();

        if (totalIssues == 0) {
            return String.format("项目 %s 未发现迁移问题，状态良好。", projectName);
        }
        return String.format(
                "项目 %s 发现 %d 个迁移问题（其中 %d 个阻断级别），涉及 %d 条规则。建议优先处理阻断问题。",
                projectName, totalIssues, blockers, rules
        );
    }
}

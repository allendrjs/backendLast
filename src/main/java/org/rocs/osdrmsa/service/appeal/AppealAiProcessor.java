package org.rocs.osdrmsa.service.appeal;

import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.service.ai.AiCaseAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class AppealAiProcessor {

    private static final Logger log = LoggerFactory.getLogger(AppealAiProcessor.class);

    private final AppealRepository appealRepository;
    private final AiCaseAnalysisService aiCaseAnalysisService;
    private final TransactionTemplate transactionTemplate;

    public AppealAiProcessor(
            AppealRepository appealRepository,
            AiCaseAnalysisService aiCaseAnalysisService,
            PlatformTransactionManager transactionManager) {
        this.appealRepository = appealRepository;
        this.aiCaseAnalysisService = aiCaseAnalysisService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Async("aiTaskExecutor")
    public void process(Long appealId) {
        String[] input = transactionTemplate.execute(status -> {
            Appeal appeal = appealRepository.findById(appealId).orElse(null);
            if (appeal == null) {
                return null;
            }
            Record record = appeal.getRecord();
            Enrollment enrollment = appeal.getEnrollment();

            StringBuilder context = new StringBuilder();
            context.append("CASE TYPE: Student Appeal\n");
            context.append("Student ID: ")
                    .append(enrollment != null && enrollment.getStudent() != null
                            ? enrollment.getStudent().getStudentId() : "Unknown")
                    .append("\n");
            context.append("Offense: ")
                    .append(record != null && record.getOffense() != null
                            ? record.getOffense().getOffense() : "Unknown")
                    .append("\n");
            context.append("Offense Type: ")
                    .append(record != null && record.getOffense() != null
                            ? record.getOffense().getType() : "Unknown")
                    .append("\n");
            context.append("Date of Violation: ")
                    .append(record != null ? record.getDateOfViolation() : null)
                    .append("\n");
            context.append("Record Status: ")
                    .append(record != null ? record.getStatus() : null)
                    .append("\n");
            context.append("Appeal Message: ").append(appeal.getMessage()).append("\n");

            String department = enrollment != null && enrollment.getDepartment() != null
                    ? enrollment.getDepartment().name() : null;

            return new String[] {context.toString(), department};
        });

        if (input == null) {
            return;
        }

        AiCaseAnalysisService.Result result;
        try {
            result = aiCaseAnalysisService.analyze("Student Appeal", input[1], input[0]);
        } catch (Exception e) {
            log.warn("Appeal AI analysis failed for appeal {}: {}", appealId, e.getMessage());
            result = new AiCaseAnalysisService.Result("UNCERTAIN", "AI analysis is temporarily unavailable.");
        }

        final AiCaseAnalysisService.Result finalResult = result;
        transactionTemplate.executeWithoutResult(status ->
                appealRepository.findById(appealId).ifPresent(appeal -> {
                    appeal.setAiRecommendation(finalResult.recommendation());
                    appeal.setAiReasoning(finalResult.reasoning());
                    appealRepository.save(appeal);
                })
        );
    }
}

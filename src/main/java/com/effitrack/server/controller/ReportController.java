package com.effitrack.server.controller;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.Task;
import com.effitrack.server.model.TaskStatus;
import com.effitrack.server.model.User;
import com.effitrack.server.repository.TaskRepository;
import com.effitrack.server.repository.UserRepository;
import com.effitrack.server.service.ChartService;
import com.effitrack.server.service.EmailService;
import com.effitrack.server.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@Tag(name = StringConst.TAG_REPORTS, description = StringConst.TAG_REPORTS_DESC)
@RequestMapping(StringConst.BASE_URL_REPORTS)
public class ReportController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private ChartService chartService;

    @Operation(summary = StringConst.OP_REPORT_SEND_SUM, description = StringConst.OP_REPORT_SEND_DESC)
    @PostMapping(StringConst.ENDPOINT_SEND_REPORT)
    public ResponseEntity<String> sendShiftReport(@PathVariable(StringConst.VAR_USER_ID) Long userId) throws IOException, MessagingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(StringConst.ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<Task> weeklyTasks = taskRepository.findByAssigneeIdAndPlannedDateBetween(
                user.getId(),
                weekAgo.atStartOfDay(),
                today.atTime(LocalTime.MAX)
        );

        String tasksTableHtml = buildTasksTableHtml(weeklyTasks);

        String targetEmail = StringConst.MASTER_EMAIL;
        if (user.getMaster() != null && user.getMaster().getEmail() != null && !user.getMaster().getEmail().isEmpty()) {
            targetEmail = user.getMaster().getEmail();
        }

        String subject = String.format(StringConst.SUBJECT_TEMPLATE, user.getFullName(), LocalDate.now());

        String baseBody = String.format(StringConst.BODY_TEMPLATE, user.getFullName(), user.getShopNumber())
                .replace(StringConst.NEW_LINE, StringConst.HTML_BR);

        String fullBody = StringConst.HTML_BODY_START + baseBody + StringConst.HTML_BR + StringConst.HTML_BR + tasksTableHtml + StringConst.HTML_BODY_END;

        byte[] chartImage = chartService.generateWeeklyProductivityChart(weeklyTasks, weekAgo, today);

        emailService.sendReportWithAttachment(targetEmail, subject, fullBody, chartImage);

        return ResponseEntity.ok("{\"message\": \"" + StringConst.SUCCESS_MESSAGE + "\"}");
    }

    @Operation(summary = StringConst.OP_REPORT_EQUIPMENT_SUM)
    @PostMapping(StringConst.ENDPOINT_REPORT_EQUIPMENT)
    public ResponseEntity<String> sendEquipmentReport(@PathVariable Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(StringConst.ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        List<Equipment> equipmentList = equipmentService.getUserEquipmentWithStats(userId);

        String reportHtml = buildEquipmentReportHtml(user, equipmentList);

        byte[] chartImage = chartService.generateEquipmentEfficiencyChart(equipmentList);

        String targetEmail = user.getMaster() != null ? user.getMaster().getEmail() : StringConst.MASTER_EMAIL;
        String subject = String.format(StringConst.SUBJECT_EQUIPMENT_REPORT_TEMPLATE, user.getFullName(), LocalDate.now());

        emailService.sendReportWithAttachment(targetEmail, subject, reportHtml, chartImage);

        return ResponseEntity.ok("{\"message\": \"" + StringConst.SUCCESS_REPORT_EQUIPMENT + "\"}");
    }

    private String buildTasksTableHtml(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return StringConst.MSG_NO_TASKS;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(StringConst.HTML_HEADER_TASKS);
        sb.append(String.format(StringConst.HTML_TABLE_START_FMT, StringConst.STYLE_TABLE));

        sb.append(String.format(StringConst.HTML_TR_HEADER_FMT, StringConst.STYLE_ROW_HEADER))
                .append(th(StringConst.TEXT_HEADER_TITLE))
                .append(th(StringConst.TEXT_HEADER_STATUS))
                .append(th(StringConst.TEXT_HEADER_TIME))
                .append(StringConst.HTML_TAG_TR_END);

        for (Task task : tasks) {
            boolean isDone = task.getStatus() == TaskStatus.DONE;
            String statusColor = isDone ? StringConst.COLOR_GREEN : StringConst.COLOR_RED;
            String statusText = isDone ? StringConst.TEXT_STATUS_DONE : StringConst.TEXT_STATUS_IN_PROGRESS;

            String statusHtml = String.format(StringConst.HTML_SPAN_STYLE_FMT, StringConst.STYLE_COLOR_PREFIX + statusColor + ";", statusText);
            String titleHtml = String.format(StringConst.HTML_BOLD_FMT, task.getTitle());

            sb.append(StringConst.HTML_TAG_TR_START)
                    .append(td(titleHtml))
                    .append(td(statusHtml))
                    .append(td(task.getActualMinutes() + StringConst.TEXT_TIME_SUFFIX))
                    .append(StringConst.HTML_TAG_TR_END);

            if (task.getOperatorComment() != null && !task.getOperatorComment().isBlank()) {
                sb.append(StringConst.HTML_TAG_TR_START);
                sb.append(String.format(StringConst.HTML_TD_DETAILS_FMT.replace("colspan=\"5\"", "colspan=\"3\""), StringConst.STYLE_TASK_DETAILS_TD));

                sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_DETAILS_CONTAINER));
                sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_LABEL_TITLE));
                sb.append(StringConst.TEXT_LABEL_TASK_COMMENT);
                sb.append(StringConst.HTML_TAG_DIV_END);
                sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_TEXT_CONTENT));
                sb.append(task.getOperatorComment());
                sb.append(StringConst.HTML_TAG_DIV_END);
                sb.append(StringConst.HTML_TAG_DIV_END);

                sb.append(StringConst.HTML_TAG_TD_END);
                sb.append(StringConst.HTML_TAG_TR_END);
            }
        }

        sb.append(StringConst.HTML_TAG_TABLE_END);
        return sb.toString();
    }

    private String buildEquipmentReportHtml(User user, List<Equipment> equipmentList) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringConst.HTML_BODY_START);
        sb.append(StringConst.HTML_H2_EQUIPMENT_REPORT);
        sb.append(String.format(StringConst.HTML_P_OPERATOR_FMT, user.getFullName()));
        sb.append(String.format(StringConst.HTML_P_SHOP_FMT, user.getShopNumber()));

        sb.append(String.format(StringConst.HTML_TABLE_START_FMT, StringConst.STYLE_TABLE_MT20));
        sb.append(String.format(StringConst.HTML_TR_HEADER_FMT, StringConst.STYLE_ROW_HEADER))
                .append(String.format(StringConst.HTML_TH_FMT, StringConst.HTML_TH_STYLE, StringConst.TEXT_HEADER_EQUIPMENT))
                .append(String.format(StringConst.HTML_TH_FMT, StringConst.HTML_TH_STYLE, StringConst.TEXT_HEADER_INV_NUM))
                .append(String.format(StringConst.HTML_TH_FMT, StringConst.HTML_TH_STYLE, StringConst.TEXT_HEADER_WORK_MIN))
                .append(String.format(StringConst.HTML_TH_FMT, StringConst.HTML_TH_STYLE, StringConst.TEXT_HEADER_DOWNTIME_MIN))
                .append(String.format(StringConst.HTML_TH_FMT, StringConst.HTML_TH_STYLE, StringConst.TEXT_HEADER_SETUP_MIN))
                .append(StringConst.HTML_TAG_TR_END);

        for (Equipment eq : equipmentList) {
            sb.append(StringConst.HTML_TAG_TR_START);
            sb.append(String.format(StringConst.HTML_TD_FMT, StringConst.HTML_TD_STYLE, eq.getName()));
            sb.append(String.format(StringConst.HTML_TD_FMT, StringConst.HTML_TD_STYLE, eq.getInventoryNumber()));
            sb.append(String.format(StringConst.HTML_TD_FMT, StringConst.STYLE_CELL_GREEN_BOLD, String.format(StringConst.HTML_BOLD_FMT, eq.getWorkTimeTodayMinutes())));
            sb.append(String.format(StringConst.HTML_TD_FMT, StringConst.STYLE_CELL_RED_BOLD, String.format(StringConst.HTML_BOLD_FMT, eq.getDowntimeTodayMinutes())));
            sb.append(String.format(StringConst.HTML_TD_FMT, StringConst.STYLE_CELL_ORANGE, String.valueOf(eq.getSetupTodayMinutes())));
            sb.append(StringConst.HTML_TAG_TR_END);

            if (eq.getDowntimeTodayMinutes() > 0 || eq.getSetupTodayMinutes() > 0) {
                sb.append(StringConst.HTML_TAG_TR_START);
                sb.append(String.format(StringConst.HTML_TD_DETAILS_FMT, StringConst.STYLE_DETAILS_TD));

                String operatorComment = (eq.getOperatorComment() != null && !eq.getOperatorComment().isBlank())
                        ? eq.getOperatorComment() : StringConst.TEXT_VALUE_NOT_SPECIFIED;
                sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_DETAILS_CONTAINER));
                sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_LABEL_TITLE));
                sb.append(StringConst.TEXT_LABEL_OPERATOR_COMMENT);
                sb.append(StringConst.HTML_TAG_DIV_END);
                sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_TEXT_CONTENT));
                sb.append(operatorComment);
                sb.append(StringConst.HTML_TAG_DIV_END);
                sb.append(StringConst.HTML_TAG_DIV_END);

                if (eq.getAiAnalysis() != null && !eq.getAiAnalysis().isBlank()) {
                    sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_AI_CONTAINER));
                    sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_LABEL_TITLE));
                    sb.append(StringConst.TEXT_LABEL_AI_ANALYSIS);
                    sb.append(StringConst.HTML_TAG_DIV_END);
                    sb.append(String.format(StringConst.HTML_TAG_DIV_START_FMT, StringConst.STYLE_TEXT_CONTENT));
                    sb.append(StringConst.HTML_TAG_I_START);
                    sb.append(eq.getAiAnalysis());
                    sb.append(StringConst.HTML_TAG_I_END);
                    sb.append(StringConst.HTML_TAG_DIV_END);
                    sb.append(StringConst.HTML_TAG_DIV_END);
                }

                sb.append(StringConst.HTML_TAG_TD_END);
                sb.append(StringConst.HTML_TAG_TR_END);
            }
        }
        sb.append(StringConst.HTML_TAG_TABLE_END);

        sb.append(StringConst.MSG_CHART_ATTACHED);
        sb.append(StringConst.HTML_BODY_END);
        return sb.toString();
    }

    private String th(String text) {
        return String.format(StringConst.HTML_TH_FMT, StringConst.STYLE_CELL, text);
    }

    private String td(String text) {
        return String.format(StringConst.HTML_TD_FMT, StringConst.STYLE_CELL, text);
    }
}

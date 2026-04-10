package com.effitrack.server.controller;

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

import static com.effitrack.server.constant.StringConst.BASE_URL_REPORTS;
import static com.effitrack.server.constant.StringConst.BODY_TEMPLATE;
import static com.effitrack.server.constant.StringConst.COLOR_GREEN;
import static com.effitrack.server.constant.StringConst.COLOR_RED;
import static com.effitrack.server.constant.StringConst.ENDPOINT_REPORT_EQUIPMENT;
import static com.effitrack.server.constant.StringConst.ENDPOINT_SEND_REPORT;
import static com.effitrack.server.constant.StringConst.ERROR_PREFIX_OBJ_NOT_FOUND;
import static com.effitrack.server.constant.StringConst.HTML_BODY_END;
import static com.effitrack.server.constant.StringConst.HTML_BODY_START;
import static com.effitrack.server.constant.StringConst.HTML_BOLD_FMT;
import static com.effitrack.server.constant.StringConst.HTML_BR;
import static com.effitrack.server.constant.StringConst.HTML_H2_EQUIPMENT_REPORT;
import static com.effitrack.server.constant.StringConst.HTML_HEADER_TASKS;
import static com.effitrack.server.constant.StringConst.HTML_P_OPERATOR_FMT;
import static com.effitrack.server.constant.StringConst.HTML_P_SHOP_FMT;
import static com.effitrack.server.constant.StringConst.HTML_SPAN_STYLE_FMT;
import static com.effitrack.server.constant.StringConst.HTML_TABLE_START_FMT;
import static com.effitrack.server.constant.StringConst.HTML_TD_FMT;
import static com.effitrack.server.constant.StringConst.HTML_TD_STYLE;
import static com.effitrack.server.constant.StringConst.HTML_TH_FMT;
import static com.effitrack.server.constant.StringConst.HTML_TH_STYLE;
import static com.effitrack.server.constant.StringConst.HTML_TR_HEADER_FMT;
import static com.effitrack.server.constant.StringConst.MASTER_EMAIL;
import static com.effitrack.server.constant.StringConst.MSG_CHART_ATTACHED;
import static com.effitrack.server.constant.StringConst.MSG_NO_TASKS;
import static com.effitrack.server.constant.StringConst.NEW_LINE;
import static com.effitrack.server.constant.StringConst.OP_REPORT_EQUIPMENT_SUM;
import static com.effitrack.server.constant.StringConst.OP_REPORT_SEND_DESC;
import static com.effitrack.server.constant.StringConst.OP_REPORT_SEND_SUM;
import static com.effitrack.server.constant.StringConst.STYLE_CELL;
import static com.effitrack.server.constant.StringConst.STYLE_CELL_GREEN_BOLD;
import static com.effitrack.server.constant.StringConst.STYLE_CELL_ORANGE;
import static com.effitrack.server.constant.StringConst.STYLE_CELL_RED_BOLD;
import static com.effitrack.server.constant.StringConst.STYLE_COLOR_PREFIX;
import static com.effitrack.server.constant.StringConst.STYLE_ROW_HEADER;
import static com.effitrack.server.constant.StringConst.STYLE_TABLE;
import static com.effitrack.server.constant.StringConst.STYLE_TABLE_MT20;
import static com.effitrack.server.constant.StringConst.SUBJECT_EQUIPMENT_REPORT_TEMPLATE;
import static com.effitrack.server.constant.StringConst.SUBJECT_TEMPLATE;
import static com.effitrack.server.constant.StringConst.SUCCESS_MESSAGE;
import static com.effitrack.server.constant.StringConst.SUCCESS_REPORT_EQUIPMENT;
import static com.effitrack.server.constant.StringConst.TAG_REPORTS;
import static com.effitrack.server.constant.StringConst.TAG_REPORTS_DESC;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_DOWNTIME_MIN;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_EQUIPMENT;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_INV_NUM;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_SETUP_MIN;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_STATUS;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_TIME;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_TITLE;
import static com.effitrack.server.constant.StringConst.TEXT_HEADER_WORK_MIN;
import static com.effitrack.server.constant.StringConst.TEXT_STATUS_DONE;
import static com.effitrack.server.constant.StringConst.TEXT_STATUS_IN_PROGRESS;
import static com.effitrack.server.constant.StringConst.TEXT_TIME_SUFFIX;
import static com.effitrack.server.constant.StringConst.VAR_USER_ID;

@RestController
@Tag(name = TAG_REPORTS, description = TAG_REPORTS_DESC)
@RequestMapping(BASE_URL_REPORTS)
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

    @Operation(summary = OP_REPORT_SEND_SUM, description = OP_REPORT_SEND_DESC)
    @PostMapping(ENDPOINT_SEND_REPORT)
    public ResponseEntity<String> sendShiftReport(@PathVariable(VAR_USER_ID) Long userId) throws IOException, MessagingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<Task> weeklyTasks = taskRepository.findByAssigneeIdAndPlannedDateBetween(
                user.getId(),
                weekAgo.atStartOfDay(),
                today.atTime(LocalTime.MAX)
        );

        String tasksTableHtml = buildTasksTableHtml(weeklyTasks);

        String targetEmail = MASTER_EMAIL;
        if (user.getMaster() != null && user.getMaster().getEmail() != null && !user.getMaster().getEmail().isEmpty()) {
            targetEmail = user.getMaster().getEmail();
        }

        String subject = String.format(SUBJECT_TEMPLATE, user.getFullName(), LocalDate.now());

        String baseBody = String.format(BODY_TEMPLATE, user.getFullName(), user.getShopNumber())
                .replace(NEW_LINE, HTML_BR);

        String fullBody = HTML_BODY_START + baseBody + HTML_BR + HTML_BR + tasksTableHtml + HTML_BODY_END;

        byte[] chartImage = chartService.generateWeeklyProductivityChart(weeklyTasks, weekAgo, today);

        emailService.sendReportWithAttachment(targetEmail, subject, fullBody, chartImage);

        return ResponseEntity.ok(SUCCESS_MESSAGE);
    }

    @Operation(summary = OP_REPORT_EQUIPMENT_SUM)
    @PostMapping(ENDPOINT_REPORT_EQUIPMENT)
    public ResponseEntity<String> sendEquipmentReport(@PathVariable Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        List<Equipment> equipmentList = equipmentService.getUserEquipmentWithStats(userId);

        String reportHtml = buildEquipmentReportHtml(user, equipmentList);

        byte[] chartImage = chartService.generateEquipmentEfficiencyChart(equipmentList);

        String targetEmail = user.getMaster() != null ? user.getMaster().getEmail() : MASTER_EMAIL;
        String subject = String.format(SUBJECT_EQUIPMENT_REPORT_TEMPLATE, user.getFullName(), LocalDate.now());

        emailService.sendReportWithAttachment(targetEmail, subject, reportHtml, chartImage);

        return ResponseEntity.ok(SUCCESS_REPORT_EQUIPMENT);
    }

    private String buildTasksTableHtml(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return MSG_NO_TASKS;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(HTML_HEADER_TASKS);

        sb.append(String.format(HTML_TABLE_START_FMT, STYLE_TABLE));

        sb.append(String.format(HTML_TR_HEADER_FMT, STYLE_ROW_HEADER))
                .append(th(TEXT_HEADER_TITLE))
                .append(th(TEXT_HEADER_STATUS))
                .append(th(TEXT_HEADER_TIME))
                .append("</tr>");

        for (Task task : tasks) {
            boolean isDone = task.getStatus() == TaskStatus.DONE;
            String statusColor = isDone ? COLOR_GREEN : COLOR_RED;
            String statusText = isDone ? TEXT_STATUS_DONE : TEXT_STATUS_IN_PROGRESS;

            String statusHtml = String.format(HTML_SPAN_STYLE_FMT, STYLE_COLOR_PREFIX + statusColor + ";", statusText);
            String titleHtml = String.format(HTML_BOLD_FMT, task.getTitle());

            sb.append("<tr>")
                    .append(td(titleHtml))
                    .append(td(statusHtml))
                    .append(td(task.getActualMinutes() + TEXT_TIME_SUFFIX))
                    .append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    private String buildEquipmentReportHtml(User user, List<Equipment> equipmentList) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_BODY_START);
        sb.append(HTML_H2_EQUIPMENT_REPORT);
        sb.append(String.format(HTML_P_OPERATOR_FMT, user.getFullName()));
        sb.append(String.format(HTML_P_SHOP_FMT, user.getShopNumber()));

        sb.append(String.format(HTML_TABLE_START_FMT, STYLE_TABLE_MT20));
        sb.append(String.format(HTML_TR_HEADER_FMT, STYLE_ROW_HEADER))
                .append(String.format(HTML_TH_FMT, HTML_TH_STYLE, TEXT_HEADER_EQUIPMENT))
                .append(String.format(HTML_TH_FMT, HTML_TH_STYLE, TEXT_HEADER_INV_NUM))
                .append(String.format(HTML_TH_FMT, HTML_TH_STYLE, TEXT_HEADER_WORK_MIN))
                .append(String.format(HTML_TH_FMT, HTML_TH_STYLE, TEXT_HEADER_DOWNTIME_MIN))
                .append(String.format(HTML_TH_FMT, HTML_TH_STYLE, TEXT_HEADER_SETUP_MIN))
                .append("</tr>");

        for (Equipment eq : equipmentList) {
            sb.append("<tr>");
            sb.append(String.format(HTML_TD_FMT, HTML_TD_STYLE, eq.getName()));
            sb.append(String.format(HTML_TD_FMT, HTML_TD_STYLE, eq.getInventoryNumber()));
            sb.append(String.format(HTML_TD_FMT, STYLE_CELL_GREEN_BOLD, String.format(HTML_BOLD_FMT, eq.getWorkTimeTodayMinutes())));
            sb.append(String.format(HTML_TD_FMT, STYLE_CELL_RED_BOLD, String.format(HTML_BOLD_FMT, eq.getDowntimeTodayMinutes())));
            sb.append(String.format(HTML_TD_FMT, STYLE_CELL_ORANGE, eq.getSetupTodayMinutes()));
            sb.append("</tr>");
        }
        sb.append("</table>");

        sb.append(MSG_CHART_ATTACHED);
        sb.append(HTML_BODY_END);
        return sb.toString();
    }

    private String th(String text) {
        return String.format(HTML_TH_FMT, STYLE_CELL, text);
    }

    private String td(String text) {
        return String.format(HTML_TD_FMT, STYLE_CELL, text);
    }
}

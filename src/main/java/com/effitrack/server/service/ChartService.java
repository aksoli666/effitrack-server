package com.effitrack.server.service;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.Task;
import com.effitrack.server.model.TaskStatus;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ChartService {
    private static final Color COLOR_SUCCESS = Color.decode("#009951");
    private static final Color COLOR_CAUTION = Color.decode("#E5A000");
    private static final Color COLOR_TODO = Color.decode("#768088");
    private static final Color COLOR_ALERT = Color.decode("#D9534F");
    private static final Color COLOR_BG = Color.WHITE;
    private static final Color COLOR_GRID = Color.decode("#E5E9EA");
    private static final Color COLOR_NO_DATA = Color.LIGHT_GRAY;

    public byte[] generateWeeklyProductivityChart(List<Task> tasks, LocalDate start, LocalDate end) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringConst.DATE_FORMAT);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate currentDate = date;
            String label = currentDate.format(formatter);

            List<Task> dailyTasks = tasks.stream()
                    .filter(t -> t.getPlannedDate().toLocalDate().isEqual(currentDate))
                    .toList();

            long doneCount = dailyTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
            long inProgressCount = dailyTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
            long todoCount = dailyTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();

            dataset.addValue(doneCount, StringConst.ROW_KEY_DONE, label);
            dataset.addValue(inProgressCount, StringConst.ROW_KEY_IN_PROGRESS, label);
            dataset.addValue(todoCount, StringConst.ROW_KEY_ASSIGNED, label);
        }

        JFreeChart barChart = ChartFactory.createStackedBarChart(
                StringConst.CHART_TITLE_TASKS_PREFIX,
                StringConst.EMPTY_STRING,
                StringConst.EMPTY_STRING,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(COLOR_BG);
        plot.setRangeGridlinePaint(COLOR_GRID);
        plot.setOutlineVisible(false);

        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        renderer.setSeriesPaint(0, COLOR_SUCCESS);
        renderer.setSeriesPaint(1, COLOR_CAUTION);
        renderer.setSeriesPaint(2, COLOR_TODO);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);

        return ChartUtils.encodeAsPNG(barChart.createBufferedImage(800, 450));
    }

    public byte[] generateEquipmentEfficiencyChart(List<Equipment> equipmentList) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();

        long totalWork = equipmentList.stream().mapToLong(Equipment::getWorkTimeTodayMinutes).sum();
        long totalDowntime = equipmentList.stream().mapToLong(Equipment::getDowntimeTodayMinutes).sum();
        long totalSetup = equipmentList.stream().mapToLong(Equipment::getSetupTodayMinutes).sum();

        if (totalWork == 0 && totalDowntime == 0 && totalSetup == 0) {
            dataset.setValue(StringConst.CHART_LABEL_NO_DATA, 1);
        } else {
            dataset.setValue(StringConst.TEXT_HEADER_WORK_MIN, totalWork);
            dataset.setValue(StringConst.TEXT_HEADER_DOWNTIME_MIN, totalDowntime);
            dataset.setValue(StringConst.TEXT_HEADER_SETUP_MIN, totalSetup);
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                StringConst.CHART_TITLE_EFFICIENCY,
                dataset,
                true, true, false
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(COLOR_BG);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelBackgroundPaint(COLOR_BG);
        plot.setSimpleLabels(true);

        plot.setLabelGenerator(null);

        plot.setSectionPaint(StringConst.TEXT_HEADER_WORK_MIN, COLOR_SUCCESS);
        plot.setSectionPaint(StringConst.TEXT_HEADER_DOWNTIME_MIN, COLOR_ALERT);
        plot.setSectionPaint(StringConst.TEXT_HEADER_SETUP_MIN, COLOR_CAUTION);
        plot.setSectionPaint(StringConst.CHART_LABEL_NO_DATA, COLOR_NO_DATA);

        return ChartUtils.encodeAsPNG(pieChart.createBufferedImage(600, 400));
    }
}

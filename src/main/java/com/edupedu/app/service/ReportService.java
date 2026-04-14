package com.edupedu.app.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.edupedu.app.model.Attendance;
import com.edupedu.app.repository.AttendanceRepository;
import com.edupedu.app.repository.SessionRepository;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    // private final InvoiceRepository invoiceRepository;
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    // public Map<String, Object> getRevenueReport(int year, int month) {
    //     List<Invoice> invoices = invoiceRepository.findAll().stream()
    //             .filter(i -> i.getYear() == year && i.getMonth() == month)
    //             .toList();

    //     long totalInvoiced = invoices.stream().mapToLong(Invoice::getAmountDue).sum();
    //     long totalPaid = invoices.stream().mapToLong(i -> i.getAmountPaid() != null ? i.getAmountPaid() : 0).sum();
    //     long totalDebt = totalInvoiced - totalPaid;

    //     Map<String, Object> report = new HashMap<>();
    //     report.put("year", year);
    //     report.put("month", month);
    //     report.put("totalInvoiced", totalInvoiced);
    //     report.put("totalPaid", totalPaid);
    //     report.put("totalDebt", totalDebt);
    //     return report;
    // }

    public Map<String, Object> getAttendanceStats(Long classGroupId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> records = attendanceRepository.findAll().stream()
                .filter(a -> (classGroupId == null || a.getSchedule().getStudentGroup().getId().equals(classGroupId)))
                .filter(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
                .toList();

        long present = records.stream().filter(r -> r.getStatus().name().equals("PRESENT")).count();
        long total = records.size();
        double percentage = total > 0 ? (double) present / total * 100 : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", total);
        stats.put("presentCount", present);
        stats.put("attendancePercentage", Math.round(percentage * 100.0) / 100.0);
        return stats;
    }

    public Map<String, Object> getTeacherWorkload(Long teacherId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        long sessionCount = sessionRepository.countByTeacherAndDateRange(teacherId, startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("teacherId", teacherId);
        report.put("year", year);
        report.put("month", month);
        report.put("sessionCount", sessionCount);
        return report;
    }
}

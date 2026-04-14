// package com.edupedu.app.service;

// import java.time.LocalDate;
// import java.util.List;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.edupedu.app.model.Invoice;
// import com.edupedu.app.model.Student;
// import com.edupedu.app.model.enums.InvoiceStatus;
// import com.edupedu.app.repository.InvoiceRepository;
// import com.edupedu.app.repository.StudentGroupRepository;
// import com.edupedu.app.repository.StudentRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class InvoiceService {

//     private final InvoiceRepository invoiceRepository;
//     private final StudentRepository studentRepository;
//     private final StudentGroupRepository studentGroupRepository;

//     @Transactional
//     public void generateAllMonthlyInvoices(int year, int month) {
//         List<Student> students = studentRepository.findAll();
//         for (Student student : students) {
//             if (student.getStudentGroup() != null && 
//                 student.getStudentGroup().getMonthlyFee() != null && 
//                 student.getStudentGroup().getMonthlyFee() > 0) {
//                 try {
//                     generateMonthlyInvoiceForStudent(student.getId(), year, month);
//                 } catch (Exception e) {
//                     // Skip if already exists or other error
//                 }
//             }
//         }
//     }

//     @Transactional
//     public void generateMonthlyInvoicesForGroup(Long groupId, int year, int month) {
//         ClassGroup group = classGroupRepository.findById(groupId)
//                 .orElseThrow(() -> new RuntimeException("Group not found"));

//         if (group.getMonthlyFee() == null || group.getMonthlyFee() <= 0) {
//             throw new RuntimeException("Group has no monthly fee set");
//         }

//         List<Student> students = group.getStudents();
//         for (Student student : students) {
//             try {
//                 generateMonthlyInvoiceForStudent(student.getId(), year, month);
//             } catch (Exception e) {
//                 // Ignore existing
//             }
//         }
//     }

//     @Transactional
//     public void generateMonthlyInvoiceForStudent(Long studentId, int year, int month) {
//         Student student = studentRepository.findById(studentId)
//                 .orElseThrow(() -> new RuntimeException("Student not found"));

//         if (student.getClassGroup() == null) {
//             throw new RuntimeException("Student has no class group");
//         }

//         int fee = student.getClassGroup().getMonthlyFee() != null ? student.getClassGroup().getMonthlyFee() : 0;
//         LocalDate dueDate = LocalDate.of(year, month, 1).plusMonths(1).withDayOfMonth(5);

//         boolean exists = invoiceRepository.findByStudentId(studentId).stream()
//                 .anyMatch(i -> i.getYear() != null && i.getYear() == year &&
//                         i.getMonth() != null && i.getMonth() == month);

//         if (exists) {
//             throw new RuntimeException("Invoice already exists");
//         }

//         Invoice invoice = Invoice.builder()
//                 .student(student)
//                 .amountDue(fee)
//                 .amountPaid(0)
//                 .dueDate(dueDate)
//                 .year(year)
//                 .month(month)
//                 .status(InvoiceStatus.UNPAID)
//                 .build();

//         invoiceRepository.save(invoice);
//     }

//     public List<Invoice> getStudentInvoices(Long studentId) {
//         return invoiceRepository.findByStudentId(studentId);
//     }

//     public List<Invoice> getInvoicesByIdentifier(String identifier) {
//         Student student = studentRepository.findByStudentNumberOrAccountNumber(identifier, identifier)
//                 .orElseThrow(() -> new RuntimeException("Student with ID/Account not found"));
//         return invoiceRepository.findByStudentId(student.getId());
//     }

//     public long calculateTotalDebt(Long studentId) {
//         return invoiceRepository.findByStudentId(studentId).stream()
//                 .filter(i -> i.getStatus() != InvoiceStatus.PAID && i.getStatus() != InvoiceStatus.CANCELLED)
//                 .mapToLong(i -> i.getAmountDue() - (i.getAmountPaid() != null ? i.getAmountPaid() : 0))
//                 .sum();
//     }
// }

package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.LocalDate;

public record Semester(
    SemesterCode semesterCode,
    LocalDate startDate,
    LocalDate endDate
) {
}

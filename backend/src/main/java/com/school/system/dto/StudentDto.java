package com.school.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long id;
    private String admissionNumber;
    private String rollNumber;
    private String name;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String religion;
    private String nationality;
    private String category;
    private String photoUrl;
    private Long classId;
    private String className;
    private Long sectionId;
    private String sectionName;
    private Long parentId;
    private String parentName;
    private String phone;
    private String email;
    private String status;
    private String medicalInfo;
    private String emergencyContact;
    private String academicYear;
}

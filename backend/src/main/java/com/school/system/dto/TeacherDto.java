package com.school.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String photoUrl;
    private String qualification;
    private Integer experience;
    private BigDecimal salary;
    private String department;
    private String status;
}

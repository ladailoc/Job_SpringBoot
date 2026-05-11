package com.example.job_code.dto;

import com.example.job_code.model.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobPostingForm {
    @NotBlank(message = "Tiêu đề công việc không được để trống")
    @Size(max = 150, message = "Tiêu đề tối đa 150 ký tự")
    private String title;

    @NotBlank(message = "Mô tả công việc không được để trống")
    @Size(min = 20, message = "Mô tả công việc phải có ít nhất 20 ký tự")
    private String description;

    @NotBlank(message = "Địa điểm không được để trống")
    @Size(max = 100, message = "Địa điểm tối đa 100 ký tự")
    private String location;

    @NotNull
    @DecimalMin(value = "0.0", message = "Lương tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal salaryMin;

    @NotNull(message = "Lương tối đa không được để trống")
    @DecimalMin(value = "0.0", message = "Lương tối đa phải lớn hơn hoặc bằng 0")
    private BigDecimal salaryMax;

    @NotNull(message = "Loại công việc không được để trống")
    private JobType jobType = JobType.FULL_TIME;

    private boolean active = true;
}

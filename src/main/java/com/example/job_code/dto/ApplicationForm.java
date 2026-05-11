package com.example.job_code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ApplicationForm {
    @NotBlank(message = "Cover letter không được để trống")
    @Size(min = 20, max = 2000, message = "Cover letter phải từ 20 đến 2000 ký tự")
    private String coverLetter;

    @NotBlank(message = "CV link không được để trống")
    @URL(message = "CV link phải là URL hợp lệ")
    @Size(max = 500, message = "CV link tối đa 500 ký tự")
    private String cvLink;

}

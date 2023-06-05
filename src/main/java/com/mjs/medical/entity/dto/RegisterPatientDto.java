package com.mjs.medical.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RegisterPatientDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String patientName;

    private Integer patientAge;

    private String patientSex;

    private String patientAddress;

    private String patientPhone;

    private String idNumber;

    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String doctorName;
}

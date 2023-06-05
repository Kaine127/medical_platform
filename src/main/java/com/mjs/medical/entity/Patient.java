package com.mjs.medical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER)
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
    @TableLogic
    private Integer isDeleted;
}

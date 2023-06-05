package com.mjs.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("patient_case")
@Data
public class PatientCase implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private Long patientId;

    private String patientName;

    private String patientSex;

    private Integer patientAge;

    private Long doctorId;

    private String doctorName;

    private String detailCase;

    private String simpleCase;

    private String detailCause;

    private String simpleCause;

    private String causeImages;

    private String listTitle;

    private LocalDateTime createTime;
}

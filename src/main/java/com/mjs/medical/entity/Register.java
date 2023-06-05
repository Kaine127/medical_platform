package com.mjs.medical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Register implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private Long userId;

    private Long registerId;

    private String registerName;

    private Integer registerNumber;

    private LocalDateTime registerTime;

    private Long doctorId;

    private String doctorName;

    private Integer status;

    private LocalDateTime createTime;

    private Integer sign;

    private String registerDay;

    private String week;


}

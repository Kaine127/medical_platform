package com.mjs.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 时间表
 */
@Data
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private Long doctorId;

    private String name;

    private Integer register;

    private Integer status;

    private String ensureOnly;

    private Integer number;

    private Integer numberMax;

    public void addEnsureOnly(){
        this.ensureOnly = doctorId.toString() + register.toString();
    }
}

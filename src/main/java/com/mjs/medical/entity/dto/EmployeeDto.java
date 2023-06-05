package com.mjs.medical.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
}

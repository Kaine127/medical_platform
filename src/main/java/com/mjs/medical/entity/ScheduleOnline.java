package com.mjs.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@TableName("schedule_online")
@Data
public class ScheduleOnline implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private Long doctorId;

    private Long scheduleId;

    private String name;

    private Integer register;

    private Integer number;

    private Integer numberMax;

    private String week;

    private String registerDay;

    private LocalDateTime createTime;

    private Integer status;
}

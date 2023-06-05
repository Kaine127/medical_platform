package com.mjs.medical.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mjs.medical.entity.ScheduleOnline;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ScheduleOnlineDto  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String patientName;

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

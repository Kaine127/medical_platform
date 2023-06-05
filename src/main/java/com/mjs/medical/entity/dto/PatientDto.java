package com.mjs.medical.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatientDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long patientId;
}

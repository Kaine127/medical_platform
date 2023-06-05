package com.mjs.medical.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class DetailCause implements Serializable {
    private static final long serialVersionUID = 1L;
    private String patientName;

    private String patientSex;

    private Integer patientAge;

    private String doctorName;

    private String simpleCause;

    private String detailCause;

    private String causeImages;
}

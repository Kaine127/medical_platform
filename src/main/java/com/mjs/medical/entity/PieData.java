package com.mjs.medical.entity;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class PieData  implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer value;

    private String name;

    public PieData(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}

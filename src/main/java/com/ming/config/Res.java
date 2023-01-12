package com.ming.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author liuming
 * @description
 * @date 2023/1/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Res implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private Object data = "";
    private String message = "";
}

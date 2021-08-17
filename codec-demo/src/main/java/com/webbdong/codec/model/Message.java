package com.webbdong.codec.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Webb Dong
 * @date 2021-08-16 7:23 PM
 */
@Data
@SuperBuilder
public class Message<T> implements Serializable {

    private Integer code;

    private T data;

    private String msg;

}

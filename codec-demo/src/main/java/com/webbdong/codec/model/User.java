package com.webbdong.codec.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Webb Dong
 * @date 2021-08-16 6:13 PM
 */
@Data
@SuperBuilder
public class User implements Serializable {

    private Long id;

    private String name;

}

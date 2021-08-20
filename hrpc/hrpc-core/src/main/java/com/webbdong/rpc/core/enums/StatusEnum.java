package com.webbdong.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Webb Dong
 * @date 2021-08-19 1:37 AM
 */
@AllArgsConstructor
@Getter
public enum StatusEnum {

    SUCCESS(200, "OK"),

    NOT_FOUND_SERVICE_PROVIDER(100001, "not found service provider");

    private Integer code;

    private String description;

}

package com.webbdong.rpc.core.util;

/**
 * @author Webb Dong
 * @date 2021-08-19 1:36 AM
 */
public class RequestIdUtil {

    public static String requestId() {
        return GlobalIDGenerator.getInstance().nextStrId();
    }

    private RequestIdUtil() {}

}

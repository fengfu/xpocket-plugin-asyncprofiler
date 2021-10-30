package com.perfma.xpocket.plugin.asyncprofiler.utils;

/**
 * 操作系统类型
 * @author fengfu
 * @date 2021.10.28
 **/
public enum OSType {
    Windows("windows"),
    MacOS("mac"),
    Linux("linux"),
    Other("other");

    private String typeName;
    OSType(String osType) {
        this.typeName = osType;
    }

    public String getTypeName() {
        return typeName;
    }

    public static OSType of(String osName) {
        if ((osName.indexOf("mac") >= 0) || (osName.indexOf("darwin") >= 0)) {
            return OSType.MacOS;
        } else if (osName.indexOf("win") >= 0) {
            return OSType.Windows;
        } else if (osName.indexOf("nux") >= 0) {
            return OSType.Linux;
        } else {
            return OSType.Other;
        }
    }
}

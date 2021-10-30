package com.perfma.xpocket.plugin.asyncprofiler.utils;

/**
 * 架构类型
 * @author fengfu
 * @date 2021.10.28
 **/
public enum ArchType {
    X86("x86_64"),
    Amd("amd64"),
    Arm("aarch64"),
    Other("other");

    private String typeName;
    ArchType(String osType) {
        this.typeName = osType;
    }

    public String getTypeName() {
        return typeName;
    }

    public static ArchType of(String archName) {
        if (archName.indexOf("x86") >= 0) {
            return ArchType.X86;
        } else if (archName.indexOf("amd") >= 0) {
            return ArchType.Amd;
        } else if (archName.indexOf("aarch") >= 0) {
            return ArchType.Arm;
        } else {
            return ArchType.Other;
        }
    }
}

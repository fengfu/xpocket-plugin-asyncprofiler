package com.perfma.xpocket.plugin.asyncprofiler.utils;

/**
 * @author fengfu
 * @date 2021.10.28
 **/
public class OSUtils {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final OSType OS_TYPE = OSType.of(OS);

    public static boolean isWindows() {
        return OS.indexOf("win") >= 0;
    }

    public static boolean isWindowsXP() {
        return OS.indexOf("win") >= 0 && OS.indexOf("xp") >= 0;
    }

    public static boolean isMac() {
        return OS.indexOf("mac") >= 0;
    }

    public static boolean isUnix() {
        return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0;
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

    private static final String ARCH = System.getProperty("os.arch");
    private static final ArchType ARCH_TYPE = ArchType.of(ARCH);

    public static String getArch() {
        return ARCH;
    }

    public static boolean isArm() {
        return ARCH.startsWith("aarch");
    }

    public static boolean is64() {
        return "64".equals(ARCH);
    }

    public static boolean is32() {
        return "32".equals(ARCH);
    }

    public static String getOS() {
        return OS;
    }

    public static OSType getOSType() {
        return OS_TYPE;
    }

    public static ArchType getArchType() {
        return ARCH_TYPE;
    }
}

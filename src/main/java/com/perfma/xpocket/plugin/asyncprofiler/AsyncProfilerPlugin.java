package com.perfma.xpocket.plugin.asyncprofiler;

import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xpocket.plugin.asyncprofiler.utils.OSUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Fengfu.Qu <qufengfu@gmail.com>
 * @since 2021/9/22
 */
public class AsyncProfilerPlugin extends AbstractXPocketPlugin {

    private static final String LOGO =
        "                                                                ____ _  __           \n" +
        "   ____ _ _____ __  __ ____   _____        ____   _____ ____   / __/(_)/ /___   _____\n" +
        "  / __ `// ___// / / // __ \\ / ___/______ / __ \\ / ___// __ \\ / /_ / // // _ \\ / ___/\n" +
        " / /_/ /(__  )/ /_/ // / / // /__ /_____// /_/ // /   / /_/ // __// // //  __// /    \n" +
        " \\__,_//____/ \\__, //_/ /_/ \\___/       / .___//_/    \\____//_/  /_//_/ \\___//_/     \n" +
        "             /____/                    /_/                                           ";
    
    private static final String USER_HOME = System.getProperty("user.home");

    private static final String TOOL_NAME = "async-profiler";

    private static final String path = USER_HOME + File.separator + ".xpocket" 
            + File.separator + "." + TOOL_NAME + File.separator;
    private static final String file = "async-profiler.zip";

    /**
     * 插件首次被初始化时被调用
     * @param process
     */
    @Override
    public void init(XPocketProcess process) {
        try {
            // 获取操作系统类型
            if (OSUtils.isWindows() || OSUtils.isSolaris()) {
                System.err.println(TOOL_NAME + "不支持当前的操作系统:" + OSUtils.getOS());
                return;
            }

            // 获取操作系统架构
            if (OSUtils.isMac() && OSUtils.isArm()) {
                System.err.println(TOOL_NAME + "不支持当前的架构:" + OSUtils.getArch());
                return;
            }

            String osTypeName = OSUtils.getOSType().getTypeName();
            String archTypeName = OSUtils.getArchType().getTypeName();

            File folder = new File(path);
            if(!folder.exists()) {
                folder.mkdirs();
            }

            File shFile = new File(path + "profiler.sh");
            if (shFile.exists()) {
                return;
            }

            // 拷贝文件
            InputStream is = AsyncProfilerPlugin.class.getClassLoader()
                    .getResourceAsStream("." + TOOL_NAME + "/" + osTypeName + "/" + archTypeName + "/" + file);
            Path targetFile = new File(path + file).toPath();
            Files.copy(is, targetFile);
            is.close();

            // 解压文件
            File zipFile = new File(path + file);
            ZipUtil.unpack(zipFile, new File(path));

            // 设置执行权限
            shFile.setExecutable(true);
            File file = new File(path + File.separator + "build/" + File.separator + "jattach");
            file.setExecutable(true);

            if (OSUtils.isUnix()) {
                file = new File(path + File.separator + "build/" + File.separator + "fdtransfer");
                file.setExecutable(true);
            }

            // 删除zip文件
            zipFile.delete();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 插件会话被切出时被调用
     * @param context
     */
    @Override
    public void switchOff(SessionContext context) {
        super.switchOff(context);
    }

    /**
     * 用于输出自定义LOGO
     * @param process
     */
    @Override
    public void printLogo(XPocketProcess process) {
        //process.output(LOGO);
        System.out.println(LOGO);
    }

    /**
     * 插件会话被切入时被调用
     * @param context
     */
    @Override
    public void switchOn(SessionContext context) {
        super.switchOn(context);
    }

    /**
     * XPocket整体退出时被调用，用于清理插件本身使用的资源
     * @throws Throwable
     */
    @Override
    public void destory() throws Throwable {
        super.destory();
    }

    @Override
    public String logo() {
        return LOGO;
    }

    public static String getPluginHome() {
        return path;
    }
}

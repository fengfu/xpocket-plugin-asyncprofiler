package com.perfma.xpocket.plugin.asyncprofiler.command;

import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xpocket.plugin.asyncprofiler.AsyncProfilerPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Profiling command for async-prolfiler
 *
 * @author Fengfu.Qu <qufengfu@gmail.com>
 * @since 2021/9/24
 */
@CommandInfo(name = "profiler", usage = "Sampling CPU and HEAP profiler for Java featuring AsyncGetCallTrace + perf_events.")
public class AsyncProfilerCommand extends AbstractXPocketCommand {
    private Process profilerProc;

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        BufferedReader stdInput = null, errInput = null;
        try {
            String fullCmd = handleCmdStr(process.getCmd(), process.getArgs());
            profilerProc = Runtime.getRuntime().exec(fullCmd);
            stdInput = new BufferedReader(new InputStreamReader(profilerProc.getInputStream()));
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                process.output(line);
            }

            errInput = new BufferedReader(new InputStreamReader(profilerProc.getErrorStream()));
            while ((line = errInput.readLine()) != null) {
                process.output(line);
            }

        } catch (Throwable ex) {
            process.output(ex.getMessage());
        } finally {
            try {
                stdInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                errInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        process.end();
    }

    private String handleCmdStr(String cmd, String[] args) {
        StringBuilder cmdStr = new StringBuilder("sh ");

        cmdStr.append(AsyncProfilerPlugin.getPluginHome());
        cmdStr.append(cmd).append(".sh");
        if (args != null) {
            for (String arg : args) {
                cmdStr.append(' ').append(arg);
            }
        }

        cmdStr.append("\n");
        return cmdStr.toString();
    }

    @Override
    public String details(String cmd) {
        return "## Profiler Options\n" +
                "The following is a complete list of the command-line options accepted by `profiler.sh` script.\n" +
                "\n" +
                "start" + "\n" +
                "  starts profiling in semi-automatic mode, i.e. profiler will run until `stop` command is explicitly called.\n" +
                "resume" + "\n" +
                "  starts or resumes earlier profiling session that has been stopped. All the collected data remains valid. The profiling options are not preserved between sessions, and should be specified again.\n" +
                "stop" + "\n" +
                "  stops profiling and prints the report.\n" +
                "dump" + "\n" +
                "  dump collected data without stopping profiling session.\n" +
                "check" + "\n" +
                "  check if the specified profiling event is available.\n" +
                "status" + "\n" +
                "  prints profiling status: whether profiler is active and for how long.\n" +
                "list" + "\n" +
                "  show the list of available profiling events. This option still requires PID, since supported events may differ depending on JVM version.\n" +
                " -d N" +  "\n" +
                "  the profiling duration, in seconds. If no `start`, `resume`, `stop` or `status` option is given, the profiler will run for the specified period of time and then automatically stop.  \n" +
                "  Example: `./profiler.sh -d 30 8983`\n" +
                "-e event" +  "\n" +
                "  the profiling event: `cpu`, `alloc`, `lock`, `cache-misses` etc. Use `list` to see the complete list of available events.\n" +
                "  In allocation profiling mode the top frame of every call trace is the class of the allocated object, and the counter is the heap pressure (the total size of allocated TLABs or objects outside TLAB).\n" +
                "  In lock profiling mode the top frame is the class of lock/monitor, and the counter is number of nanoseconds it took to enter this lock/monitor.\n" +
                "  Two special event types are supported on Linux: hardware breakpoints and kernel tracepoints:\n" +
                "    - `-e mem:<func>[:rwx]` sets read/write/exec breakpoint at function\n" +
                "      `<func>`. The format of `mem` event is the same as in `perf-record`. Execution breakpoints can be also specified by the function name, e.g. `-e malloc` will trace all calls of native `malloc` function.\n" +
                "    - `-e trace:<id>` sets a kernel tracepoint. It is possible to specify tracepoint symbolic name, e.g. `-e syscalls:sys_enter_open` will trace all `open` syscalls.\n" +
                "-i N" + "\n" +
                "  sets the profiling interval in nanoseconds or in other units, if N is followed by `ms` (for milliseconds), `us` (for microseconds), or `s` (for seconds). Only CPU active time is counted. No samples are collected while CPU is idle. The default is 10000000 (10ms).  \n" +
                "  Example: `./profiler.sh -i 500us 8983`\n" +
                "--alloc N" + "\n" +
                "  allocation profiling interval in bytes or in other units, if N is followed by `k` (kilobytes), `m` (megabytes), or `g` (gigabytes).\n" +
                "--lock N" + "\n" +
                "  lock profiling threshold in nanoseconds (or other units). In lock profiling mode, record contended locks that the JVM has waited for longer than the specified duration.\n" +
                "-j N" + "\n" +
                "  sets the Java stack profiling depth. This option will be ignored if N is greater than default 2048.  \n" +
                "  Example: `./profiler.sh -j 30 8983`\n" +
                "-t" + "\n" +
                "  profile threads separately. Each stack trace will end with a frame that denotes a single thread.  \n" +
                "  Example: `./profiler.sh -t 8983`\n" +
                "-s" + "\n" +
                "  print simple class names instead of FQN.\n" +
                "-g" + "\n" +
                "  print method signatures.\n" +
                "-a" + "\n" +
                "  annotate Java method names by adding `_[j]` suffix.\n" +
                "-l" + "\n" +
                "  prepend library names to symbols, e.g. ``libjvm.so`JVM_DefineClassWithSource``.\n" +
                "-o fmt" + "\n" +
                "  specifies what information to dump when profiling ends.\n" +
                "  `fmt` can be one of the following options:\n" +
                "    - `traces[=N] : dump call traces (at most N samples);\n" +
                "    - `flat[=N] : dump flat profile (top N hot methods);  \n" +
                "      can be combined with `traces`, e.g. `traces=200,flat=200`\n" +
                "    - `jfr : dump events in Java Flight Recorder format readable by Java Mission Control.\n" +
                "      This *does not* require JDK commercial features to be enabled.\n" +
                "    - `collapsed : dump collapsed call traces in the format used by\n" +
                "      [FlameGraph](https://github.com/brendangregg/FlameGraph) script. This is a collection of call stacks, where each line is a semicolon separated list of frames followed by a counter.\n" +
                "    - `flamegraph : produce Flame Graph in HTML format.\n" +
                "    - `tree : produce Call Tree in HTML format.  \n" +
                "      `--reverse` option will generate backtrace view.\n" +
                "--total" + "\n" +
                "  count the total value of the collected metric instead of the number of samples, e.g. total allocation size.\n" +
                "--chunksize N, --chunktime N" + "\n" +
                "  approximate size and time limits for a single JFR chunk.\n" +
                "  Example: `./profiler.sh -f profile.jfr --chunksize 100m --chunktime 1h 8983`\n" +
                "-I include, -X exclude" + "\n" +
                "  filter stack traces by the given pattern(s).\n" +
                "  `-I` defines the name pattern that *must* be present in the stack traces, while `-X` is the pattern that *must not* occur in any of stack traces in the output.\n" +
                "  `-I` and `-X` options can be specified multiple times. A pattern may begin or end with a star `*` that denotes any (possibly empty) sequence of characters.  \n" +
                "  Example: `./profiler.sh -I 'Primes.*' -I 'java/*' -X '*Unsafe.park*' 8983`\n" +
                " --title TITLE, --minwidth PERCENT, --reverse" + "\n" +
                "  FlameGraph parameters.  \n" +
                "  Example: `./profiler.sh -f profile.html --title \"Sample CPU profile\" --minwidth 0.5 8983`\n" +
                "-f FILENAME" + "\n" +
                "  the file name to dump the profile information to.  \n" +
                "  `%p` in the file name is expanded to the PID of the target JVM;  \n" +
                "  `%t : to the timestamp at the time of command invocation.  \n" +
                "  Example: `./profiler.sh -o collapsed -f /tmp/traces-%t.txt 8983`\n" +
                "--all-user" + "\n" +
                "  include only user-mode events. This option is helpful when kernel profiling is restricted by `perf_event_paranoid` settings.  \n" +
                "--sched" + "\n" +
                "  group threads by Linux-specific scheduling policy: BATCH/IDLE/OTHER.\n" +
                "--cstack MODE" + "\n" +
                "  how to traverse native frames (C stack). Possible modes are `fp` (Frame Pointer), `lbr` (Last Branch Record, available on Haswell since Linux 4.1), and `no` (do not collect C stack).\n" +
                "  By default, C stack is shown in cpu, itimer, wall-clock and perf-events profiles.\n" +
                "  Java-level events like `alloc` and `lock` collect only Java stack.\n" +
                "--begin function, --end function" + "\n" +
                "  automatically start/stop profiling when the specified native function is executed.\n" +
                "--ttsp" + "\n" +
                "  time-to-safepoint profiling. An alias for  \n" +
                "  `--begin SafepointSynchronize::begin --end RuntimeService::record_safepoint_synchronized`  \n" +
                "  It is not a separate event type, but rather a constraint. Whatever event type you choose (e.g. `cpu` or `wall`), the profiler will work as usual, except that only events between the safepoint request and the start of the VM operation will be recorded.\n" +
                "--jfrsync CONFIG" + "\n" +
                "  start Java Flight Recording with the given configuration synchronously with the profiler. The output .jfr file will include all regular JFR events, except that execution samples will be obtained from async-profiler.\n" +
                "  This option implies `-o jfr`.\n" +
                "    - `CONFIG` is a predefined JFR profile or a JFR configuration file (.jfc).\n" +
                "  Example: `./profiler.sh -e cpu --jfrsync profile -f combined.jfr 8983`\n" +
                "--fdtransfer" + "\n" +
                "  runs \"fdtransfer\" alongside, which is a small program providing an interface for the profiler to access, `perf_event_open` even while this syscall is unavailable for the profiled process (due to low privileges).\n" +
                "  See [Profiling Java in a container](#profiling-java-in-a-container).\n" +
                "-v, --version" + "\n" +
                "  prints the version of profiler library. If PID is specified, gets the version of the library loaded into the given process.";
    }
}

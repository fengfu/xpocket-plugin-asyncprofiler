package com.perfma.xpocket.plugin.asyncprofiler;

import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncProfilerPluginTest {

    private AsyncProfilerPlugin asyncProfilerPluginUnderTest;

    @Before
    public void setUp() {
        asyncProfilerPluginUnderTest = new AsyncProfilerPlugin();
    }

    @Test
    public void testInit() {
        // Setup
        final XPocketProcess process = null;

        // Run the test
        asyncProfilerPluginUnderTest.init(process);

        // Verify the results
    }

    @Test
    public void testSwitchOff() {
        // Setup
        final SessionContext context = null;

        // Run the test
        asyncProfilerPluginUnderTest.switchOff(context);

        // Verify the results
    }

    @Test
    public void testPrintLogo() {
        // Setup
        final XPocketProcess process = null;

        // Run the test
        asyncProfilerPluginUnderTest.printLogo(process);

        System.out.println(System.getProperty("os.arch"));

        // Verify the results
    }

    @Test
    public void testSwitchOn() {
        // Setup
        final SessionContext context = null;

        // Run the test
        asyncProfilerPluginUnderTest.switchOn(context);

        // Verify the results
    }

    @Test
    public void testDestory() throws Throwable {
        // Setup

        // Run the test
        asyncProfilerPluginUnderTest.destory();

        // Verify the results
    }

    @Test
    public void testLogo() {
        // Setup

        // Run the test
        final String result = asyncProfilerPluginUnderTest.logo();

        // Verify the results
        // assertEquals("result", result);
    }
}

/**
 * 
 */
package org.jsoar.performancetesting.jsoar;

import org.jsoar.kernel.Agent;
import org.jsoar.kernel.RunType;
import org.jsoar.kernel.SoarException;
import org.jsoar.kernel.SoarProperties;
import org.jsoar.performancetesting.Test;
import org.jsoar.performancetesting.TestSettings;
import org.jsoar.util.NullWriter;
import org.jsoar.util.commands.SoarCommandInterpreter;
import org.jsoar.util.commands.SoarCommands;

/**
 * A JSoarTest.  This class is used to launch and run all JSoar tests.
 * 
 * @author ALT
 *
 */
public class JSoarTest implements Test
{
    private String testName;
    private String testFile;
    
    private Agent agent;
    
    private Double cpuTime;
    private Double kernelTime;
    private int decisionsRunFor;
    private long memoryForRun;
    
    private TestSettings settings;
    
    /**
     * Sets all the values used in the test to be impossible
     * values so we know if something failed horribly.
     */
    public JSoarTest()
    {
        this.agent = null;
        
        this.cpuTime = -1.0;
        this.kernelTime = -1.0;
        this.decisionsRunFor = -1;
        this.memoryForRun = -1;
    }
    
    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#initialize(java.lang.String, java.lang.String)
     */
    @Override
    public void initialize(String testName, String testFile, TestSettings settings)
    {
        this.testName = testName;
        this.testFile = testFile;
        this.settings = settings;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getTestName()
     */
    @Override
    public String getTestName()
    {
        return testName;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getTestFile()
     */
    @Override
    public String getTestFile()
    {
        return testFile;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#run()
     */
    @Override
    public boolean run(int runCount) throws SoarException
    {
        // This is to make it very likely that the garbage collector has cleaned up all references and freed memory
        // http://stackoverflow.com/questions/1481178/forcing-garbage-collection-in-java
        System.gc();
        System.gc();
        
        memoryForRun = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        agent = new Agent("JSoar Performance Testing Agent - " + testName + " - " + runCount);
        agent.getTrace().setEnabled(false);
        agent.getPrinter().pushWriter(new NullWriter());
        agent.initialize();
        SoarCommandInterpreter ifc = agent.getInterpreter();
        
        SoarCommands.source(ifc, testFile);
        
        if (settings.isUsingSeed())
        {
            ifc.eval("srand " + settings.getSeed());
        }
        
        ifc.eval("set-stop-phase -o");
                
        if (settings.getDecisionCycles() == 0)
            agent.runForever();
        else
            agent.runFor(settings.getDecisionCycles(), RunType.DECISIONS);
        
        cpuTime = agent.getTotalCpuTimer().getTotalSeconds();
        kernelTime = agent.getTotalKernelTimer().getTotalSeconds();
        
        //This is the same counter as the stats command
        decisionsRunFor = agent.getProperties().get(SoarProperties.DECISION_PHASES_COUNT).intValue();
        memoryForRun = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryForRun;
        
        agent.dispose();
        
        agent = null;
        
        return true;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#reset()
     */
    @Override
    public boolean reset()
    {
        if (agent != null)
        {
            agent.dispose();
            agent = null;
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getCPURunTime()
     */
    @Override
    public double getCPURunTime()
    {
        return cpuTime;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getKernelRunTime()
     */
    @Override
    public double getKernelRunTime()
    {
        return kernelTime;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getDecisionCyclesRunFor()
     */
    @Override
    public int getDecisionCyclesRunFor()
    {
        return decisionsRunFor;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getMemoryForRun()
     */
    @Override
    public long getMemoryForRun()
    {
        return memoryForRun;
    }

    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getDisplayName()
     */
    @Override
    public String getDisplayName()
    {
        return "JSoar";
    }
    
    /* (non-Javadoc)
     * @see org.jsoar.performancetesting.Test#getTestSettings()
     */
    @Override
    public TestSettings getTestSettings()
    {
        return settings;
    }
}

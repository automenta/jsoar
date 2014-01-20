/*
 * Copyright (c) 2009 Dave Ray <daveray@gmail.com>
 */
package org.jsoar.kernel.learning.rl;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jsoar.kernel.SoarException;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.ApoptosisChoices;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.ChunkStop;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.DecayMode;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.HrlDiscount;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.Learning;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.LearningPolicy;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.Meta;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.TemporalDiscount;
import org.jsoar.kernel.learning.rl.ReinforcementLearningParams.Trace;
import org.jsoar.util.adaptables.Adaptable;
import org.jsoar.util.adaptables.Adaptables;
import org.jsoar.util.commands.SoarCommand;
import org.jsoar.util.commands.SoarCommandContext;
import org.jsoar.util.commands.SoarCommandInterpreter;
import org.jsoar.util.commands.SoarCommandProvider;
import org.jsoar.util.properties.PropertyKey;
import org.jsoar.util.properties.PropertyManager;

/**
 * Implementation of the "rl" command.
 * 
 * @author ray
 */
public final class ReinforcementLearningCommand implements SoarCommand
{
    private final ReinforcementLearning rl;

    public static class Provider implements SoarCommandProvider
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.jsoar.util.commands.SoarCommandProvider#registerCommands(org.
         * jsoar.util.commands.SoarCommandInterpreter)
         */
        @Override
        public void registerCommands(SoarCommandInterpreter interp, Adaptable context)
        {
            interp.addCommand("rl", new ReinforcementLearningCommand(context));
        }
    }

    public ReinforcementLearningCommand(Adaptable context)
    {
        this.rl = Adaptables.require(getClass(), context, ReinforcementLearning.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.util.commands.SoarCommand#execute(org.jsoar.util.commands.
     * SoarCommandContext, java.lang.String[])
     */
    @Override
    public String execute(SoarCommandContext context, String[] args) throws SoarException
    {
        if (args.length == 1)
        {
            return doRl();
        }

        final String arg = args[1];
        if ("-s".equals(arg) || "--set".equals(arg))
        {
            return doSet(1, args);
        }
        else if("-g".equals(arg) || "--get".equals(arg))
        {
            return doGet(1, args);
        }
        else if (arg.startsWith("-"))
        {
            throw new SoarException("Unknown option " + arg);
        }
        else
        {
            throw new SoarException("Unknown argument " + arg);
        }
    }
    
    private String doSet(int i, String[] args) throws SoarException
    {
        if (i + 2 >= args.length)
        {
            throw new SoarException("Invalid arguments for " + args[i] + " option");
        }
        final String name = args[i + 1];
        final String value = args[i + 2];
        final PropertyManager props = rl.getParams().getProperties();
        
        try
        {
            if (name.equals("learning"))
            {
                props.set(ReinforcementLearningParams.LEARNING, Learning.valueOf(value));
            }
            else if (name.equals("discount-rate"))
            {
                props.set(ReinforcementLearningParams.DISCOUNT_RATE, Double.parseDouble(value));
                return "Set discount-rate to " + Double.parseDouble(value);
            }
            else if (name.equals("learning-policy"))
            {
                props.set(ReinforcementLearningParams.LEARNING_POLICY, LearningPolicy.valueOf(value));
                return "Set learning-policy to " + LearningPolicy.valueOf(value);
            }
            else if (name.equals("learning-rate"))
            {
                props.set(ReinforcementLearningParams.LEARNING_RATE, Double.parseDouble(value));
                return "Set learning-rate to " + Double.parseDouble(value);
            }
            else if (name.equals("hrl-discount"))
            {
                props.set(ReinforcementLearningParams.HRL_DISCOUNT, HrlDiscount.valueOf(value));
                return "Set hrl-discount to " + HrlDiscount.valueOf(value);
            }
            else if (name.equals("temporal-discount"))
            {
                props.set(ReinforcementLearningParams.TEMPORAL_DISCOUNT, TemporalDiscount.valueOf(value));
                return "Set temporal-discount to " + TemporalDiscount.valueOf(value);
            }
            else if (name.equals("chunk-stop"))
            {
                props.set(ReinforcementLearningParams.CHUNK_STOP, ChunkStop.valueOf(value));
                return "Set chunk-stop to " + ChunkStop.valueOf(value);
            }
            else if (name.equals("decay-mode"))
            {
                props.set(ReinforcementLearningParams.DECAY_MODE, DecayMode.valueOf(value));
                return "Set decay-mode to " + DecayMode.valueOf(value); 
            }
            else if (name.equals("meta"))
            {
                props.set(ReinforcementLearningParams.META, Meta.valueOf(value));
                return "Set meta to " + Meta.valueOf(value);
            }
            else if (name.equals("meta-learning-rate"))
            {
                props.set(ReinforcementLearningParams.META_LEARNING_RATE, Double.parseDouble(value));
                return "Set meta-learning-rate to " + Double.parseDouble(value);
            }
            else if (name.equals("update-log-path"))
            {
                props.set(ReinforcementLearningParams.UPDATE_LOG_PATH, value);
                return "Set update-log-path to " + value;
            }
            else if (name.equals("apoptosis"))
            {
                props.set(ReinforcementLearningParams.APOPTOSIS, ApoptosisChoices.getEnum(value));
                return "Set apoptosis to " + ApoptosisChoices.getEnum(value); 
            }
            else if (name.equals("apoptosis-decay"))
            {
                props.set(ReinforcementLearningParams.APOPTOSIS_DECAY, Double.parseDouble(value));
                return "Set apoptosis-decay to " + Double.parseDouble(value);
            }
            else if (name.equals("apoptosis-thresh"))
            {
                props.set(ReinforcementLearningParams.APOPTOSIS_THRESH, Double.parseDouble(value));
                return "Set apoptosis-thresh to " + Double.parseDouble(value);
            }
            else if (name.equals("trace"))
            {
                props.set(ReinforcementLearningParams.TRACE, Trace.valueOf(value));
                return "Set trace to " + Trace.valueOf(value);
            }
            else
            {
                throw new SoarException("Unknown rl parameter '" + name + "'");
            }
        }
        catch(IllegalArgumentException e) // this is thrown by the enums if a bad value is passed in
        {
            throw new SoarException("Invalid value.");
        }

        return "";
    }
    
    private String doGet(int i, String[] args) throws SoarException
    {
        if(i + 1 >= args.length)
        {
            throw new SoarException("Invalid arguments for " + args[i] + " option");
        }
        final String name = args[i+1];
        final PropertyKey<?> key = ReinforcementLearningParams.getProperty(rl.getParams().getProperties(), name);
        if(key == null)
        {
            throw new SoarException("Unknown parameter '" + name + "'");
        }
        return rl.getParams().getProperties().get(key).toString();
    }

    private String doRl()
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        final ReinforcementLearningParams p = rl.getParams();
        pw.printf(RLPrintHelper.generateHeader("", 0));
        pw.printf(RLPrintHelper.generateItem("Soar-RL learning:", p.learning.get(), 40));
        pw.printf(RLPrintHelper.generateItem("temporal-extension:", p.temporal_extension.get(), 40));
        pw.printf(RLPrintHelper.generateSection("Discount", 40));
        pw.printf(RLPrintHelper.generateItem("discount-rate:", p.discount_rate.get(), 40));
        pw.printf(RLPrintHelper.generateSection("Learning", 40));
        pw.printf(RLPrintHelper.generateItem("learning-policy:", p.learning_policy.get(), 40));
        pw.printf(RLPrintHelper.generateItem("learning-rate:", p.learning_rate.get(), 40));
        pw.printf(RLPrintHelper.generateItem("hrl-discount:", p.hrl_discount.get(), 40));
//		This is commented out here since the CSoar report does not show it,
//      even though it is there in CSoar as well.
//        pw.printf(RLPrintHelper.generateItem("temporal-discount:", p.temporal_discount.get(), 40));
        pw.printf(RLPrintHelper.generateSection("Eligibility Traces", 40));
        pw.printf(RLPrintHelper.generateItem("eligibility-trace-decay-rate:", p.et_decay_rate.get(), 40));
        pw.printf(RLPrintHelper.generateItem("eligibility-trace-tolerance:", p.et_tolerance.get(), 40));
        pw.printf(RLPrintHelper.generateSection("Experimental", 40));
        pw.printf(RLPrintHelper.generateItem("chunk-stop:", p.chunk_stop.get(), 40));
        pw.printf(RLPrintHelper.generateItem("decay-mode:", p.decay_mode.get(), 40));
        pw.printf(RLPrintHelper.generateItem("meta:", p.meta.get(), 40));
        pw.printf(RLPrintHelper.generateItem("meta-learning-rate:", p.meta_learning_rate.get(), 40));
        pw.printf(RLPrintHelper.generateItem("update-log-path:", p.update_log_path.get(), 40));
        pw.printf(RLPrintHelper.generateItem("", "0", 0));
        
        //	The following are not implemented yet, except for being faked here
        pw.printf(RLPrintHelper.generateItem("apoptosis:", p.apoptosis.get(), 40));
        pw.printf(RLPrintHelper.generateItem("apoptosis-decay:", p.apoptosis_decay.get(), 40));
        pw.printf(RLPrintHelper.generateItem("apoptosis-thresh:", p.apoptosis_thresh.get(), 40));
        pw.printf(RLPrintHelper.generateItem("", "0", 0));
        pw.printf(RLPrintHelper.generateItem("trace:", p.trace.get(), 40));
        pw.printf(RLPrintHelper.generateItem("", "0", 0));

        
        pw.flush();
        return sw.toString();
    }

}
/*
 * Copyright (c) 2009 Dave Ray <daveray@gmail.com>
 */
package org.jsoar.kernel.commands;

import org.jsoar.kernel.SoarException;
import org.jsoar.util.commands.SoarCommand;
import org.jsoar.util.commands.SoarCommandContext;

/**
 * Implementation of the "default-wme-depth" command.
 * 
 * @author ray
 */
public final class DefaultWmeDepthCommand implements SoarCommand
{
    private final PrintCommand printCommand;

    public DefaultWmeDepthCommand(PrintCommand printCommand)
    {
        this.printCommand = printCommand;
    }

    @Override
    public String execute(SoarCommandContext commandContext, String[] args) throws SoarException
    {
        if(args.length == 1)
        {
            return Integer.toString(printCommand.getDefaultDepth());
        }
        else if(args.length == 2)
        {
            try
            {
                int depth = Integer.valueOf(args[1]);
                printCommand.setDefaultDepth(depth);
                return Integer.toString(printCommand.getDefaultDepth());
            }
            catch(NumberFormatException e)
            {
                throw new SoarException(args[1] + " is not a valid number");
            }
        }
        else
        {
            // TODO illegal args
            throw new SoarException(String.format("%s <level>", args[0]));
        }
    }
}
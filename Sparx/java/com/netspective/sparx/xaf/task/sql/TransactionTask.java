/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: TransactionTask.java,v 1.4 2002-08-17 15:11:24 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.task.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xaf.task.BasicTask;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.task.TaskInitializeException;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class TransactionTask extends BasicTask
{
    static public final int COMMAND_UNKNOWN = 0;
    static public final int COMMAND_BEGIN = 1;
    static public final int COMMAND_END = 2;
    public static final int COMMAND_ROLLBACK = 3;

    private SingleValueSource dataSourceValueSource;
    private int command;

    public TransactionTask()
    {
        super();
    }

    /**
     *
     */
    public void reset()
    {
        super.reset();
        command = COMMAND_UNKNOWN;
        dataSourceValueSource = null;
    }

    public SingleValueSource getDataSource()
    {
        return dataSourceValueSource;
    }

    public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public void setCommand(int value)
    {
        command = value;
    }

    public void setCommand(String value)
    {
        if("begin".equals(value))
            setCommand(COMMAND_BEGIN);
        else if("end".equals(value))
            setCommand(COMMAND_END);
        else
            setCommand(COMMAND_UNKNOWN);
    }

    public int getCommand()
    {
        return command;
    }

    /**
     *
     */
    public void initialize(Element elem) throws TaskInitializeException
    {
        super.initialize(elem);
        setDataSource(elem.getAttribute("data-src"));
        setCommand(elem.getAttribute("command"));
    }

    /**
     * Executes the task representing the <xaf:transaction> tag
     * @param tc TaskContext
     */
    public void execute(TaskContext tc) throws TaskExecuteException
    {
        tc.registerTaskExecutionBegin(this);

        try
        {
            DatabaseContext dbContext = DatabaseContextFactory.getContext(tc.getRequest(), tc.getServletContext());

            /* get the appropriate datasource and translate it first so that if it's value source we'll get a static string */
            String dataSourceId = this.getDataSource() != null ? this.getDataSource().getValue(tc) : null;
            dataSourceId = dbContext.translateDataSourceId(tc, dataSourceId);

            int command = this.getCommand();
            switch(command)
            {
                case COMMAND_BEGIN:
                    dbContext.beginConnectionSharing(tc, dataSourceId);
                    break;

                case COMMAND_END:
                    dbContext.endConnectionSharing(tc, dataSourceId, true);
                    break;

                case COMMAND_ROLLBACK:
                    dbContext.endConnectionSharing(tc, dataSourceId, false);
                    break;

                default:
                    throw new TaskExecuteException("No appropriate Transaction command provided ("+ command +").");
            }
        }
        catch(Exception e)
        {
            throw new TaskExecuteException(e);
        }

        tc.registerTaskExecutionEnd(this);
    }
}

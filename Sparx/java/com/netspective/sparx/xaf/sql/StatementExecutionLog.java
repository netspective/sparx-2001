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
 * $Id: StatementExecutionLog.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.util.value.ValueContext;

public class StatementExecutionLog extends ArrayList
{
    public final class StatementExecutionStatistics
    {
        public int resetAfterCount;
        public int totalExecutions;
        public int totalFailed;

        public long averageTotalExecTime;
        public long maxTotalExecTime;
        public long sumTotalExecTime;

        public long averageConnectionEstablishTime;
        public long maxConnectionEstablishTime;
        public long sumConnectionEstablishTime;

        public long averageBindParamsTime;
        public long maxBindParamsTime;
        public long sumBindParamsTime;

        public long averageSqlExecTime;
        public long maxSqlExecTime;
        public long sumSqlExecTime;

        public StatementExecutionStatistics()
        {
        }
    }

    /* resetLogAfterCount
	     value -1 means unknown (find out at first call)
		 value 0 means never reset
		 value > 0 means reset after this many entries
	*/
    private int resetLogAfterCount = -1;

    public StatementExecutionLog()
    {
    }

    public int getResetLogAfterCount()
    {
        return resetLogAfterCount;
    }

    public StatementExecutionLogEntry createNewEntry(ValueContext vc, StatementInfo si)
    {
        if(resetLogAfterCount == -1)
        {
            Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
            if(appConfig == null)
                throw new RuntimeException("Unable to obtain default configuration");
            Collection logs = appConfig.getValues(vc, "com.netspective.sparx.xaf.sql.StatementManager.ExecutionLog.ResetCount." + si.getId());
            if(logs == null)
                logs = appConfig.getValues(vc, "com.netspective.sparx.xaf.sql.StatementManager.ExecutionLog.ResetCount");
            if(logs == null)
                resetLogAfterCount = 0;
            else
            {
                String envName = ConfigurationManagerFactory.getExecutionEvironmentName(vc.getServletContext());
                for(Iterator i = logs.iterator(); i.hasNext();)
                {
                    Object entry = i.next();
                    if(entry instanceof Property)
                    {
                        Property logProperty = (Property) entry;
                        String logEnvName = logProperty.getName();

                        if(envName.equalsIgnoreCase(logEnvName))
                        {
                            String resetCountStr = appConfig.getValue(vc, logProperty, null);
                            try
                            {
                                int resetCount = Integer.parseInt(resetCountStr);
                                resetLogAfterCount = resetCount;
                            }
                            catch(Exception e)
                            {
                                resetLogAfterCount = 0;
                            }
                        }
                    }
                }

                // if no value specified, then default to no reset
                if(resetLogAfterCount == -1)
                    resetLogAfterCount = 0;
            }
        }

        if(resetLogAfterCount > 0 && size() >= resetLogAfterCount)
            clear();

        StatementExecutionLogEntry result = new StatementExecutionLogEntry(vc, si);
        add(result);
        return result;
    }

    public StatementExecutionStatistics getStatistics()
    {
        StatementExecutionStatistics stats = new StatementExecutionStatistics();

        int items = size();
        int failed = 0;
        int successful = 0;

        for(int i = 0; i < items; i++)
        {
            StatementExecutionLogEntry entry = (StatementExecutionLogEntry) get(i);
            if(!entry.wasSuccessful())
            {
                failed++;
                continue;
            }

            long totalTime = entry.getTotalExecutionTime();
            stats.sumTotalExecTime += totalTime;
            if(totalTime > stats.maxTotalExecTime)
                stats.maxTotalExecTime = totalTime;

            long connTime = entry.getConnectionEstablishTime();
            stats.sumConnectionEstablishTime += connTime;
            if(connTime > stats.maxConnectionEstablishTime)
                stats.maxConnectionEstablishTime = connTime;

            long bindParamsTime = entry.getBindParamsBindTime();
            stats.sumBindParamsTime += bindParamsTime;
            if(bindParamsTime > stats.maxBindParamsTime)
                stats.maxBindParamsTime = bindParamsTime;

            long sqlExecTime = entry.getSqlExecTime();
            stats.sumSqlExecTime += sqlExecTime;
            if(sqlExecTime > stats.maxSqlExecTime)
                stats.maxSqlExecTime = sqlExecTime;

            successful++;
        }

        stats.resetAfterCount = resetLogAfterCount;
        stats.totalExecutions = items;
        stats.totalFailed = failed;

        if(successful == 0)
            return stats;

        stats.averageTotalExecTime = stats.sumTotalExecTime / successful;
        stats.averageConnectionEstablishTime = stats.sumConnectionEstablishTime / successful;
        stats.averageBindParamsTime = stats.sumBindParamsTime / successful;
        stats.averageSqlExecTime = stats.sumSqlExecTime / successful;

        return stats;
    }
}
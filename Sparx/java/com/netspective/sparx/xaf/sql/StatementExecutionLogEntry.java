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
 * $Id: StatementExecutionLogEntry.java,v 1.2 2002-08-18 21:08:06 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.netspective.sparx.util.log.AppServerLogger;
import com.netspective.sparx.util.log.LogManager;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;

public class StatementExecutionLogEntry
{
    private boolean successful;
    private String source;
    private String statementName;
    private Date initDate;
    private Date getConnStartDate;
    private Date getConnEndDate;
    private Date bindParamsStartDate;
    private Date bindParamsEndDate;
    private Date execSqlStartDate;
    private Date execSqlEndDate;

    public StatementExecutionLogEntry(ValueContext vc, StatementInfo si)
    {
        ServletRequest req = vc.getRequest();
        if(req instanceof HttpServletRequest)
        {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            source = httpReq.getRequestURI();
            String qs = httpReq.getQueryString();
            if(qs != null) source += "?" + qs;
        }

        statementName = si.getId();
        initDate = new Date();

        AppServerLogger logger = (AppServerLogger) AppServerLogger.getLogger(LogManager.DEBUG_SQL);
        if(logger.isDebugEnabled())
        {
            logger.debug(statementName + LogManager.MONITOR_ENTRY_FIELD_SEPARATOR + source + LogManager.MONITOR_ENTRY_FIELD_SEPARATOR + si.getSql(vc));
            StatementParameter[] params = si.getParams();
            if(params != null)
            {
                for(int i = 1; i <= params.length; i++)
                {
                    StatementParameter param = params[i - 1];
                    if(param.isListType())
                    {
                        String[] values = param.getListSource().getValues(vc);
                        if(values != null)
                        {
                            for(int v = 0; v < values.length; v++)
                                logger.debug("Bind " + statementName + " [" + i + "][" + v + "] {string}: " + values[v] + " (list)");
                        }
                        else
                        {
                            logger.debug("Bind " + statementName + " [" + i + "]: NULL (list)");
                        }
                    }
                    else
                    {
                        String type = StatementManager.getTypeNameForId(param.getParamType());
                        SingleValueSource vs = param.getValueSource();
                        logger.debug("Bind " + statementName + " [" + i + "] {" + vs.getId() + "}: " + vs.getValue(vc) + " (" + type + ")");
                    }
                }
            }
        }
    }

    public String getSource()
    {
        return source;
    }

    public boolean wasSuccessful()
    {
        return successful;
    }

    public void registerGetConnectionBegin()
    {
        getConnStartDate = new Date();
    }

    public void registerGetConnectionEnd(java.sql.Connection conn)
    {
        getConnEndDate = new Date();
    }

    public void registerBindParamsBegin()
    {
        bindParamsStartDate = new Date();
    }

    public void registerBindParamsEnd()
    {
        bindParamsEndDate = new Date();
    }

    public void registerExecSqlBegin()
    {
        execSqlStartDate = new Date();
    }

    public void registerExecSqlEndSuccess()
    {
        execSqlEndDate = new Date();
        successful = true;
    }

    public void registerExecSqlEndFailed()
    {
        execSqlEndDate = new Date();
    }

    public void finalize(ValueContext vc)
    {
        AppServerLogger cat = (AppServerLogger) AppServerLogger.getLogger(LogManager.MONITOR_SQL);
        if(!cat.isInfoEnabled())
            return;

        StringBuffer info = new StringBuffer();
        info.append(statementName);
        info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
        info.append(successful ? 1 : 0);
        info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
        if(successful)
        {
            info.append(getConnEndDate.getTime() - getConnStartDate.getTime());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(bindParamsEndDate.getTime() - bindParamsStartDate.getTime());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(execSqlEndDate.getTime() - execSqlStartDate.getTime());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(execSqlEndDate.getTime() - initDate.getTime());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
        }
        else
        {
            info.append(-1);
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(-1);
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(-1);
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(-1);
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
        }
        info.append(source);

        cat.info(info.toString());
    }

    public Date getInitDate()
    {
        return initDate;
    }

    public long getTotalExecutionTime()
    {
        return execSqlEndDate.getTime() - initDate.getTime();
    }

    public long getConnectionEstablishTime()
    {
        return getConnEndDate.getTime() - getConnStartDate.getTime();
    }

    public long getBindParamsBindTime()
    {
        return bindParamsEndDate.getTime() - bindParamsStartDate.getTime();
    }

    public long getSqlExecTime()
    {
        return execSqlEndDate.getTime() - execSqlStartDate.getTime();
    }
}
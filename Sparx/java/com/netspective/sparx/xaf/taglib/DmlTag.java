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
 * $Id: DmlTag.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.taglib;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.netspective.sparx.xaf.task.Task;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;

public class DmlTag extends javax.servlet.jsp.tagext.TagSupport
{
    private com.netspective.sparx.xaf.task.sql.DmlTask task = new com.netspective.sparx.xaf.task.sql.DmlTask();

    public void release()
    {
        super.release();
        task.reset();
    }

    public void setDebug(String value)
    {
        if(value.equals("yes")) task.setFlag(com.netspective.sparx.xaf.task.Task.TASKFLAG_DEBUG);
    }

    public void setDataCmd(String value)
    {
        task.setDataCmdCondition(value);
    }

    public void setCommand(String value)
    {
        task.setCommand(value);
    }

    public void setTable(String value)
    {
        task.setTable(value);
    }

    public void setAutoInc(String value)
    {
        task.setAutoInc(value);
    }

    public void setAutoIncStore(String value)
    {
        task.setAutoIncStore(value);
    }

    public void setDataSource(String value)
    {
        task.setDataSource(value);
    }

    public void setFields(String value)
    {
        task.setFields(value);
    }

    public void setWhere(String value)
    {
        task.setWhereCond(value);
    }

    public void setWhereBind(String value)
    {
        task.setWhereCondBindParams(value);
    }

    public void setColumns(String value)
    {
        task.setColumns(value);
    }

    public void setContext(String value)
    {
        task.setDialogContextAttrName(value);
    }

    public void setInsertchk(String value)
    {
        task.setInsertCheckValueSource(value);
    }

    public void setUpdatechk(String value)
    {
        task.setUpdateCheckValueSource(value);
    }

    public int doStartTag() throws javax.servlet.jsp.JspException
    {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws javax.servlet.jsp.JspException
    {
        javax.servlet.jsp.JspWriter out = pageContext.getOut();
        com.netspective.sparx.xaf.task.TaskContext tc = new com.netspective.sparx.xaf.task.TaskContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());
        try
        {
            task.execute(tc);
            if(tc.hasError())
                out.write(tc.getErrorMessage());
            else if(tc.hasResultMessage())
                out.write(tc.getResultMessage());
        }
        catch(java.io.IOException e)
        {
            throw new javax.servlet.jsp.JspException(e.toString());
        }
        catch(com.netspective.sparx.xaf.task.TaskExecuteException e)
        {
            try
            {
                out.write(e.getDetailedMessage());
            }
            catch(java.io.IOException ie)
            {
                throw new javax.servlet.jsp.JspException(ie.getMessage());
            }
            return SKIP_PAGE;
        }

        return EVAL_PAGE;
    }

}
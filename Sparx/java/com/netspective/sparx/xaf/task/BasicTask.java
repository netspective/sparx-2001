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
 * $Id: BasicTask.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.util.value.ValueContext;

public class BasicTask implements Task
{
    private static long taskNum = 0;
    private long flags;
    private int dataCmdCondition = DialogContext.DATA_CMD_NONE;
    private String taskName;
    private List initErrors;
    private List children;

    public BasicTask()
    {
        taskNum++;
    }

    public void reset()
    {
        flags = 0;
        dataCmdCondition = 0;
        children = null;
        taskName = null;
        initErrors = null;
    }

    public long getTaskNum()
    {
        return taskNum;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public List getChildren()
    {
        return children;
    }

    public int getChildrenCount()
    {
        return children != null ? children.size() : 0;
    }

    public void addChildTask(Task task)
    {
        if(children == null)
            children = new ArrayList();
        children.add(task);
    }

    public final long getFlags()
    {
        return flags;
    }

    public final boolean flagIsSet(long flag)
    {
        return (flags & flag) == 0 ? false : true;
    }

    public final void setFlag(long flag)
    {
        flags |= flag;
        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                ((Task) i.next()).setFlag(flag);
            }
        }
    }

    public final void clearFlag(long flag)
    {
        flags &= ~flag;
        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                ((Task) i.next()).clearFlag(flag);
            }
        }
    }

    public int getDataCmdCondition()
    {
        return dataCmdCondition;
    }

    public void setDataCmdCondition(int condition)
    {
        dataCmdCondition = condition;
    }

    public void setDataCmdCondition(String condition)
    {
        dataCmdCondition = DialogContext.getDataCmdIdForCmdText(condition);
    }

    public boolean isDataCmdConditionTrue(TaskContext tc)
    {
        DialogContext dc = tc.getDialogContext();
        if(dc == null || dataCmdCondition == DialogContext.DATA_CMD_NONE)
            return true;

        return dc.matchesDataCmdCondition(dataCmdCondition);
    }

    public void addInitError(String message)
    {
        if(initErrors == null)
            initErrors = new ArrayList();
        initErrors.add(message);
    }

    public boolean isValid()
    {
        return initErrors == null;
    }

    public List getInitErrors()
    {
        return initErrors;
    }

    public void initialize(Element elem) throws TaskInitializeException
    {
        taskName = elem.getNodeName();

        if(elem.getAttribute("debug").equals("yes"))
            setFlag(TASKFLAG_DEBUG);

        String dataCmd = elem.getAttribute("data-cmd");
        if(dataCmd.length() > 0)
            setDataCmdCondition(dataCmd);

        NodeList childNodes = elem.getChildNodes();
        for(int c = 0; c < childNodes.getLength(); c++)
        {
            Node childNode = childNodes.item(c);
            if(childNode.getNodeType() == Node.ELEMENT_NODE)
            {
                try
                {
                    Task task = TaskFactory.getTask((Element) childNode, true);
                    addChildTask(task);
                }
                catch(Exception e)
                {
                    addInitError(e.toString());
                }
            }
        }
    }

    public boolean allowExecute(TaskContext tc)
    {
        return isDataCmdConditionTrue(tc);
    }

    public void executeChildren(TaskContext tc) throws TaskExecuteException
    {
        if(children == null || children.size() == 0)
            return;

        for(Iterator i = children.iterator(); i.hasNext();)
        {
            Task task = (Task) i.next();
            if(task.allowExecute(tc))
                task.execute(tc);
            if(tc.haltProcessing())
                break;
        }
    }

    public void execute(TaskContext tc) throws TaskExecuteException
    {
        tc.registerTaskExecutionBegin(this);
        executeChildren(tc);
        tc.registerTaskExecutionEnd(this);
    }

    public String getDebugHtml(ValueContext vc)
    {
        StringBuffer html = new StringBuffer();
        html.append("Task '" + getTaskName() + "' (" + getClass().getName() + ") [" + DialogContext.getDataCmdTextForCmdId(dataCmdCondition) + "]");

        if(children != null)
        {
            html.append("<ol>");
            for(int i = 0; i < children.size(); i++)
            {
                Task child = (Task) children.get(i);
                html.append("<li>");
                html.append(child.getDebugHtml(vc));
                html.append("</li>");
            }
            html.append("</ol>");
        }

        return html.toString();
    }
}
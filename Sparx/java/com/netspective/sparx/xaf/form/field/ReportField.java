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
 * $Id: ReportField.java,v 1.2 2002-11-03 23:26:42 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.task.TaskInitializeException;
import com.netspective.sparx.xaf.task.sql.StatementTask;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.log.LogManager;

public class ReportField extends DialogField
{
    static public final int SELECTSTYLE_RADIO = 0;
    static public final int SELECTSTYLE_MULTICHECK = 1;

    private int style;
    private ListValueSource defaultValue;
    private StatementTask task;
    private Throwable taskException;

    public ReportField()
    {
        super();
        style = SELECTSTYLE_MULTICHECK;
    }

    public ReportField(String aName, String aCaption, int aStyle)
    {
        super(aName, aCaption);
        style = aStyle;
    }

    public final boolean isMulti()
    {
        return style == SELECTSTYLE_MULTICHECK;
    }

    public final int getStyle()
    {
        return style;
    }

    public void setStyle(int value)
    {
        style = value;
    }

    public StatementTask getTask()
    {
        return task;
    }

    public void setReport(StatementTask task)
    {
        this.task = task;
    }

    public boolean defaultIsListValueSource()
    {
        return true;
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String styleValue = elem.getAttribute("style");
        if(styleValue.length() > 0)
        {
            if(styleValue.equalsIgnoreCase("radio"))
                style = SelectField.SELECTSTYLE_RADIO;
            else if(styleValue.equalsIgnoreCase("multicheck"))
                style = SelectField.SELECTSTYLE_MULTICHECK;
            else
                style = SelectField.SELECTSTYLE_RADIO;
        }

        String defaultv = elem.getAttribute("default");
        if(defaultv.length() > 0)
        {
            if(isMulti())
                defaultValue = ValueSourceFactory.getListValueSource(defaultv);
            else
                super.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(defaultv));
        }
        else
            defaultValue = null;

        NodeList taskElems = elem.getElementsByTagName("statement");
        if(taskElems.getLength() > 0)
        {
            try
            {
                task = new StatementTask();
                task.initialize((Element) taskElems.item(0));
            }
            catch(TaskInitializeException e)
            {
                taskException = e;
                LogManager.recordException(this.getClass(), "importFromXml", "unable to initialize task", e);
            }
        }
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        boolean readOnly = isReadOnly(dc);
        String id = getId();
        String defaultControlAttrs = dc.getSkin().getDefaultControlAttrs();

        if(task == null)
        {
            writer.write(taskException == null ?
                    "No StatementTask is available." :
                    taskException.getMessage());
            return;
        }

        TaskContext tc = new TaskContext(dc);
        tc.setCanvas(this);
        try
        {
            task.execute(tc);
        }
        catch(TaskExecuteException e)
        {
            writer.write(e.toString());
            LogManager.recordException(this.getClass(), "renderControlHtml", "unable to execute task", e);
            return;
        }
        writer.write(tc.hasError() ? tc.getErrorMessage() : tc.getResultMessage());
    }

}
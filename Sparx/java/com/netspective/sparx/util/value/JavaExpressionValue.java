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
 * $Id: JavaExpressionValue.java,v 1.1 2002-11-03 23:25:25 shahid.shah Exp $
 */

package com.netspective.sparx.util.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlHelper;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.util.log.LogManager;

public class JavaExpressionValue extends ValueSource implements ListValueSource
{
    private Expression expression;
    private String exprError;

    public JavaExpressionValue()
    {
        super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
                "Evaluates the given expression using the Jakarta Commons Jexl package. The 'vc' variable is made " +
                "accessible in the expression context. 'vc' is the instance of the ValueContext class sent into the " +
                "value source at the time it is evaluated. If the ValueContext is an instance of DialogContext, then " +
                "an alias of 'vc' named 'dc' (for DialogContext) is also available. If the ValueContext is an instance" +
                "of ReportContext, then an alias of 'vc' named 'rc' (for ReportContext) is also available.",
                new String[]{"jexl-expr"}
        );
    }

    public void initializeSource(String srcParams)
    {
        try
        {
            expression = ExpressionFactory.createExpression(srcParams);
        }
        catch (Exception e)
        {
            expression = null;
            exprError = e.toString();
            LogManager.recordException(this.getClass(), "initializeSource", "Could not create expression '"+ srcParams +"'", e);
        }
    }

    public String getValue(ValueContext vc)
    {
        Object value = getObjectValue(vc);
        if(value != null)
            return value.toString();
        else
            return null;
    }

    public Object getObjectValue(ValueContext vc)
    {
        if(expression == null)
            return "Expression is invalid: " + exprError;

        try
        {
            JexlContext jexlContext = JexlHelper.createContext();
            Map vars = new HashMap();
            vars.put("vc", vc);
            if(vc instanceof DialogContext)
                vars.put("dc", vc);
            else if(vc instanceof ReportContext)
                vars.put("rc", vc);
            jexlContext.setVars(vars);
            return expression.evaluate(jexlContext);
        }
        catch(Exception e)
        {
            LogManager.recordException(this.getClass(), "initializeSource", "Could not evaluate expression '"+ expression.getExpression() +"'", e);
            return "Unable to evaluate expression '"+ expression.getExpression() +"': " + e.toString();
        }
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        Object o = getObjectValue(vc);
        if(o instanceof SelectChoicesList)
            return (SelectChoicesList) o;

        SelectChoicesList choices = new SelectChoicesList();
        if(o != null)
        {
            if(o instanceof Map)
            {
                Map map = (Map) o;
                for(Iterator i = map.entrySet().iterator(); i.hasNext();)
                {
                    Map.Entry entry = (Map.Entry) i.next();
                    choices.add(new SelectChoice(entry.getKey().toString(), entry.getValue().toString()));
                }
            }
            else if(o instanceof String[])
            {
                String[] values = (String[]) o;
                for(int i = 0; i < values.length; i++)
                    choices.add(new SelectChoice(values[i]));
            }
            else if(o instanceof String)
            {
                choices.add(new SelectChoice(o.toString()));
            }
        }
        return choices;

    }

    public String[] getValues(ValueContext vc)
    {
        Object o = getObjectValue(vc);
        if(o != null)
        {
            if(o instanceof String[])
                return (String[]) o;
            else if(o instanceof String)
                return new String[] { o.toString() };
        }
        return null;
    }
}
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
 * $Id: ComponentCommandFactory.java,v 1.6 2003-04-07 15:27:33 aye.thu Exp $
 */

package com.netspective.sparx.xaf.html;

import com.netspective.sparx.xaf.html.command.*;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class ComponentCommandFactory
{
    /**
     * Map that contains the various URL commands allowed by Sparx. The key is a text identifier, the value is a
     * ComponentCommand class that is instantiated for each command.
     */
    static private Map commandClasses = new HashMap();

    /**
     * The string used to delimit the command name and the entire parameter string
     */
    static private String CMDNAME_AND_PARAM_DELIM = ",";

    static
    {
        registerCommand(DialogComponentCommand.COMMAND_ID, DialogComponentCommand.class);
        registerCommand(ListValueSourceComponentCommand.COMMAND_ID, ListValueSourceComponentCommand.class);
        registerCommand(StatementComponentCommand.COMMAND_ID, StatementComponentCommand.class);
        registerCommand(RecordUpdateComponentCommand.COMMAND_ID, RecordUpdateComponentCommand.class);
        registerCommand(RecordAddComponentCommand.COMMAND_ID, RecordAddComponentCommand.class);
        registerCommand(RecordDeleteComponentCommand.COMMAND_ID, RecordDeleteComponentCommand.class);
        registerCommand(QueryDefinitionDialogComponentCommand.COMMAND_ID, QueryDefinitionDialogComponentCommand.class);
    }

    static public void registerCommand(String name, Class cls)
    {
        commandClasses.put(name, cls);
    }

    static public ComponentCommand getCommand(String name, String params)
    {
        Class ccClass = (Class) commandClasses.get(name);
        if(ccClass != null)
        {
            try
            {
                ComponentCommand command = (ComponentCommand) ccClass.newInstance();
                if(params != null) command.setCommand(params);
                return command;
            }
            catch (InstantiationException e)
            {
                return null;
            }
            catch (IllegalAccessException e)
            {
                return null;
            }
        }
        else
            return null;
    }

    static public ComponentCommand getCommand(String name, StringTokenizer params)
    {
        Class ccClass = (Class) commandClasses.get(name);
        try
        {
            ComponentCommand command = (ComponentCommand) ccClass.newInstance();
            command.setCommand(params);
            return command;
        }
        catch (InstantiationException e)
        {
            return null;
        }
        catch (IllegalAccessException e)
        {
            return null;
        }
    }

    static public DialogComponentCommand getDialogCommand(String params)
    {
        DialogComponentCommand command = new DialogComponentCommand();
        command.setCommand(params);
        return command;
    }

    static public DialogComponentCommand getDialogCommand(StringTokenizer params)
    {
        DialogComponentCommand command = new DialogComponentCommand();
        command.setCommand(params);
        return command;
    }

    static public StatementComponentCommand getStatementCommand(String params)
    {
        StatementComponentCommand command = new StatementComponentCommand();
        command.setCommand(params);
        return command;
    }

    static public StatementComponentCommand getStatementCommand(StringTokenizer params)
    {
        StatementComponentCommand command = new StatementComponentCommand();
        command.setCommand(params);
        return command;
    }

    static public boolean containsDialogCommand(ValueContext vc)
    {
        String pageCmdReqParamValue = vc.getRequest().getParameter(ComponentCommand.PAGE_COMMAND_REQUEST_PARAM_NAME);
        if(pageCmdReqParamValue == null)
            return false;

        String pageCmd = "unknown";
        String pageCmdParam = null;
        int cmdDelimPos = pageCmdReqParamValue.indexOf(CMDNAME_AND_PARAM_DELIM);
        if(cmdDelimPos != -1)
        {
            pageCmd = pageCmdReqParamValue.substring(0, cmdDelimPos);
            pageCmdParam = pageCmdReqParamValue.substring(cmdDelimPos+1);
        }

        ComponentCommand command = ComponentCommandFactory.getCommand(pageCmd, pageCmdParam);
        return command instanceof DialogComponentCommand;
    }

    public static boolean handleDefaultBodyItem(ServletContext context, Servlet servlet, ServletRequest req, ServletResponse resp) throws ComponentCommandException, IOException
    {
        String pageCmdReqParamValue = req.getParameter(ComponentCommand.PAGE_COMMAND_REQUEST_PARAM_NAME);
        if(pageCmdReqParamValue == null)
            return false;

        String pageCmd = "unknown";
        String pageCmdParam = null;
        int cmdDelimPos = pageCmdReqParamValue.indexOf(CMDNAME_AND_PARAM_DELIM);
        if(cmdDelimPos != -1)
        {
            pageCmd = pageCmdReqParamValue.substring(0, cmdDelimPos);
            pageCmdParam = pageCmdReqParamValue.substring(cmdDelimPos+1);
        }

        ComponentCommand command = ComponentCommandFactory.getCommand(pageCmd, pageCmdParam);
        if(command != null)
        {
            command.handleCommand(new ServletValueContext(context, servlet, req, resp), resp.getWriter(), false);
            return true;
        }
        else
        {
            resp.getWriter().write("Page command '" + pageCmd + "' not recognized.");
            return false;
        }
    }

/**
     * Process the page context for component commands
     * @param pageContext
     * @param writer
     * @return
     * @throws ComponentCommandException
     * @throws IOException
     */
    public static boolean handleDefaultBodyItem(PageContext pageContext, Writer writer) throws ComponentCommandException, IOException
    {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse resp = (HttpServletResponse) pageContext.getResponse();
        if (writer == null)
            writer = new StringWriter();
        String pageCmdReqParamValue = req.getParameter(ComponentCommand.PAGE_COMMAND_REQUEST_PARAM_NAME);
        if(pageCmdReqParamValue == null)
            return false;

        String pageCmd = "unknown";
        String pageCmdParam = null;
        int cmdDelimPos = pageCmdReqParamValue.indexOf(CMDNAME_AND_PARAM_DELIM);
        if(cmdDelimPos != -1)
        {
            pageCmd = pageCmdReqParamValue.substring(0, cmdDelimPos);
            pageCmdParam = pageCmdReqParamValue.substring(cmdDelimPos+1);
        }

        ComponentCommand command = ComponentCommandFactory.getCommand(pageCmd, pageCmdParam);
        if(command != null)
        {
            command.handleCommand(new ServletValueContext(pageContext.getServletContext(),
                    (Servlet) pageContext.getPage(), req, resp), writer, false);
            return true;
        }
        else
        {
            writer.write("Page command '" + pageCmd + "' not recognized.");
            return false;
        }
    }


    public static void createCatalog(Element parent)
    {
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Component Commands");
        factoryElem.setAttribute("class", ComponentCommandFactory.class.getName());
        for(Iterator i = commandClasses.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            String commandId = (String) entry.getKey();
            Class commandClass = (Class) entry.getValue();

            Element childElem = doc.createElement("component-command");
            childElem.setAttribute("name", commandId);
            childElem.setAttribute("class", ((Class) entry.getValue()).getName());
            factoryElem.appendChild(childElem);

            try
            {
                ComponentCommand command = (ComponentCommand) commandClass.newInstance();
                ComponentCommand.Documentation commandDoc = command.getDocumentation();

                if(commandDoc != null)
                {
                    childElem.setAttribute("usage", commandDoc.getUsageHtml(commandId, command.getParametersDelimiter(), true));
                    childElem.setAttribute("params", commandDoc.getParamsHtml(commandId));
                    String descr = commandDoc.getDescription();
                    if(descr != null)
                    {
                        Element commandDescrElem = doc.createElement("descr");
                        commandDescrElem.appendChild(doc.createTextNode(commandDoc.getDescription()));
                        childElem.appendChild(commandDescrElem);
                    }
                }
            }
            catch(Exception e)
            {
                Element commandDescrElem = doc.createElement("descr");
                commandDescrElem.appendChild(doc.createTextNode(e.toString()));
                childElem.appendChild(commandDescrElem);
            }
        }
    }

}

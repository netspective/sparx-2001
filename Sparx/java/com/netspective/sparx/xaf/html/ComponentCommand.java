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
 * $Id: ComponentCommand.java,v 1.2 2003-01-01 19:27:45 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.html;

import com.netspective.sparx.util.value.ValueContext;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public interface ComponentCommand
{
    static public final String PAGE_COMMAND_REQUEST_PARAM_NAME = "cmd";
    static public final String EMPTY_COMMAND_PARAMETER = "-";

    /**
     * Returns the documentation for this command
     */
    public Documentation getDocumentation();

    /**
     * Set the active command for the component
     * @param params the parameters being sent to the command
     */
    public void setCommand(String params);

    /**
     * Set the active command for the component
     * @param params the parameters being sent to the command
     */
    public void setCommand(StringTokenizer params);

    /**
     * Get the text used to delimit the parameters
     * @return parameters delimiter
     */
    public String getParametersDelimiter();

    /**
     * Get the active command for the component
     * @return the parameters set earlier by the set methods
     */
    public String getCommand();

    /**
     * Render the html for this command
     * @param vc the value context
     * @throws IOException
     */
    public void handleCommand(ValueContext vc, Writer writer, boolean unitTest) throws ComponentCommandException, IOException;

    public class Documentation
    {
        public static class Parameter
        {
            private String name;
            private boolean required;
            private String[] enums;
            private String defaultValue;
            private String description;

            public Parameter(String name, boolean required, String[] enums, String defaultValue, String description)
            {
                this.name = name;
                this.required = required;
                this.enums = enums;
                this.defaultValue = defaultValue;
                this.description = description;
            }

            public String getDefaultValue()
            {
                return defaultValue;
            }

            public void setDefaultValue(String defaultValue)
            {
                this.defaultValue = defaultValue;
            }

            public String getName()
            {
                return name;
            }

            public void setName(String name)
            {
                this.name = name;
            }

            public boolean isRequired()
            {
                return required;
            }

            public void setRequired(boolean required)
            {
                this.required = required;
            }

            public String[] getEnums()
            {
                return enums;
            }

            public void setEnums(String[] enums)
            {
                this.enums = enums;
            }

            public String getDescription()
            {
                return description;
            }

            public void setDescription(String description)
            {
                this.description = description;
            }
        }

        private String description;
        private List parameters = new ArrayList();

        public Documentation(String descr, Parameter param)
        {
            this.description = descr;
            addParameter(param);
        }

        public Documentation(String descr, Parameter[] params)
        {
            this.description = descr;
            for(int i = 0; i < params.length; i++)
                addParameter(params[i]);
        }

        public String getUsageHtml(String commandId, String delim, boolean all)
        {
            StringBuffer html = new StringBuffer();
            boolean isFirst = true;
            if(commandId != null)
                html.append(PAGE_COMMAND_REQUEST_PARAM_NAME + "=" + commandId + ",");
            for(int i = 0; i < parameters.size(); i++)
            {
                Parameter param = (Parameter) parameters.get(i);
                if(all || param.isRequired())
                {
                    if(! isFirst) html.append(delim);
                    if(param.isRequired()) html.append("<i>");
                    html.append(param.getName());
                    if(param.isRequired()) html.append("</i>");
                    isFirst = false;
                }
            }
            return html.toString();
        }

        public String getParamsHtml(String commandId)
        {
            StringBuffer html = new StringBuffer();
            html.append("<table class='data_table' cellspacing='0' cellpadding='2' border='0'>");
            html.append("<tr class='data_table_header'><th class='data_table'>Name</th><th class='data_table'>Req</th><th class='data_table'>Default</th><th class='data_table'>Choices</th></tr>");
            for(int i = 0; i < parameters.size(); i++)
            {
                html.append("<tr class='data_table'>");
                Parameter param = (Parameter) parameters.get(i);
                html.append("<td class='data_table' rowspan=2><nobr>" + (param.isRequired() ? "<b>" : "") + param.getName() + "</b></nobr></td><td class='data_table'>"+ (param.isRequired() ? "required" : "optional") + "</td>");
                String defaultValue = param.getDefaultValue();
                html.append("<td class='data_table'>" + (defaultValue != null ? defaultValue : "&nbsp;") + "</td>");
                html.append("<td class='data_table'>");
                String[] enums = param.getEnums();
                if(enums != null)
                {
                    for(int e = 0; e < enums.length; e++)
                    {
                        if(e > 0) html.append(" | ");
                        String enum = enums[e];
                        if(defaultValue != null && enum.equals(defaultValue))
                        {
                            html.append("<u>" + enums[e] + "</u>");
                        }
                        else
                            html.append(enums[e]);
                    }
                }
                else
                    html.append("&nbsp;");
                html.append("</td>");
                html.append("</tr>");
                html.append("<tr><td class='data_table' colspan=3><font color='blue'>"+ param.getDescription() +"</font></td></tr>");
            }
            html.append("</table>");
            return html.toString();
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public List getParameters()
        {
            return parameters;
        }

        public void addParameter(Parameter param)
        {
            parameters.add(param);
        }
    }
}

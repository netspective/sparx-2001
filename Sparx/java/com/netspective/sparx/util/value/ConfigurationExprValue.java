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
 * $Id: ConfigurationExprValue.java,v 1.2 2002-08-25 16:06:16 shahid.shah Exp $
 */

package com.netspective.sparx.util.value;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;

public class ConfigurationExprValue extends ValueSource
{
    private String source;
    private String exprValue;

    public ConfigurationExprValue()
    {
        super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
                "Evaluates the given expression as a property value that would be created had the given expression been " +
                "specified in the default Sparx configuration file (WEB-INF/conf/sparx.xml). If " +
                "no source-name is provided the expr requested is evaluted based on the the default configuration element " +
                "of the default configuration file. If a source-name is provided, then the expr is evaluated based on the " +
                "configuration named source-name in the default configuration file.",
                new String[]{"expr", "source-name/expr"}
        );
    }

    public void initializeSource(String srcParams)
    {
        int delimPos = srcParams.indexOf("//");
        if(delimPos >= 0)
        {
            source = srcParams.substring(0, delimPos);
            valueKey = srcParams.substring(delimPos + 1);
        }
        else
            valueKey = srcParams;
    }

    public String getValue(ValueContext vc)
    {
        if(exprValue != null)
            return exprValue;

        Configuration config = null;
        if(source == null)
        {
            config = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
            if(config == null)
                return "No default Configuration found in " + getId();
        }
        else
        {
            ConfigurationManager manager = ConfigurationManagerFactory.getManager(vc.getServletContext());
            if(manager == null)
                return "No ConfigurationManager found in " + getId();
            config = manager.getConfiguration(source);
            if(config == null)
                return "No '" + source + "' Configuration found in " + getId();
        }

        Configuration.ReplacementInfo ri = config.replaceProperties(vc, valueKey);
        if(ri.isFinal())
        {
            exprValue = ri.result.toString();
            return exprValue;
        }
        else
            return ri.result.toString();
    }
}
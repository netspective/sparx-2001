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
 * $Id: AppServerPatternParser.java,v 1.2 2002-02-09 13:02:12 snshah Exp $
 */

package com.netspective.sparx.util.log;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 *  Extend PatternParser to recognize additional conversion characters
 *  suitable for use by application servers.
 *  <p>
 *  <ul>
 *  <li>s - server name
 *  <li>h - hostname
 *  <li>b - component name (c and C already in use)
 *  <li>v - version name
 *  </ul>
 *  <p>
 *
 *  @see org.apache.log4j.helpers.PatternParser
 *  @see org.apache.log4j.PatternLayout
 *
 *  @author Paul Glezen
 */
public class AppServerPatternParser extends PatternParser
{
    /** Set to 'h'.*/
    protected static final char HOSTNAME_CHAR = 'h';
    /** Set to 's'.*/
    protected static final char SERVER_CHAR = 's';
    /** Set to 'b'.*/
    protected static final char COMPONENT_CHAR = 'b';
    /** Set to 'v'.*/
    protected static final char VERSION_CHAR = 'v';

    /**
     *  Create a parser with the provided pattern.
     *
     *  @param pattern a formatting pattern to parse
     */
    public AppServerPatternParser(String pattern)
    {
        super(pattern);
    }

    /**
     *  Decide, based on the format character, which subtype of
     *  <code>Converter</code> to instanciate.  The converter is
     *  then added to the converter list using the
     *  <code>addConverter</code> superclass method.
     *  If the format character is not recognized, it is passed
     *  to the superclass for interpretation.
     */
    public void finalizeConverter(char formatChar)
    {
        PatternConverter pc = null;
        switch(formatChar)
        {
            case HOSTNAME_CHAR:
                pc = new HostnamePatternConverter(formattingInfo);
                currentLiteral.setLength(0);
                addConverter(pc);
                break;
            case SERVER_CHAR:
                pc = new ServerPatternConverter(formattingInfo);
                currentLiteral.setLength(0);
                addConverter(pc);
                break;
            case COMPONENT_CHAR:
                pc = new ComponentPatternConverter(formattingInfo);
                currentLiteral.setLength(0);
                addConverter(pc);
                break;
            case VERSION_CHAR:
                pc = new VersionPatternConverter(formattingInfo);
                currentLiteral.setLength(0);
                addConverter(pc);
                break;
            default:
                super.finalizeConverter(formatChar);
        }
    }

    /**
     *  The <code>AppServerPatternConverter</code> inner class factors
     *  out all the duties of a converter except
     */
    private static abstract class AppServerPatternConverter extends PatternConverter
    {
        AppServerPatternConverter(FormattingInfo formattingInfo)
        {
            super(formattingInfo);
        }

        public String convert(LoggingEvent event)
        {
            String result = null;
            AppServerLoggingEvent appEvent = null;

            if(event instanceof AppServerLoggingEvent)
            {
                appEvent = (AppServerLoggingEvent) event;
                result = convert(appEvent);
            }
            return result;
        }

        public abstract String convert(AppServerLoggingEvent event);
    }

    private static class HostnamePatternConverter extends AppServerPatternConverter
    {
        HostnamePatternConverter(FormattingInfo formatInfo)
        {
            super(formatInfo);
        }

        public String convert(AppServerLoggingEvent event)
        {
            return event.hostname;
        }
    }

    private static class ServerPatternConverter extends AppServerPatternConverter
    {
        ServerPatternConverter(FormattingInfo formatInfo)
        {
            super(formatInfo);
        }

        public String convert(AppServerLoggingEvent event)
        {
            return event.server;
        }
    }

    private static class ComponentPatternConverter extends AppServerPatternConverter
    {
        ComponentPatternConverter(FormattingInfo formatInfo)
        {
            super(formatInfo);
        }

        public String convert(AppServerLoggingEvent event)
        {
            return event.component;
        }
    }

    private static class VersionPatternConverter extends AppServerPatternConverter
    {
        VersionPatternConverter(FormattingInfo formatInfo)
        {
            super(formatInfo);
        }

        public String convert(AppServerLoggingEvent event)
        {
            return event.version;
        }
    }
}

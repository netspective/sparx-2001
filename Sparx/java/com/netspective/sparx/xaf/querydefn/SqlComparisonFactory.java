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
 * $Id: SqlComparisonFactory.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.xaf.querydefn.comparison.BinaryOpComparison;
import com.netspective.sparx.xaf.querydefn.comparison.ContainsComparison;
import com.netspective.sparx.xaf.querydefn.comparison.ContainsComparisonIgnoreCase;
import com.netspective.sparx.xaf.querydefn.comparison.DateComparison;
import com.netspective.sparx.xaf.querydefn.comparison.EndsWithComparison;
import com.netspective.sparx.xaf.querydefn.comparison.InComparison;
import com.netspective.sparx.xaf.querydefn.comparison.IsDefinedComparison;
import com.netspective.sparx.xaf.querydefn.comparison.StartsWithComparison;

public class SqlComparisonFactory implements Factory
{
    static List comparisonsList = new ArrayList();
    static Map comparisonsMap = new HashMap();

    static
    {
        addComparison(new BinaryOpComparison("equals", "equals", "general", "="), new String[]{"is", "="});
        addComparison(new BinaryOpComparison("not-equals", "does not equal", "general", "!="), new String[]{"is-not", "!="});
        addComparison(new StartsWithComparison(), null);
        addComparison(new ContainsComparison(), null);
        addComparison(new ContainsComparisonIgnoreCase(), null);
        addComparison(new EndsWithComparison(), null);
        addComparison(new InComparison(), null);
        addComparison(new IsDefinedComparison(), null);
        addComparison(new BinaryOpComparison("greater-than", "greater than", "general", ">"), new String[]{"gt", ">"});
        addComparison(new BinaryOpComparison("greater-than-equal", "greater than or equal to", "general", ">="), new String[]{"gte", ">="});
        addComparison(new BinaryOpComparison("less-than", "less than", "general", "<"), new String[]{"lt", "<"});
        addComparison(new BinaryOpComparison("less-than-equal", "less than or equal to", "general", "<="), new String[]{"lte", "<="});
        addComparison(new DateComparison("lte-date", "<="), new String[]{"lte-date", "less-than-equal-date"});
        addComparison(new DateComparison("lt-date", "<"), new String[]{"lt-date", "less-than-date"});
        addComparison(new DateComparison("gte-date", ">="), new String[]{"gte-date", "greater-than-equal-date"});
        addComparison(new DateComparison("gt-date", ">"), new String[]{"gt-date", "greater-than-date"});
    }

    static public void addComparison(SqlComparison comp, String[] aliases)
    {
        comparisonsList.add(comp);
        comparisonsMap.put(comp.getName(), comp);

        if(aliases != null)
        {
            for(int i = 0; i < aliases.length; i++)
                comparisonsMap.put(aliases[i], comp);
        }
    }

    static public List getComparisonsList()
    {
        return comparisonsList;
    }

    static public SqlComparison getComparison(String name)
    {
        return (SqlComparison) comparisonsMap.get(name);
    }

    public static void createCatalog(Element parent)
    {
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "SQL Comparisons");
        factoryElem.setAttribute("class", SqlComparisonFactory.class.getName());
        for(Iterator i = comparisonsMap.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("dialog-field");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((SqlComparison) entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }
    }

}
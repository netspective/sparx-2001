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
 * $Id: QueryDefinition.java,v 1.4 2002-11-30 16:38:43 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.util.ClassPath;

public class QueryDefinition
{
    static public class QueryFieldSortInfo
    {
        private QueryField field;
        private boolean isDescending;

        public QueryFieldSortInfo(QueryField field, boolean descending)
        {
            this.field = field;
            isDescending = descending;
        }

        public QueryField getField()
        {
            return field;
        }

        public void setField(QueryField field)
        {
            this.field = field;
        }

        public boolean isDescending()
        {
            return isDescending;
        }

        public void setDescending(boolean descending)
        {
            isDescending = descending;
        }
    }

    private boolean isDynamic;
    private String name;
    private SingleValueSource dataSourceValueSource;
    private List fieldsList = new ArrayList();
    private Map fieldsMap = new Hashtable();
    private Map joins = new Hashtable();
    private List selectsList = new ArrayList();
    private Map selectsMap = new Hashtable();
    private List selectDialogsList = new ArrayList();
    private Map selectDialogsMap = new Hashtable();
    private List defaultConditions;
    private List defaultWhereExprs;
    private List autoIncludeJoins = new ArrayList();
    private List errors;
    private QueryBuilderDialog dialog;

    public QueryDefinition()
    {
        dataSourceValueSource = null;
    }

    public QueryDefinition(boolean isDynamic)
    {
        this();
        this.isDynamic = isDynamic;
    }

    public boolean isDynamic()
    {
        return isDynamic;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public SingleValueSource getDataSource()
    {
        return dataSourceValueSource;
    }

    public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public List getFieldsList()
    {
        return fieldsList;
    }

    public Map getFieldsMap()
    {
        return fieldsMap;
    }

    public QueryField getField(String name)
    {
        return (QueryField) fieldsMap.get(name);
    }

    public Map getJoins()
    {
        return joins;
    }

    public QueryJoin getJoin(String name)
    {
        return (QueryJoin) joins.get(name);
    }

    public List getAutoIncJoins()
    {
        return autoIncludeJoins;
    }

    public List getDefaultConditions()
    {
        return defaultConditions;
    }

    public List getWhereExprs()
    {
        return defaultWhereExprs;
    }

    public QueryBuilderDialog getBuilderDialog()
    {
        if(dialog == null)
            dialog = new QueryBuilderDialog(this);
        return dialog;
    }

    public List getSelectDialogsList()
    {
        return selectDialogsList;
    }

    public Map getSelectDialogsMap()
    {
        return selectDialogsMap;
    }

    public QuerySelectDialog getSelectDialog(String name)
    {
        return (QuerySelectDialog) selectDialogsMap.get(name);
    }

    public List getSelectsList()
    {
        return selectsList;
    }

    public Map getSelectsMap()
    {
        return selectsMap;
    }

    public QuerySelect getSelect(String name)
    {
        return (QuerySelect) selectsMap.get(name);
    }

    public List getErrors()
    {
        return errors;
    }

    public void addError(String group, String message)
    {
        if(errors == null) errors = new ArrayList();
        errors.add(group + ": " + message);
    }

    public QueryFieldSortInfo[] getFieldsFromDelimitedNames(String names, String delim)
    {
        List result = new ArrayList();
        StringTokenizer st = new StringTokenizer(names, delim);
        while(st.hasMoreTokens())
        {
            String fieldName = st.nextToken();
            boolean isDescending = false;
            if(fieldName.startsWith("-"))
            {
                fieldName = fieldName.substring(1);
                isDescending = true;
            }
            result.add(new QueryFieldSortInfo((QueryField) fieldsMap.get(fieldName), isDescending));
        }
        return (QueryFieldSortInfo[]) result.toArray(new QueryFieldSortInfo[result.size()]);
    }

    public void importFromXml(XmlSource xs, Element elem)
    {
        name = elem.getAttribute("id");

        setDataSource(elem.getAttribute("data-src"));

        List selectElems = new ArrayList();
        List selectDialogElems = new ArrayList();
        List condElems = new ArrayList();
        List whereExprElems = new ArrayList();

        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = node.getNodeName();
            if(childName.equals("field"))
            {
                Element fieldElem = (Element) node;
                ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(fieldElem.getAttribute("class"), QueryField.class, true);
                QueryField field = (QueryField) instanceGen.getInstance();
                field.importFromXml(fieldElem);
                defineField(field);
            }
            else if(childName.equals("join"))
            {
                Element joinElem = (Element) node;
                ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(joinElem.getAttribute("class"), QueryJoin.class, true);
                QueryJoin join = (QueryJoin) instanceGen.getInstance();
                join.importFromXml(joinElem);
                defineJoin(join);
            }
            else if(childName.equals("select"))
            {
                selectElems.add(node);
            }
            else if(childName.equals("select-dialog"))
            {
                selectDialogElems.add(node);
            }
            else if(childName.equals("default-condition"))
            {
                condElems.add(node);
            }
            else if(childName.equals("default-where-expr"))
            {
                whereExprElems.add(node);
            }
        }

        finalizeDefn();

        // now that we have all the fields and joins connected, define all
        // conditions that are specified

        if(condElems.size() > 0)
        {
            defaultConditions = new ArrayList();
            for(Iterator i = condElems.iterator(); i.hasNext();)
            {
                Element condElem = (Element) i.next();
                ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(condElem.getAttribute("class"), QueryCondition.class, true);
                QueryCondition cond = (QueryCondition) instanceGen.getInstance();
                cond.importFromXml(this, condElem);
                defineDefaultCondition(cond);
            }
        }

        if(whereExprElems.size() > 0)
        {
            defaultWhereExprs = new ArrayList();
            for(Iterator i = whereExprElems.iterator(); i.hasNext();)
            {
                Element whereExprElem = (Element) i.next();
                ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(whereExprElem.getAttribute("class"), SqlWhereExpression.class, true);
                SqlWhereExpression expr = (SqlWhereExpression) instanceGen.getInstance();
                expr.importFromXml(whereExprElem);
                defineWhereExpression(expr);
            }
        }

        // now that we have all the fields and joins connected, define all
        // selects that are specified

        for(Iterator i = selectElems.iterator(); i.hasNext();)
        {
            Element selectElem = (Element) i.next();
            ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(selectElem.getAttribute("class"), QuerySelect.class, true);
            QuerySelect select = (QuerySelect) instanceGen.getInstance();
            select.setQueryDefn(this);
            select.importFromXml(selectElem);
            defineSelect(select);
        }

        // all the query-specific stuff is now known so try and create all the
        // fixed-condition dialogs

        for(Iterator i = selectDialogElems.iterator(); i.hasNext();)
        {
            QuerySelectDialog dialog = new QuerySelectDialog(this);
            Element dialogElem = (Element) i.next();
            xs.processTemplates(dialogElem);
            dialog.importFromXml(null, dialogElem);
            defineSelectDialog(dialog);
        }
    }

    public void defineSelectDialog(QuerySelectDialog dialog)
    {
        selectDialogsList.add(dialog);
        selectDialogsMap.put(dialog.getName(), dialog);
    }

    public void defineSelect(QuerySelect select)
    {
        selectsList.add(select);
        selectsMap.put(select.getName(), select);
    }

    public void defineWhereExpression(SqlWhereExpression expr)
    {
        defaultWhereExprs.add(expr);
    }

    public void defineDefaultCondition(QueryCondition cond)
    {
        defaultConditions.add(cond);
    }

    public void finalizeDefn()
    {
        // now that we have all the fields and joins, allow the fields and
        // joins to "connect" themselves

        for(Iterator i = joins.values().iterator(); i.hasNext();)
        {
            QueryJoin join = (QueryJoin) i.next();
            join.finalizeDefn(this);
            if(join.shouldAutoInclude())
                autoIncludeJoins.add(join);
        }

        for(Iterator i = fieldsList.iterator(); i.hasNext();)
        {
            ((QueryField) i.next()).finalizeDefn(this);
        }
    }

    public void defineJoin(QueryJoin join)
    {
        joins.put(join.getName(), join);
    }

    public void defineField(QueryField field)
    {
        fieldsList.add(field);
        fieldsMap.put(field.getName(), field);
    }
}
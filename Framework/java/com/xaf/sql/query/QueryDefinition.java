package com.xaf.sql.query;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.db.*;

public class QueryDefinition
{
	private String name;
    private String dataSourceId;
	private int dbms;
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
    }

	public String getName() { return name; }
	public int getDbms() { return dbms; }
    public String getDataSource() { return dataSourceId; }

	public List getFieldsList() { return fieldsList; }
	public Map getFieldsMap() { return fieldsMap; }
	public QueryField getField(String name) { return (QueryField) fieldsMap.get(name); }

	public Map getJoins() { return joins; }
	public QueryJoin getJoin(String name) { return (QueryJoin) joins.get(name); }
	public List getAutoIncJoins() { return autoIncludeJoins; }

	public List getDefaultConditions() { return defaultConditions; }
	public List getWhereExprs() { return defaultWhereExprs; }

	public QueryBuilderDialog getBuilderDialog()
	{
		if(dialog == null)
			dialog = new QueryBuilderDialog(this);
		return dialog;
	}

	public List getSelectDialogsList() { return selectDialogsList; }
	public Map getSelectDialogsMap() { return selectDialogsMap; }
	public QuerySelectDialog getSelectDialog(String name) { return (QuerySelectDialog) selectDialogsMap.get(name); }

	public List getSelectsList() { return selectsList; }
	public Map getSelectsMap() { return selectsMap; }
	public QuerySelect getSelect(String name) { return (QuerySelect) selectsMap.get(name); }

	public List getErrors() { return errors; }

	public void addError(String group, String message)
	{
		if(errors == null) errors = new ArrayList();
		errors.add(group + ": " + message);
	}

	public void importFromXml(Element elem)
	{
		name = elem.getAttribute("id");
        dataSourceId = elem.getAttribute("data-src");
        if(dataSourceId.length() == 0)
            dataSourceId = null;

		String dbmsId = elem.getAttribute("dbms");
		if(dbmsId != null && dbmsId.length() > 0)
			dbms = DBMS.getCodeFromId(dbmsId);

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
				QueryField field = new QueryField();
				field.importFromXml(fieldElem);
				fieldsList.add(field);
				fieldsMap.put(field.getName(), field);
			}
			else if(childName.equals("join"))
			{
				Element joinElem = (Element) node;
				QueryJoin join = new QueryJoin();
				join.importFromXml(joinElem);
				joins.put(join.getName(), join);
			}
			else if(childName.equals("select"))
			{
				selectElems.add((Element) node);
			}
			else if(childName.equals("select-dialog"))
			{
				selectDialogElems.add((Element) node);
			}
			else if(childName.equals("default-condition"))
			{
				condElems.add((Element) node);
			}
			else if(childName.equals("default-where-expr"))
			{
				whereExprElems.add((Element) node);
			}
		}

		// now that we have all the fields and joins, allow the fields and
		// joins to "connect" themselves

		for(Iterator i = joins.values().iterator(); i.hasNext(); )
		{
			QueryJoin join = (QueryJoin) i.next();
			join.finalizeDefn(this);
			if(join.shouldAutoInclude())
				autoIncludeJoins.add(join);
		}

		for(Iterator i = fieldsList.iterator(); i.hasNext(); )
		{
			((QueryField) i.next()).finalizeDefn(this);
		}

		// now that we have all the fields and joins connected, define all
		// conditions that are specified

		if(condElems.size() > 0)
		{
			defaultConditions = new ArrayList();
			for(Iterator i = condElems.iterator(); i.hasNext(); )
			{
				QueryCondition cond = new QueryCondition();
				cond.importFromXml(this, (Element) i.next());
				defaultConditions.add(cond);
			}
		}

		if(whereExprElems.size() > 0)
		{
			defaultWhereExprs = new ArrayList();
			for(Iterator i = whereExprElems.iterator(); i.hasNext(); )
			{
				SqlWhereExpression expr = new SqlWhereExpression();
				expr.importFromXml((Element) i.next());
				defaultWhereExprs.add(expr);
			}
		}

		// now that we have all the fields and joins connected, define all
		// selects that are specified

		for(Iterator i = selectElems.iterator(); i.hasNext(); )
		{
			QuerySelect select = new QuerySelect(this);
			select.importFromXml((Element) i.next());
			selectsList.add(select);
			selectsMap.put(select.getName(), select);
		}

		// all the query-specific stuff is now known so try and create all the
		// fixed-condition dialogs

		for(Iterator i = selectDialogElems.iterator(); i.hasNext(); )
		{
			QuerySelectDialog dialog = new QuerySelectDialog(this);
			dialog.importFromXml(null, (Element) i.next());
			selectDialogsList.add(dialog);
			selectDialogsMap.put(dialog.getName(), dialog);
		}
	}
}
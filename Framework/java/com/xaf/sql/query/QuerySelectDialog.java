package com.xaf.sql.query;

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.http.*;
import org.w3c.dom.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.report.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class QuerySelectDialog extends Dialog
{
	static public final String QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME = "queryDefnName";
	static public final String QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME = "queryDefnSelectDialogName";

	private QueryDefinition queryDefn;
	private String fixedCondId;
	private QuerySelect select;

    public QuerySelectDialog(QueryDefinition queryDefn)
    {
		setQueryDefn(queryDefn);
		setLoopEntries(true);
    }

	public void importFromXml(String packageName, Element elem)
	{
		String dialogId = elem.getAttribute("name");
		NodeList children = elem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element childElem = (Element) node;
			String childName = node.getNodeName();
			if(childName.startsWith(DialogField.FIELDTAGPREFIX))
			{
				String condFieldId = childElem.getAttribute("query-field");
				if(condFieldId.length() > 0)
				{
					QueryField condField = queryDefn.getField(condFieldId);
					if(condField == null)
						throw new RuntimeException("query-field '"+condFieldId+"' in QuerySelectDialog '"+dialogId+"' does not exist");

					String fieldName = childElem.getAttribute("name");
					if(fieldName.length() == 0)
						childElem.setAttribute("name", condFieldId);

					String childCaption = childElem.getAttribute("caption");
					if(childCaption.length() == 0)
						childElem.setAttribute("caption", condField.getCaption());
				}
			}
			else if(childName.equals("select"))
			{
				select = new QuerySelect(queryDefn);
				select.importFromXml(childElem);
			}
		}

		if(select == null)
		    throw new RuntimeException("'select' child element is required for dialogs in QueryFixedCondDialog");

		// now we've got all the QueryDefinition-specific information all setup
		// so now we just do a normal dialog initialization from an XML resource

		super.importFromXml(packageName, elem);

		DialogField hiddenName = new DialogField(QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, null);
		hiddenName.setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);
		addField(hiddenName);

	    hiddenName = new DialogField(QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME, null);
		hiddenName.setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);
		addField(hiddenName);

		DialogField options = new DialogField();
		options.setSimpleName("options");
		options.setFlag(DialogField.FLDFLAG_SHOWCAPTIONASCHILD);

		SelectField rowsPerPage = new SelectField("rows_per_page", "Rows Per Page", SelectField.SELECTSTYLE_COMBO, "10 rows=10;20 rows=20;30 rows=30");
		rowsPerPage.setDefaultValue(new StaticValue("10"));
		options.addChildField(rowsPerPage);
		options.addChildField(new BooleanField("debug", "Debug", BooleanField.BOOLSTYLE_CHECK, 0));

		addField(options);

		setDirector(new DialogDirector());
		addField(new ResultSetNavigatorButtonsField());
	}

	public void makeStateChanges(DialogContext dc, int stage)
	{
		dc.setValue(QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, queryDefn.getName());
		dc.setValue(QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME, getName());
		if(dc.inExecuteMode() && stage == DialogContext.STATECALCSTAGE_FINAL)
		{
			List fields = this.getFields();
			for(int i = 0; i < fields.size(); i++)
				dc.setFlag(((DialogField) fields.get(i)).getQualifiedName(), DialogField.FLDFLAG_INVISIBLE);

			dc.setFlag("director", DialogField.FLDFLAG_INVISIBLE);
			dc.clearFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);
		}
		else
		{
			List fields = this.getFields();
			for(int i = 0; i < fields.size(); i++)
				dc.clearFlag(((DialogField) fields.get(i)).getQualifiedName(), DialogField.FLDFLAG_INVISIBLE);

			dc.setFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);
		}
	}

	public QueryDefinition getQueryDefn() { return queryDefn; }
	public void setQueryDefn(QueryDefinition value)
	{
		queryDefn = value;
	}

	public String execute(DialogContext dc)
	{
        String debugStr = dc.getValue("options.debug");
		if(debugStr != null && debugStr.equals("1"))
		{
            String sql = select.getSql(dc);
            return "<p><pre><code>SQL:<p>" + sql + (sql == null ? "<p>" + select.getErrorSql() : select.getBindParamsDebugHtml(dc)) + "</code></pre>";
		}

		String transactionId = dc.getTransactionId();
		HttpSession session = dc.getSession();
		QuerySelectScrollState state = (QuerySelectScrollState) session.getAttribute(transactionId);

		try
		{
            /*
                If the state is not found, then we have not executed at all yet;
                if the state is found and it's the initial execution then it means
                that the user has pressed the "back" button -- which means we
                should reset the state management.
             */
			if(state == null || (state != null && dc.isInitialExecute()))
			{
				state = new QuerySelectScrollState(DatabaseContextFactory.getContext(dc), dc, select, Integer.parseInt(dc.getValue("options.rows_per_page")));
				if(state.isValid())
					session.setAttribute(transactionId, state);
				else
					return "Could not execute SQL: " + state.getErrorMsg();
			}
			dc.getRequest().setAttribute(transactionId + "_state", state);

			if(dc.getRequest().getParameter("rs_nav_next") != null)
				state.setPageDelta(1);
			else if(dc.getRequest().getParameter("rs_nav_prev") != null)
				state.setPageDelta(-1);
			else if(dc.getRequest().getParameter("rs_nav_last") != null)
				state.setPage(state.getTotalPages());
			else if(dc.getRequest().getParameter("rs_nav_first") != null)
				state.setPage(1);

			StringWriter writer = new StringWriter();
			state.produceReport(writer, dc);
			return writer.toString();
		}
		catch(Exception e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));

            String sql = select.getSql(dc) + "<p><br>" + select.getBindParamsDebugHtml(dc);
			return e.toString() + "<p><pre><code>" + (sql + (sql == null ? "<p>" + select.getErrorSql() : "")) + "\n" + stack.toString() + "</code></pre>";
		}
	}

}
package com.xaf.sql.query;

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.http.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.report.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class QueryBuilderDialog extends Dialog
{
	static public final String QBDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME = "queryDefnName";

	private int maxConditions;
	private QueryDefinition queryDefn;

    public QueryBuilderDialog()
    {
		setName("queryDialog");
		setLoopEntries(true);
    }

    public QueryBuilderDialog(QueryDefinition queryDefn)
    {
		setName("queryDialog");
		setQueryDefn(queryDefn);
		setMaxConditions(5);
		setLoopEntries(true);
    }

	public int getMaxConditions() { return maxConditions; }
	public void setMaxConditions(int value)
	{
		maxConditions = value;
		clearFields();

		int lastConditionNum = maxConditions-1;
		ListValueSource fieldsList = ValueSourceFactory.getListValueSource("query-defn-fields:" + queryDefn.getName());
		ListValueSource compList = ValueSourceFactory.getListValueSource("sql-comparisons:all");

		DialogField hiddenName = new DialogField(QBDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, null);
		hiddenName.setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);
		addField(hiddenName);
		addField(new SeparatorField("conditions_separator", "Conditions"));
		for(int i = 0; i < maxConditions; i++)
		{
			SelectField queryFieldsSelect =
				new SelectField("field", null, SelectField.SELECTSTYLE_COMBO, fieldsList);

			SelectField compareSelect =
				new SelectField("compare", null, SelectField.SELECTSTYLE_COMBO, compList);

			TextField valueText = new TextField("value", null);

			DialogField condition = new DialogField();
			condition.setSimpleName("condition_" + i);
		    condition.addChildField(queryFieldsSelect);
		    condition.addChildField(compareSelect);
		    condition.addChildField(valueText);

			if(i != lastConditionNum)
			{
				SelectField joinSelect =
					new SelectField("join", null, SelectField.SELECTSTYLE_COMBO, " ;and;or");
				condition.addChildField(joinSelect);
			}

			if(i > 0)
			{
	    		condition.addConditionalAction(new DialogFieldConditionalDisplay(condition, "condition_" + (i - 1) + ".join", "control.value != ' '"));
			}

			addField(condition);
		}

		SelectField predefinedSels = null;
		List predefinedSelects = queryDefn.getSelectsList();
		if(predefinedSelects.size() > 0)
		{
			ListValueSource selectsList = ValueSourceFactory.getListValueSource("query-defn-selects:" + queryDefn.getName());
			predefinedSels = new SelectField("predefined_select", "Display", SelectField.SELECTSTYLE_COMBO, selectsList);
		}

		SelectField displayFields =
			new SelectField("display_fields", null, SelectField.SELECTSTYLE_MULTIDUAL, fieldsList);
		displayFields.setMultiDualCaptions("Available Display Fields", "Show Fields");
		displayFields.setMultiDualWidth(150);
		displayFields.setSize(7);

		SelectField sortFields =
			new SelectField("sort_fields", null, SelectField.SELECTSTYLE_MULTIDUAL, fieldsList);
		sortFields.setMultiDualCaptions("Available Sort Fields", "Sort Fields");
		sortFields.setMultiDualWidth(150);
		sortFields.setSize(5);

		if(predefinedSels != null)
		{
			displayFields.addConditionalAction(new DialogFieldConditionalDisplay(displayFields, "options.predefined_select", "control.value == '"+QueryDefnSelectsListValue.CUSTOMIZE+"'"));
			sortFields.addConditionalAction(new DialogFieldConditionalDisplay(sortFields, "options.predefined_select", "control.value == '"+QueryDefnSelectsListValue.CUSTOMIZE+"'"));
		}

		DialogField options = new DialogField();
		options.setSimpleName("options");
		options.setFlag(DialogField.FLDFLAG_SHOWCAPTIONASCHILD);
		if(predefinedSels != null)
			options.addChildField(predefinedSels);
		options.addChildField(new SelectField("rows_per_page", "Rows Per Page", SelectField.SELECTSTYLE_COMBO, "10 rows=10;20 rows=20;30 rows=30"));
		options.addChildField(new BooleanField("debug", "Debug", BooleanField.BOOLSTYLE_CHECK, 0));

		addField(new SeparatorField("results_separator", "Results"));
		addField(options);
		addField(displayFields);
		addField(sortFields);

		addField(new DialogDirector());
		addField(new ResultSetNavigatorButtonsField());
	}

	public QueryDefinition getQueryDefn() { return queryDefn; }
	public void setQueryDefn(QueryDefinition value)
	{
		queryDefn = value;
	}

	public void makeStateChanges(DialogContext dc, int stage)
	{
		dc.setValue(QBDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, queryDefn.getName());
		if(dc.inExecuteMode() && stage == DialogContext.STATECALCSTAGE_FINAL)
		{
			dc.setFlag("conditions_separator", DialogField.FLDFLAG_INVISIBLE);
			dc.setFlag("results_separator", DialogField.FLDFLAG_INVISIBLE);
			dc.setFlag("options", DialogField.FLDFLAG_INVISIBLE);
			dc.setFlag("display_fields", DialogField.FLDFLAG_INVISIBLE);
			dc.setFlag("sort_fields", DialogField.FLDFLAG_INVISIBLE);
			dc.setFlag("director", DialogField.FLDFLAG_INVISIBLE);
			dc.clearFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);

			int lastCondition = maxConditions-1;
			for(int i = 0; i < maxConditions; i++)
			{
				dc.setFlag("condition_"+i, DialogField.FLDFLAG_READONLY);
			}
		}
		else
		{
			dc.setFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);
		}
	}

	public QuerySelect createSelect(DialogContext dc)
	{
		QuerySelect select = new QuerySelect(queryDefn);

		boolean customizing = true;
		String predefinedSel = dc.getValue("options.predefined_select");
		if(predefinedSel != null && ! predefinedSel.equals(QueryDefnSelectsListValue.CUSTOMIZE))
		{
			customizing = false;
			select.importFromSelect(queryDefn.getSelect(predefinedSel));
		}

		if(customizing)
		{
			String[] display = dc.getValues("display_fields");
			if(display != null && display.length > 0)
				select.addReportFields(display);
			else
				select.addReportField("*");
		}

		for(int i = 0; i < maxConditions; i++)
		{
			String conditionId = "condition_" + i;
			String value = dc.getValue(conditionId + ".value");
			String join = dc.getValue(conditionId + ".join");

			if(value != null && value.length() > 0)
			{
				select.addCondition(
					    dc.getValue(conditionId + ".field"),
					    dc.getValue(conditionId + ".compare"),
						value, join);
			}

			if(join == null || join.equals(" "))
				break;
		}

		if(customizing)
		{
			String[] sort = dc.getValues("sort_fields");
			if(sort != null && sort.length > 0)
				select.addOrderBy(sort);
		}

		return select;
	}

	public String execute(DialogContext dc)
	{
        String debugStr = dc.getValue("options.debug");
		if(debugStr != null && debugStr.equals("1"))
		{
			QuerySelect select = createSelect(dc);
            String sql = select.getSql(dc);
            return "<p><pre><code>SQL:<p>" + sql + (sql == null ? "<p>" + select.getErrorSql() : select.getBindParamsDebugHtml(dc)) + "</code></pre>";
		}

		String transactionId = dc.getTransactionId();
		HttpSession session = dc.getRequest().getSession(true);
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
				QuerySelect select = createSelect(dc);

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
			//return state.getReport(writer, dc);
		}
		catch(Exception e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));

			QuerySelect select = createSelect(dc);
            String sql = select.getSql(dc);
			return e.toString() + "<p><pre><code>" + (sql + (sql == null ? "<p>" + select.getErrorSql() : "")) + "\n" + stack.toString() + "</code></pre>";
		}
	}

}
package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.sql.*;

import com.xaf.sql.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

public class DialogFieldValue extends ValueSource implements ListValueSource
{
    public DialogFieldValue()
    {
    }

    public String getValue(ValueContext vc)
    {
		return vc.getRequest().getParameter(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
		String[] values = getValues(vc);
		for(int i = 0; i < values.length; i++)
			choices.add(new SelectChoice(values[i]));
        return choices;
	}

    public String[] getValues(ValueContext vc)
    {
		return vc.getRequest().getParameterValues(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
	}

	public boolean supportsSetValue()
	{
		return true;
	}

	public void setValue(ValueContext vc, String value)
	{
		if(vc instanceof DialogContext)
		{
			((DialogContext) vc).setValue(valueKey, (String) value);
		}
		else
		{
			throw new RuntimeException("DialogFieldValue.setValue(ValueContext, String) requires a DialogContext as its ValueContext.");
		}
	}

	public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException
	{
		if(storeType != RESULTSET_STORETYPE_SINGLEROWFORMFLD)
			throw new RuntimeException("DialogFieldValue.setValue(ValueContext, ResultSet, int) only supports STORETYPE_SINGLEROWFORMFLD");

		if(vc instanceof DialogContext)
		{
			DialogContext dc = (DialogContext) vc;
			if(rs.next())
			{
				ResultSetMetaData rsmd = rs.getMetaData();
				int colsCount = rsmd.getColumnCount();
				for(int i = 1; i <= colsCount; i++)
				{
					String fieldName = rsmd.getColumnName(i).toLowerCase();
					DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) dc.get(fieldName);
					if(state != null)
						state.value = rs.getString(i);
				}
			}
		}
		else
		{
			Map rsMap = StatementManager.getResultSetSingleRowAsMap(rs);
			vc.getRequest().setAttribute(DialogContext.DIALOG_FIELD_VALUES_ATTR_NAME, rsMap);
		}
	}
}
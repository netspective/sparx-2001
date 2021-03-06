package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.db.*;
import com.xaf.sql.*;
import com.xaf.sql.query.*;

public class QueryDefnSelectsListValue extends ListSource
{
	static public final String CUSTOMIZE = "Customize...";

	private String stmtMgrName;
	private String queryDefnName;

    public QueryDefnSelectsListValue()
    {
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
        int delimPos = srcParams.indexOf('/');
        if(delimPos >= 0)
        {
            stmtMgrName = srcParams.substring(0, delimPos);
            queryDefnName = srcParams.substring(delimPos+1);
        }
        else
            queryDefnName = srcParams;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
		try
		{
			StatementManager stmtMgr = stmtMgrName == null ? StatementManagerFactory.getManager(vc.getServletContext()) : StatementManagerFactory.getManager(stmtMgrName);
			QueryDefinition queryDefn = stmtMgr.getQueryDefn(queryDefnName);

			if(queryDefn == null)
				choices.add(new SelectChoice("queryDefn '"+ queryDefnName +"' not found"));
			else
			{
				List selects = queryDefn.getSelectsList();
				for(Iterator i = selects.iterator(); i.hasNext(); )
				{
					QuerySelect select = (QuerySelect) i.next();
					choices.add(new SelectChoice(select.getCaption(), select.getName()));
				}
				choices.add(new SelectChoice(CUSTOMIZE));
			}
		}
		catch(Exception e)
		{
			choices.add(new SelectChoice(e.toString()));
		}

        return choices;
	}
}
package com.xaf.sql.query;

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

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.report.*;
import com.xaf.skin.*;
import com.xaf.sql.*;
import com.xaf.value.*;

public class QuerySelectScrollState extends ResultSetScrollState
{
	private DatabaseContext dbContext;
	private ValueContext valueContext;
	private QuerySelect select;
	private Report reportDefn;
	private ReportSkin skin;
	private boolean resultSetValid;

    public QuerySelectScrollState(DatabaseContext dc, ValueContext vc, QuerySelect select, int rowsPerPage) throws NamingException, SQLException
    {
		super(select.execute(dc, vc), rowsPerPage);

		this.valueContext = vc;
		this.dbContext = dc;
		this.select = select;

		ResultSet rs = getResultSet();
		if(rs != null)
		{
			this.reportDefn = new StandardReport();
			this.reportDefn.initialize(rs, null);

			ReportColumnsList rcl = this.reportDefn.getColumns();
			List selectFields = select.getReportFields();
			for(int i = 0; i < rcl.size(); i++)
			{
				ReportColumn rc = ((QueryField) selectFields.get(i)).getReportColumn();
				if(rc != null)
					rcl.getColumn(i).importFromColumn(rc);
			}

			this.skin = new HtmlReportSkin();
			this.resultSetValid = true;
		}
    }

	public QuerySelect getSelect() { return select; }
	public String getSelectSql() { return select.getSql(); }
	public String getErrorMsg() { return select.getErrorSql(); }
	public boolean isValid() { return resultSetValid; }

	public void produceReport(Writer writer, DialogContext dc) throws SQLException, NamingException, IOException
	{
		if(! isScrollable())
			scrollToActivePage(select.execute(dbContext, dc));
		else
			scrollToActivePage();

		ReportContext rc = new ReportContext(dc, reportDefn, skin);
		rc.setResultsScrolling(0, getRowsPerPage());

		skin.produceReport(writer, rc, getResultSet());
	}
}
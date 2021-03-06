package com.xaf.sql;

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
import java.text.*;
import javax.naming.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.Metric;
import com.xaf.db.*;
import com.xaf.xml.*;
import com.xaf.skin.*;
import com.xaf.sql.query.*;
import com.xaf.report.*;
import com.xaf.task.*;
import com.xaf.value.*;

public class StatementManager extends XmlSource
{
    static public final Object[] SQL_TYPES_ARRAY =
    {
		"strings", new Integer(Types.ARRAY),
        "integer", new Integer(Types.INTEGER),
        "double", new Integer(Types.DOUBLE),
        "text", new Integer(Types.VARCHAR),
        "varchar", new Integer(Types.VARCHAR)
    };
    static public final Map SQL_TYPES_MAP = new HashMap();

    static public final String getTypeNameForId(int sqlType)
    {
        for(int i = 0; i < SQL_TYPES_ARRAY.length; i+=2)
        {
            if(((Integer) SQL_TYPES_ARRAY[i+1]).intValue() == sqlType)
                return (String) SQL_TYPES_ARRAY[i];
        }
        return null;
    }

	static public class ResultInfo
	{
		protected ResultSet rs;
		protected StatementInfo si;
		protected StatementExecutionLogEntry logEntry;

		ResultInfo(StatementInfo si, ResultSet rs, StatementExecutionLogEntry logEntry)
		{
			this.si = si;
			this.rs = rs;
			this.logEntry = logEntry;
		}

		public ResultSet getResultSet() { return rs; }
		public String getSQL(ValueContext vc) { return si.getSql(vc); }
		public Element getStmtElement() { return si.getStatementElement(); }
		public StatementExecutionLogEntry getLogEntry() { return logEntry; }

		public void close() throws SQLException
		{
            Statement stmt = rs.getStatement();
            Connection conn = stmt.getConnection();
            rs.close();
            stmt.close();
			if (conn.getAutoCommit() == true)
				conn.close();
		}

        protected void finalize() throws Throwable
        {
            close();
            super.finalize();
        }
	}

    static private Hashtable dynamicSql = new Hashtable();

	private String defaultStyleSheet = null;
	private Hashtable statements = new Hashtable();
	private Hashtable queryDefns = new Hashtable();
	private Hashtable reports = new Hashtable();

	public StatementManager(File file)
	{
		loadDocument(file);
	}

    public StatementInfo getStatement(String stmtId)
    {
        reload();
        return (StatementInfo) statements.get(stmtId);
    }

	public Map getStatements()
	{
		reload();
		return statements;
	}

	public Map getQueryDefns()
	{
		reload();
		return queryDefns;
	}

	public QueryDefinition getQueryDefn(String name)
	{
		reload();
		return (QueryDefinition) queryDefns.get(name);
	}

	public void updateExecutionStatistics()
	{
		for(Iterator i = statements.values().iterator(); i.hasNext(); )
		{
			StatementInfo si = (StatementInfo) i.next();
			StatementExecutionLog execLog = si.getExecutionLog();
			StatementExecutionLog.StatementExecutionStatistics stats = execLog.getStatistics();
			Element elem = si.getStatementElement();

			elem.setAttribute("stat-reset-log-after", stats.resetAfterCount == 0 ? "never reset (0)" : (stats.resetAfterCount == -1 ? "unknown (-1)" : Long.toString(stats.resetAfterCount)));
			elem.setAttribute("stat-total-executions", Long.toString(stats.totalExecutions));
			elem.setAttribute("stat-total-failed", Long.toString(stats.totalFailed));
			elem.setAttribute("stat-total-avg-time", Long.toString(stats.averageTotalExecTime));
			elem.setAttribute("stat-total-max-time", Long.toString(stats.maxTotalExecTime));

			elem.setAttribute("stat-connection-avg-time", Long.toString(stats.averageConnectionEstablishTime));
			elem.setAttribute("stat-connection-max-time", Long.toString(stats.maxConnectionEstablishTime));

			elem.setAttribute("stat-bind-params-avg-time", Long.toString(stats.averageBindParamsTime));
			elem.setAttribute("stat-bind-params-max-time", Long.toString(stats.maxBindParamsTime));

			elem.setAttribute("stat-sql-exec-avg-time", Long.toString(stats.averageSqlExecTime));
			elem.setAttribute("stat-sql-exec-max-time", Long.toString(stats.maxSqlExecTime));

			NodeList execLogsNodes = elem.getElementsByTagName("exec-log");
			if(execLogsNodes.getLength() > 0)
			{
				Node execLogElem = execLogsNodes.item(0);
				execLogElem.getParentNode().removeChild(execLogElem);
			}

		    DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);

			Element execLogElem = xmlDoc.createElement("exec-log");
			elem.appendChild(execLogElem);
			for(Iterator l = execLog.iterator(); l.hasNext(); )
			{
				StatementExecutionLogEntry entry = (StatementExecutionLogEntry) l.next();
				Element execLogEntryElem = xmlDoc.createElement("entry");
				execLogElem.appendChild(execLogEntryElem);

				if(! entry.wasSuccessful())
				{
					execLogEntryElem.setAttribute("init-date",   fmt.format(entry.getInitDate()));
					execLogEntryElem.setAttribute("src", entry.getSource());
					execLogEntryElem.setAttribute("total-time", "FAILED");
					continue;
				}

				execLogEntryElem.setAttribute("src", entry.getSource());
				execLogEntryElem.setAttribute("init-date",   fmt.format(entry.getInitDate()));
				execLogEntryElem.setAttribute("total-time", Long.toString(entry.getTotalExecutionTime()));
				execLogEntryElem.setAttribute("conn-time", Long.toString(entry.getConnectionEstablishTime()));
				execLogEntryElem.setAttribute("bind-time", Long.toString(entry.getBindParamsBindTime()));
				execLogEntryElem.setAttribute("sql-time", Long.toString(entry.getSqlExecTime()));
			}
		}
	}

	public void catalogNodes()
	{

		statements.clear();
		//reports.clear();
        queryDefns.clear();
		defaultStyleSheet = null;

        if(SQL_TYPES_MAP.size() == 0)
        {
            for(int i = 0; i < SQL_TYPES_ARRAY.length; i += 2)
            {
                SQL_TYPES_MAP.put(SQL_TYPES_ARRAY[i], SQL_TYPES_ARRAY[i+1]);
            }
        }

        if(xmlDoc == null)
            return;

		NodeList children = xmlDoc.getDocumentElement().getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

            String nodeName = node.getNodeName();
			if(nodeName.equals("sql-statements"))
			{
				Element statementsElem = (Element) node;
				defaultStyleSheet = statementsElem.getAttribute("style-sheet");
				String stmtPkg = statementsElem.getAttribute("package");
				String stmtPkgDataSrc = statementsElem.getAttribute("data-source");
				if(stmtPkgDataSrc.length() == 0)
					stmtPkgDataSrc = null;

				NodeList statementsChildren = node.getChildNodes();
				for(int c = 0; c < statementsChildren.getLength(); c++)
				{
					Node stmtsChild = statementsChildren.item(c);
					if(stmtsChild.getNodeType() != Node.ELEMENT_NODE)
						continue;
                    String childName = stmtsChild.getNodeName();
	    			if(childName.equals("statement"))
					{
						Element stmtElem = (Element) stmtsChild;
		    			StatementInfo si = new StatementInfo();
                        processTemplates(stmtElem);
						si.importFromXml(this, stmtElem, stmtPkg, stmtPkgDataSrc);

						String statementId = si.getId();
						statements.put(statementId, si);
						stmtElem.setAttribute("qualified-name", si.getId());
						stmtElem.setAttribute("package", stmtPkg);
					}
				}
			}
			else if(nodeName.equals("query-defn"))
			{
				QueryDefinition queryDefn = new QueryDefinition();
                processTemplates((Element) node);
				queryDefn.importFromXml(this, (Element) node);
				queryDefns.put(queryDefn.getName(), queryDefn);
			}
			else if(nodeName.equals("report"))
			{
				Report report = new StandardReport();
                processTemplates((Element) node);
				report.importFromXml((Element) node);
				reports.put(report.getName(), report);
			}
			else if(nodeName.equals("register-report-skin"))
			{
				Element typeElem = (Element) node;
				String className = typeElem.getAttribute("class");
				try
				{
					SkinFactory.addReportSkin(typeElem.getAttribute("name"), className);
				}
				catch(IllegalAccessException e)
				{
					errors.add("ReportSkin class '"+className+"' access exception: " + e.toString());
				}
				catch(ClassNotFoundException e)
				{
					errors.add("ReportSkin class '"+className+"' not found: " + e.toString());
				}
				catch(InstantiationException e)
				{
					errors.add("ReportSkin class '"+className+"' instantiation exception: " + e.toString());
				}
			}
            else if(nodeName.equals("register-column-data-calc"))
			{
				Element typeElem = (Element) node;
				String className = typeElem.getAttribute("class");
				try
				{
					ColumnDataCalculatorFactory.addColumnDataCalc(typeElem.getAttribute("name"), className);
				}
				catch(ClassNotFoundException e)
				{
					errors.add("ColumnDataCalculator class '"+className+"' not found: " + e.toString());
				}
			}
            else
            {
                catalogElement((Element) node);
            }
		}

		addMetaInformation();
	}

	static public ResultInfo execute(DatabaseContext dc, ValueContext vc, String dataSourceId, StatementInfo si, Object[] params) throws NamingException, SQLException
	{
		if(dataSourceId == null)
        {
            dataSourceId = si.getDataSource() != null ?si.getDataSource().getValue(vc) : null;
        }

		StatementExecutionLogEntry logEntry = si.createNewExecLogEntry(vc);

		try
		{
			logEntry.registerGetConnectionBegin();
			Connection conn = dc.getConnection(vc, dataSourceId);
			logEntry.registerGetConnectionEnd(conn);

			PreparedStatement stmt = conn.prepareStatement(si.getSql(vc));

			logEntry.registerBindParamsBegin();
			if(params != null)
			{
				for(int i = 0; i < params.length; i++)
					stmt.setObject(i+1, params[i]);
			}
			else
				si.applyParams(dc, vc, stmt);
			logEntry.registerBindParamsEnd();

			logEntry.registerExecSqlBegin();
			if(stmt.execute())
			{
				logEntry.registerExecSqlEndSuccess();
				//logEntry.finalize(vc); -- this will be done in the "finally" block??
				return new ResultInfo(si, stmt.getResultSet(), logEntry);
			}
			logEntry.registerExecSqlEndFailed();
		}
		finally
		{
			logEntry.finalize(vc);
		}

		return null;
	}

	public ResultInfo execute(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
	{
		reload();

		StatementInfo si = (StatementInfo) statements.get(statementId);
		if(si == null)
			throw new StatementNotFoundException(this, statementId);

        return execute(dc, vc, dataSourceId, si, params);
	}

	public ResultInfo execute(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId) throws StatementNotFoundException, NamingException, SQLException
	{
		return execute(dc, vc, dataSourceId, statementId, null);
	}

	public ResultInfo executeAndStore(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, statementId, null);
		ResultSet rs = ri.getResultSet();
		vs.setValue(vc, rs, storeType);
		if(storeType != SingleValueSource.RESULTSET_STORETYPE_RESULTSET)
			ri.close();
		return ri;
	}

	public ResultInfo executeAndStore(DatabaseContext dc, ValueContext vc, String dataSourceId, StatementInfo si, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, si, null);
		ResultSet rs = ri.getResultSet();
		vs.setValue(vc, rs, storeType);
		if(storeType != SingleValueSource.RESULTSET_STORETYPE_RESULTSET)
			ri.close();
		return ri;
	}

	static public Object getResultSetSingleColumn(ResultSet rs) throws SQLException
    {
		if(rs.next())
            return rs.getObject(1);
		else
			return null;
    }

    static public Object[] getResultSetSingleRowArray(ResultSet rs) throws SQLException
	{
		if(rs.next())
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int colsCount = rsmd.getColumnCount();
            Object[] result = new Object[colsCount];
			for(int i = 1; i <= colsCount; i++)
			{
				result[i-1] = rs.getObject(i);
			}
			return result;
		}
		else
			return null;
	}

	static public Map getResultSetSingleRowAsMap(ResultSet rs) throws SQLException
	{
		Map result = new HashMap();
		if(rs.next())
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int colsCount = rsmd.getColumnCount();
			for(int i = 1; i <= colsCount; i++)
			{
				result.put(rsmd.getColumnName(i).toLowerCase(), rs.getObject(i));
			}
			return result;
		}
		else
			return null;
	}

	static public Map[] getResultSetRowsAsMapArray(ResultSet rs) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int colsCount = rsmd.getColumnCount();
		String[] columnNames = new String[colsCount];
		for(int c = 1; c <= colsCount; c++)
		{
			columnNames[c-1] = rsmd.getColumnName(c).toLowerCase();
		}

		ArrayList result = new ArrayList();
		while(rs.next())
		{
			Map rsMap = new HashMap();
			for(int i = 1; i <= colsCount; i++)
			{
				rsMap.put(columnNames[i-1], rs.getObject(i));
			}
			result.add(rsMap);
		}

		if(result.size() > 0)
			return (Map[]) result.toArray(new Map[result.size()]);
		else
			return null;
	}

	static public Object getResultSetSingleColumn(Object[][] data) throws SQLException
    {
		if(data.length > 0)
            return data[0][0];
		else
			return null;
    }

	static public Map getResultSetSingleRowAsMap(ResultSetMetaData rsmd, Object[][] data) throws SQLException
	{
		Map result = new HashMap();
		if(data.length > 0)
		{
			Object[] row = data[0];
			int colsCount = rsmd.getColumnCount();
			for(int i = 1; i <= colsCount; i++)
			{
				result.put(rsmd.getColumnName(i).toLowerCase(), row[i-1]);
			}
			return result;
		}
		else
			return null;
	}

	static public Map[] getResultSetRowsAsMapArray(ResultSetMetaData rsmd, Object[][] data) throws SQLException
	{
		int colsCount = rsmd.getColumnCount();
		String[] columnNames = new String[colsCount];
		for(int c = 1; c <= colsCount; c++)
		{
			columnNames[c-1] = rsmd.getColumnName(c).toLowerCase();
		}

		ArrayList result = new ArrayList();
		for(int rowNum = 0; rowNum < data.length; rowNum++)
		{
			Object[] row = data[rowNum];
			Map rsMap = new HashMap();
			for(int i = 0; i < colsCount; i++)
			{
				rsMap.put(columnNames[i], row[i]);
			}
			result.add(rsMap);
		}

		if(result.size() > 0)
			return (Map[]) result.toArray(new Map[result.size()]);
		else
			return null;
	}

	static public Object[] getResultSetSingleRowAsArray(ResultSet rs) throws SQLException
	{
		if(rs.next())
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int colsCount = rsmd.getColumnCount();
			Object[] result = new Object[colsCount];
			for(int i = 1; i <= colsCount; i++)
			{
				result[i-1] = rs.getObject(i);
			}
			return result;
		}
		else
			return null;
	}

	static public String[] getResultSetSingleRowAsStrings(ResultSet rs) throws SQLException
	{
		if(rs.next())
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int colsCount = rsmd.getColumnCount();
			String[] result = new String[colsCount];
			for(int i = 1; i <= colsCount; i++)
			{
				result[i-1] = rs.getString(i);
			}
			return result;
		}
		else
			return null;
	}

	static public Object[][] getResultSetRowsAsMatrix(ResultSet rs) throws SQLException
	{
		ArrayList result = new ArrayList();
		while(rs.next())
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int colsCount = rsmd.getColumnCount();
			Object[] row = new Object[colsCount];
			for(int i = 1; i <= colsCount; i++)
			{
				row[i-1] = rs.getObject(i);
			}
			result.add(row);
		}

		if(result.size() > 0)
			return (Object[][]) result.toArray(new Object[result.size()][]);
		else
			return null;
	}

	static public String[] getResultSetRowsAsStrings(ResultSet rs) throws SQLException
	{
		ArrayList result = new ArrayList();
		while(rs.next())
		{
			result.add(rs.getString(1));
		}

		if(result.size() > 0)
			return (String[]) result.toArray(new String[result.size()]);
		else
			return null;
	}

	public void produceReport(Writer writer, DatabaseContext dc, ValueContext vc, String dataSourceId, ReportSkin skin, String statementId, Object[] params, String reportId) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
		produceReport(ri, writer, dc, vc, skin, params, reportId);
	}

	public void produceReport(Writer writer, DatabaseContext dc, ValueContext vc, String dataSourceId, ReportSkin skin, StatementInfo si, Object[] params, String reportId) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, si, params);
		produceReport(ri, writer, dc, vc, skin, params, reportId);
	}

	public void produceReport(ResultInfo ri, Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, Object[] params, String reportId) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
        ResultSet rs = ri.getResultSet();

        Report rd = new StandardReport();
		if(vc instanceof TaskContext)
			rd.setCanvas(((TaskContext) vc).getCanvas());

        String statementId = ri.si.getId();
		Element reportElem = ri.si.getReportElement(reportId);
		if(reportElem == null && reportId != null)
		{
			writer.write("Report id '"+reportId+"' not found for statement '"+statementId+"'");
		}

        rd.initialize(rs, reportElem);

		ReportContext rc = new ReportContext(vc, rd, skin);
        rc.produceReport(writer, rs);

		ri.close();
	}

	public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, String dataSourceId, ReportSkin skin, String statementId, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        produceReportAndStoreResultSet(writer, dc, vc, skin, ri, params, reportId, vs, storeType);
	}

	public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, String dataSourceId, ReportSkin skin, StatementInfo si, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, si, params);
        produceReportAndStoreResultSet(writer, dc, vc, skin, ri, params, reportId, vs, storeType);
	}

	public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, ResultInfo ri, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultSet rs = ri.getResultSet();

		// get the ResultSet into a matrix so that we can stash it away later
		// use the matrix to produce the report and do the storage so we don't have to run the query multiple times

	    Object[][] data = StatementManager.getResultSetRowsAsMatrix(rs);
		vs.setValue(vc, rs.getMetaData(), data, storeType);

        Report rd = new StandardReport();
		if(vc instanceof TaskContext)
			rd.setCanvas(((TaskContext) vc).getCanvas());

        String statementId = ri.si.getPkgName() + ri.si.getStmtName();
		Element reportElem = ri.si.getReportElement(reportId);
		if(reportElem == null && reportId != null)
		{
			writer.write("Report id '"+reportId+"' not found for statement '"+statementId+"'");
		}

        rd.initialize(rs, reportElem);

		ReportContext rc = new ReportContext(vc, rd, skin);
        rc.produceReport(writer, data);

		ri.close();
	}

    /**
     * Executes static (XML) SQL and returns true if any results were found
     */
    public boolean stmtRecordExists(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        boolean result = false;
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        if(ri.getResultSet().next())
            result = true;
        ri.close();
        return result;
    }

    /**
     * Executes static (XML) SQL and returns the first column of the first row (single value)
     */
    public Object executeStmtGetValue(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        Object result = getResultSetSingleColumn(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes static (XML) SQL and returns the first row as an array
     */
    public Object[] executeStmtGetValues(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        Object[] result = getResultSetSingleRowArray(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes static (XML) SQL and returns the first row as a map (key is column name, value is value)
     */
    public Map executeStmtGetValuesMap(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        Map result = getResultSetSingleRowAsMap(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes static (XML) and returns the all the rows as an array with each row as a map
     */
    public Map[] executeStmtGetValuesMapArray(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        Map[] result = getResultSetRowsAsMapArray(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes static (XML) SQL and returns all the objects as a matrix
     */
    public Object[][] executeStmtGetValuesMatrix(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        Object[][] result = getResultSetRowsAsMatrix(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes static (XML) SQL and returns all the rows, but only the first column as a string
     */
    public String[] executeStmtGetRowsAsStrings(DatabaseContext dc, ValueContext vc, String dataSourceId, String statementId, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, statementId, params);
        String[] result = getResultSetRowsAsStrings(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL instead of requiring the SQL to be stored in the XML file
     */
    static public ResultInfo executeSql(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        StatementInfo si = (StatementInfo) dynamicSql.get(sql);
        if(si == null)
        {
            si = new StatementInfo(sql);
            dynamicSql.put(sql, si);
        }
        return execute(dc, vc, dataSourceId, si, params);
    }

    /**
     * Executes dynamic (passed into method) SQL and returns true if any results were found
     */
    static public boolean sqlRecordExists(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws StatementNotFoundException, NamingException, SQLException
    {
        boolean result = false;
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        if(ri.getResultSet().next())
            result = true;
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL and returns the first column of the first row (single value)
     */
    static public Object executeSqlGetValue(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        Object result = getResultSetSingleColumn(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL and returns the first row as an array
     */
    static public Object[] executeSqlGetValues(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        Object[] result = getResultSetSingleRowArray(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL and returns the first row as a map (key is column name, value is value)
     */
    static public Map executeSqlGetValuesMap(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        Map result = getResultSetSingleRowAsMap(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL and returns the all the rows as an array with each row as a map
     */
    static public Map[] executeSqlGetValuesMapArray(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        Map[] result = getResultSetRowsAsMapArray(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL and returns all the objects as a matrix
     */
    static public Object[][] executeSqlGetValuesMatrix(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        Object[][] result = getResultSetRowsAsMatrix(ri.getResultSet());
        ri.close();
        return result;
    }

    /**
     * Executes dynamic (passed into method) SQL and returns all the rows, but only the first column as a string
     */
    static public String[] executeSqlGetRowsAsStrings(DatabaseContext dc, ValueContext vc, String dataSourceId, String sql, Object[] params) throws NamingException, SQLException
    {
        ResultInfo ri = executeSql(dc, vc, dataSourceId, sql, params);
        String[] result = getResultSetRowsAsStrings(ri.getResultSet());
        ri.close();
        return result;
    }

	public Metric getMetrics(Metric root)
	{
		reload();

		Metric uiMetrics = root.createChildMetricGroup("User Interface");
		Metric reportsMetric = uiMetrics.createChildMetricSimple("SQL Reports");
		reportsMetric.setFlag(Metric.METRICFLAG_SUM_CHILDREN);

		Metric stdReportsMetric = reportsMetric.createChildMetricSimple("Standard");
		Metric customReportsMetric = reportsMetric.createChildMetricSimple("Custom");
		Metric qddMetric = uiMetrics.createChildMetricSimple("Query Definition Select Dialogs");
		Metric skinsMetric = uiMetrics.createChildMetricSimple("Custom Report Skins");

		Metric metrics = root.createChildMetricGroup("Database");
		Metric packagesMetric = metrics.createChildMetricSimple("Total Packages");
		Metric stmtsMetric = metrics.createChildMetricSimple("Total SQL Statements");
		Metric qdMetric = metrics.createChildMetricSimple("Total Query Definitions");
		Metric qdfMetric = qdMetric.createChildMetricSimple("Query Definition Fields");
		Metric qdjMetric = qdMetric.createChildMetricSimple("Query Definition Joins");

		try
		{
			packagesMetric.setSum(getSelectNodeListCount("//sql-statements"));
			stmtsMetric.setSum(getSelectNodeListCount("//statement"));
			stdReportsMetric.setSum(getSelectNodeListCount("//statement/report[not(@name)]"));
			customReportsMetric.setSum(getSelectNodeListCount("//statement/report[@name]"));
			qdMetric.setSum(getSelectNodeListCount("//query-defn"));
			qdfMetric.setSum(getSelectNodeListCount("//query-defn/field"));
			qdjMetric.setSum(getSelectNodeListCount("//query-defn/join"));
			qddMetric.setSum(getSelectNodeListCount("//query-defn/select-dialog"));
			skinsMetric.setSum(getSelectNodeListCount("//register-report-skin"));
		}
		catch(Exception e)
		{
			metrics.createChildMetricSimple(e.toString());
		}

		return metrics;
	}
}
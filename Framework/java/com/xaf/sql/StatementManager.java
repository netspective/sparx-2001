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
import javax.naming.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.db.*;
import com.xaf.xml.*;
import com.xaf.skin.*;
import com.xaf.sql.query.*;
import com.xaf.report.*;
import com.xaf.value.*;

public class StatementManager extends XmlSource
{
    static public final Object[] SQL_TYPES_ARRAY =
    {
        "integer", new Integer(Types.INTEGER),
        "double", new Integer(Types.DOUBLE),
        "text", new Integer(Types.VARCHAR),
        "varchar", new Integer(Types.VARCHAR)
    };
    static public final Map SQL_TYPES_MAP = new HashMap();

    static public String getTypeNameForId(int sqlType)
    {
        for(int i = 0; i < SQL_TYPES_ARRAY.length; i+=2)
        {
            if(((Integer) SQL_TYPES_ARRAY[i+1]).intValue() == sqlType)
                return (String) SQL_TYPES_ARRAY[i];
        }
        return null;
    }

	static public class StatementInfo
	{
		protected String pkgName;
		protected String stmtName;
		protected String dataSourceId;
		protected Element stmtElem;
		protected String sql;
		protected SingleValueSource[] paramValueSources;
        protected int[] paramTypes;
		protected List reportElems;

		public StatementInfo()
		{
		}

        public final String getId() { return pkgName != null ? (pkgName + "." + stmtName) : stmtName; }
        public final String getSql() { return sql; }
        public final SingleValueSource[] getParams() { return paramValueSources; }
        public final int[] getParamTypes() { return paramTypes; }

		public void applyParams(DatabaseContext dc, ValueContext vc, PreparedStatement stmt) throws SQLException
		{
            if(paramValueSources == null)
                return;

			int paramsCount = paramValueSources.length;
			for(int i = 0; i < paramsCount; i++)
			{
				SingleValueSource vs = (SingleValueSource) paramValueSources[i];
                if(paramTypes[i] == Types.VARCHAR)
    				stmt.setObject(i+1, vs.getValue(vc));
                else
                {
                    switch(paramTypes[i])
                    {
                        case Types.INTEGER:
                            stmt.setInt(i+1, vs.getIntValue(vc));
                            break;

                        case Types.DOUBLE:
                            stmt.setDouble(i+1, vs.getDoubleValue(vc));
                            break;
                    }
                }
			}
		}

		public void importFromXml(Element stmtElem, String pkgName, String pkgDataSourceId)
		{
			this.pkgName = pkgName;
			this.stmtElem = stmtElem;
			stmtName = stmtElem.getAttribute("name");
		    sql = stmtElem.getFirstChild().getNodeValue();
            ArrayList paramElems = new ArrayList();

			dataSourceId = stmtElem.getAttribute("data-source");
			if(dataSourceId.length() == 0)
			{
				dataSourceId = null;
				if(pkgDataSourceId != null)
					dataSourceId = pkgDataSourceId;
			}

			NodeList stmtChildren = stmtElem.getChildNodes();
			for(int ch = 0; ch < stmtChildren.getLength(); ch++)
			{
				Node stmtChild = stmtChildren.item(ch);
				if(stmtChild.getNodeType() != Node.ELEMENT_NODE)
					continue;
                String childName = stmtChild.getNodeName();
				if(childName.equals("report"))
				{
					if(reportElems == null) reportElems = new ArrayList();
					reportElems.add(stmtChild);
				}
				else if(childName.equals("params"))
				{
					NodeList paramsChildren = stmtChild.getChildNodes();
					for(int p = 0; p < paramsChildren.getLength(); p++)
					{
						Node paramsChild = paramsChildren.item(p);
						if(paramsChild.getNodeType() != Node.ELEMENT_NODE)
							continue;

						Element paramElem = (Element) paramsChild;
                        paramElems.add(paramElem);
					}
				}
			}

            if(paramElems.size() > 0)
            {
                int paramElemsCount = paramElems.size();
                paramValueSources = new SingleValueSource[paramElemsCount];
                paramTypes = new int[paramElemsCount];

                for(int p = 0; p < paramElemsCount; p++)
                {
                    Element paramElem = (Element) paramElems.get(p);
                    paramValueSources[p] = ValueSourceFactory.getSingleOrStaticValueSource(paramElem.getAttribute("value"));
                    String paramTypeName = paramElem.getAttribute("type");
                    if(paramTypeName.length() > 0)
                    {
                        Integer typeNum = (Integer) SQL_TYPES_MAP.get(paramTypeName);
                        if(typeNum == null)
                            throw new RuntimeException("param type '"+paramTypeName+"' is invalid for statement '"+pkgName+stmtName+"'");
                        paramTypes[p] = typeNum.intValue();
                    }
                    else
                    {
                        paramTypes[p] = Types.VARCHAR;
                    }
                }
            }
		}

		public String getDebugHtml(ValueContext vc)
		{
			StringBuffer html = new StringBuffer();
			html.append("<pre>");
			html.append(getSql());
			html.append("</pre>");
			if(paramValueSources != null)
			{
				html.append("<p>Bind Parameters:<ol>");
				int paramsCount = paramValueSources.length;
				for(int i = 0; i < paramsCount; i++)
				{
					SingleValueSource vs = (SingleValueSource) paramValueSources[i];
					html.append("<li><code><b>");
					html.append(vs.getId());
					html.append("</b> = ");
					html.append(vs.getValue(vc));
					html.append("</code> (");
                    html.append(getTypeNameForId(paramTypes[i]));
					html.append(")</li>");
				}
			}
			return html.toString();
		}
	}

	static public class ResultInfo
	{
		protected ResultSet rs;
		protected StatementInfo si;

		ResultInfo(StatementInfo si, ResultSet rs)
		{
			this.si = si;
			this.rs = rs;
		}

		public ResultSet getResultSet() { return rs; }
		public String getSQL() { return si.sql; }
		public Element getStmtElement() { return si.stmtElem; }
	}

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

	public Map getReports()
	{
		reload();
		return reports;
	}

	public QueryDefinition getQueryDefn(String name)
	{
		reload();
		return (QueryDefinition) queryDefns.get(name);
	}

	public void catalogNodes()
	{

		statements.clear();
		reports.clear();
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
						si.importFromXml(stmtElem, stmtPkg, stmtPkgDataSrc);

						String statementId = si.getId();
						statements.put(statementId, si);
						stmtElem.setAttribute("qualified-name", si.getId());
						stmtElem.setAttribute("package", stmtPkg);
						if(si.reportElems != null)
						{
							for(Iterator i = si.reportElems.iterator(); i.hasNext(); )
							{
								Element reportElem = (Element) i.next();
								String reportName = reportElem.getAttribute("name");
								if(reportName.length() > 0)
									reports.put(statementId + "." + reportName, reportElem);
								else
									reports.put(statementId, reportElem);
							}
						}
					}
				}
			}
			else if(nodeName.equals("query-defn"))
			{
				QueryDefinition queryDefn = new QueryDefinition();
				queryDefn.importFromXml((Element) node);
				queryDefns.put(queryDefn.getName(), queryDefn);
			}
			else if(nodeName.equals("report"))
			{
				Report report = new StandardReport();
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
		}

	}

	static public ResultInfo execute(DatabaseContext dc, ValueContext vc, String dataSourceId, StatementInfo si, Object[] params) throws NamingException, SQLException
	{
		if(dataSourceId == null)
			dataSourceId = si.dataSourceId;

		Connection conn = dataSourceId == null ? dc.getConnection() : dc.getConnection(dataSourceId);
        if (conn == null)
            throw new RuntimeException("Your mama!" + dataSourceId);
        PreparedStatement stmt = conn.prepareStatement(si.sql);

        if(params != null)
        {
            for(int i = 0; i < params.length; i++)
                stmt.setObject(i, params[i]);
        }
        else if(si.paramValueSources != null)
            si.applyParams(dc, vc, stmt);

        if(stmt.execute())
            return new ResultInfo(si, stmt.getResultSet());

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
			rs.close();
		return ri;
	}

	public ResultInfo executeAndStore(DatabaseContext dc, ValueContext vc, String dataSourceId, StatementInfo si, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException
	{
		ResultInfo ri = execute(dc, vc, dataSourceId, si, null);
		ResultSet rs = ri.getResultSet();
		vs.setValue(vc, rs, storeType);
		if(storeType != SingleValueSource.RESULTSET_STORETYPE_RESULTSET)
			rs.close();
		return ri;
	}

	static public Object getResultSetSingleColumn(ResultSet rs) throws SQLException
    {
		if(rs.next())
            return rs.getObject(1);
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
			result.add(rs.getString(0));
		}

		if(result.size() > 0)
			return (String[]) result.toArray(new String[result.size()]);
		else
			return null;
	}

	public void produceReport(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, String statementId, Object[] params, String reportId) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, null, statementId, params);
		produceReport(ri, writer, dc, vc, skin, params, reportId);
	}

	public void produceReport(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, StatementInfo si, Object[] params, String reportId) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, null, si, params);
		produceReport(ri, writer, dc, vc, skin, params, reportId);
	}

	public void produceReport(ResultInfo ri, Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, Object[] params, String reportId) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
        ResultSet rs = ri.getResultSet();

        Report rd = new StandardReport();
        String statementId = ri.si.getId();
		Element reportElem = (Element) reports.get(reportId == null ? statementId : (statementId + "." + reportId));
		if(reportElem == null && reportId != null)
		{
			writer.write("Report id '"+reportElem+"' not found for statement '"+statementId+"'");
		}

        rd.initialize(rs, reportElem);
        rd.produceReport(writer, rs, skin);
		rs.close();
	}

	public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, String statementId, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, null, statementId, params);
        produceReportAndStoreResultSet(writer, dc, vc, skin, ri, params, reportId, vs, storeType);
	}

	public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, StatementInfo si, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		ResultInfo ri = execute(dc, vc, null, si, params);
        produceReportAndStoreResultSet(writer, dc, vc, skin, ri, params, reportId, vs, storeType);
	}

	public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, ResultInfo ri, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
	{
		//ResultInfo ri = execute(dc, vc, null, statementId, params);
		ResultSet rs = ri.getResultSet();

		// get the ResultSet into a matrix so that we can stash it away later
		// use the matrix to produce the report and do the storage so we don't have to run the query multiple times

	    Object[][] data = StatementManager.getResultSetRowsAsMatrix(rs);
		vs.setValue(vc, rs.getMetaData(), data, storeType);

        Report rd = new StandardReport();
        String statementId = ri.si.pkgName + ri.si.stmtName;
		Element reportElem = (Element) reports.get(reportId == null ? statementId : (statementId + "." + reportId));
		if(reportElem == null && reportId != null)
		{
			writer.write("Report id '"+reportElem+"' not found for statement '"+statementId+"'");
		}

        rd.initialize(rs, reportElem);
        rd.produceReport(writer, data, skin);
		rs.close();
	}
}
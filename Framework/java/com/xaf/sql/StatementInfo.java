package com.xaf.sql;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.naming.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.config.*;
import com.xaf.db.*;
import com.xaf.xml.*;
import com.xaf.skin.*;
import com.xaf.sql.query.*;
import com.xaf.report.*;
import com.xaf.value.*;

public class StatementInfo
{
	public final static String REPLACEMENT_PREFIX = "${";
	public final static String LISTPARAM_PREFIX = "param-list:";

	private String pkgName;
	private String stmtName;
	private SingleValueSource dataSourceValueSource;
	private Element stmtElem;
	private boolean sqlIsDynamic;
	private String sql;
	private StatementParameter[] parameters;
	private Element defaultReportElem;
	private Map reportElems;
	private StatementExecutionLog execLog = new StatementExecutionLog();

	public StatementInfo()
	{
	}

    public StatementInfo(String sql)
	{
        this.pkgName = "dynamic";
        this.stmtName = "stmt_" + this.toString();
        this.sql = sql;
        if(sql.indexOf(REPLACEMENT_PREFIX) != -1)
			sqlIsDynamic = true;
	}

	public final String getPkgName() { return pkgName; }
	public final String getStmtName() { return stmtName; }
    public final String getId() { return pkgName != null ? (pkgName + "." + stmtName) : stmtName; }
	public final Element getStatementElement() { return stmtElem; }
	public final Map getReportElems() { return reportElems; }
	public final Element getReportElement(String name) { return name == null ? defaultReportElem : (Element) reportElems.get(name); }
	public final SingleValueSource getDataSource() { return dataSourceValueSource; }
	public final void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }
    public final StatementParameter[] getParams() { return parameters; }
	public final StatementExecutionLog getExecutionLog() { return execLog; }
	public final StatementExecutionLogEntry createNewExecLogEntry(ValueContext vc) { return execLog.createNewEntry(vc, this); }

    public final String getSql(ValueContext vc)
	{
		if(! sqlIsDynamic)
			return sql;
		else
			return formatSql(vc);
	}

    /** Replace ${xxx} values
     */
    public String formatSql(ValueContext vc)
    {
		Configuration config = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());

        StringBuffer sb = new StringBuffer();
        int i = 0;
        int prev = 0;

        int pos;
        while((pos=sql.indexOf("$", prev )) >= 0)
		{
            if(pos>0)
			{
                sb.append(sql.substring( prev, pos ));
            }
            if( pos == (sql.length() - 1))
			{
                sb.append('$');
                prev = pos + 1;
            }
            else if (sql.charAt( pos + 1 ) != '{')
			{
                sb.append(sql.charAt(pos + 1));
                prev=pos+2;
            }
			else
			{
                int endName=sql.indexOf('}', pos);
                if( endName < 0 )
				{
                    throw new RuntimeException("Syntax error in sql: " + sql);
                }
                String expression = sql.substring(pos+2, endName);

				if(expression.startsWith(LISTPARAM_PREFIX)) // format is param:# 12 below is length of "param:"
				{
					try
					{
						int paramNum = Integer.parseInt(expression.substring(LISTPARAM_PREFIX.length()));
						if(paramNum >= 0 && paramNum < parameters.length)
						{
							StatementParameter param = parameters[paramNum];
							if(! param.isListType())
								throw new RuntimeException("Only list parameters may be specified here (param '"+ paramNum +"')");

							ListValueSource source = param.getListSource();
							String[] values = source.getValues(vc);

							for(int q = 0; q < values.length; q++)
							{
								if(q > 0)
									sb.append(", ");
								sb.append("?");
							}
						}
						else
							throw new RuntimeException("Parameter '"+ paramNum +"' does not exist");
					}
					catch(Exception e)
					{
						sb.append("##"+ e.toString() +"##");
					}
				}
				else
				{
					// TO DO: replace ' with '' !!
					sb.append(config.getValue(vc, expression));
				}

                prev=endName+1;
            }
        }

        if(prev < sql.length()) sb.append(sql.substring(prev));
        return sb.toString();
    }

	public void applyParams(DatabaseContext dc, ValueContext vc, PreparedStatement stmt) throws SQLException
	{
        if(parameters == null)
            return;

		StatementParameter.ApplyContext ac = new StatementParameter.ApplyContext(this);
		int paramsCount = parameters.length;
		for(int i = 0; i < paramsCount; i++)
		{
			parameters[i].apply(ac, dc, vc, stmt);
		}
	}

	public void importFromXml(XmlSource xs, Element stmtElem, String pkgName, String pkgDataSourceId)
	{
		this.pkgName = pkgName;
		this.stmtElem = stmtElem;
		stmtName = stmtElem.getAttribute("name");
		sql = stmtElem.getFirstChild().getNodeValue();
		if(sql.indexOf(REPLACEMENT_PREFIX) != -1)
			sqlIsDynamic = true;

        ArrayList paramElems = new ArrayList();

		String dataSourceId = stmtElem.getAttribute("data-src");
		if(dataSourceId.length() == 0 && pkgDataSourceId != null)
		{
			setDataSource(pkgDataSourceId);
		}
        else
        {
            setDataSource(dataSourceId);
        }

		NodeList stmtChildren = stmtElem.getChildNodes();
		for(int ch = 0; ch < stmtChildren.getLength(); ch++)
		{
			Node stmtChild = stmtChildren.item(ch);
			if(stmtChild.getNodeType() != Node.ELEMENT_NODE)
				continue;
            String childName = stmtChild.getNodeName();
            if(childName.equals("sql"))
            {
                sql = stmtChild.getNodeValue();
                if(sql.indexOf(REPLACEMENT_PREFIX) != -1)
                    sqlIsDynamic = true;
            }
			else if(childName.equals("report"))
			{
				Element reportElem = (Element) stmtChild;
                if(xs != null) xs.processTemplates(reportElem);
				String reportName = reportElem.getAttribute("name");
				if(reportName.length() == 0)
					defaultReportElem = reportElem;
				else
				{
					if(reportElems == null) reportElems = new HashMap();
					reportElems.put(reportName, reportElem);
				}
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
            parameters = new StatementParameter[paramElemsCount];

            for(int p = 0; p < paramElemsCount; p++)
            {
                Element paramElem = (Element) paramElems.get(p);
				parameters[p] = new StatementParameter(this, p, paramElem);
            }
        }
	}

	public String getDebugHtml(ValueContext vc)
	{
		StringBuffer html = new StringBuffer();
		html.append("<pre>");
		html.append(getSql(vc));
		html.append("</pre>");
		if(parameters != null)
		{
			html.append("<p>Bind Parameters:<ol>");
			int paramsCount = parameters.length;
			for(int i = 0; i < paramsCount; i++)
			{
				parameters[i].appendDebugHtml(html, vc);
			}
		}
		html.append("<p>");
		return html.toString();
	}
}

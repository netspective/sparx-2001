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
 * $Id: StatementInfo.java,v 1.8 2002-12-26 19:26:33 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.apache.oro.text.perl.Perl5Util;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.StaticValue;
import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.util.log.LogManager;
import com.netspective.sparx.util.ClassPath;
import com.netspective.sparx.xaf.html.SyntaxHighlight;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.field.IntegerField;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.StandardReport;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.ace.page.DatabaseSqlPage;

import javax.naming.NamingException;

public class StatementInfo
{
    public final static String REPLACEMENT_PREFIX = "${";
    public final static String LISTPARAM_PREFIX = "param-list:";
    public final static String REQ_ATTR_NAME_DEBUG_APPLY_CONTEXT = "debug-apply-context";

    static public class ResultInfo extends com.netspective.sparx.xaf.sql.ResultInfo
    {
        private StatementInfo si;
        private StatementExecutionLogEntry logEntry;

        ResultInfo(ValueContext vc, Connection conn, StatementInfo si, Statement stmt, StatementExecutionLogEntry logEntry) throws SQLException
        {
            super(vc, conn, stmt);
            this.si = si;
            this.logEntry = logEntry;
        }

        public String getSQL(ValueContext vc)
        {
            return si.getSql(vc);
        }

        public StatementInfo getStatementInfo()
        {
            return si;
        }

        public Element getStmtElement()
        {
            return si.getStatementElement();
        }

        public StatementExecutionLogEntry getLogEntry()
        {
            return logEntry;
        }

        public void produceReport(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, Object[] params, String reportId, String[] urlFormats) throws StatementNotFoundException, NamingException, SQLException, IOException
        {
            ResultSet rs = getResultSet();

            Element reportElem = si.getReportElement(reportId);
            if(reportElem == null && reportId != null)
                writer.write("Report id '" + reportId + "' not found for statement '" + si.getId() + "'");

            Report rd = si.createReport(reportElem);
            if(vc instanceof TaskContext)
                rd.setCanvas(((TaskContext) vc).getCanvas());

            rd.initialize(rs, reportElem);

            ReportContext rc = new ReportContext(vc, rd, skin);
            if(urlFormats != null)
            {
                ReportContext.ColumnState[] state = rc.getStates();
                for(int i = 0; i < urlFormats.length; i++)
                    state[i].setUrl(urlFormats[i]);
            }

            rc.produceReport(writer, rs);
            close();
        }

        public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, ReportSkin skin, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
        {
            ResultSet rs = getResultSet();

            // get the ResultSet into a matrix so that we can stash it away later
            // use the matrix to produce the report and do the storage so we don't have to run the query multiple times

            Object[][] data = StatementManager.getResultSetRowsAsMatrix(rs);
            vs.setValue(vc, rs.getMetaData(), data, storeType);

            Element reportElem = si.getReportElement(reportId);
            if(reportElem == null && reportId != null)
                writer.write("Report id '" + reportId + "' not found for statement '" + si.getId() + "'");

            Report rd = si.createReport(reportElem);
            if(vc instanceof TaskContext)
                rd.setCanvas(((TaskContext) vc).getCanvas());

            rd.initialize(rs, reportElem);

            ReportContext rc = new ReportContext(vc, rd, skin);
            rc.produceReport(writer, data);
            close();
        }
    }

    private String pkgName;
    private String stmtName;
    private SingleValueSource dataSourceValueSource;
    private Element stmtElem;
    private boolean sqlIsDynamic;
    private String sql;
    private int sqlMaxLineSize;
    private StatementParameter[] parameters;
    private Element defaultReportElem;
    private Report defaultReport;
    private Map reportElems;
    private Map reports;
    private StatementExecutionLog execLog = new StatementExecutionLog();
    private StatementDialog dialog;

    public StatementInfo()
    {
    }

    public StatementInfo(String sql)
    {
        this.pkgName = "dynamic";
        this.stmtName = "stmt_" + this.toString();
        setSql(sql);
    }

    private void setSql(String sql)
    {
        this.sql = sql;
        if(sql.indexOf(REPLACEMENT_PREFIX) != -1)
            sqlIsDynamic = true;

        /*
         * if the entire SQL string is indented, find out how far the first line is indented
         */
        StringBuffer replStr = new StringBuffer();
        for(int i = 0; i < sql.length(); i++)
        {
            char ch = sql.charAt(i);
            if(Character.isWhitespace(ch))
                replStr.append(ch);
            else
                break;
        }

        /*
         * If the first line is indented, unindent all the lines the distance of just the first line
         */
        Perl5Util perlUtil = new Perl5Util();

        if(replStr.length() > 0)
            this.sql = perlUtil.substitute("s/" + replStr + "/\n/g", sql);

        this.sql = this.sql.trim();

        if(stmtElem != null)
        {
            Document doc = stmtElem.getOwnerDocument();
            Element sqlText = doc.createElement("sql-html");
            StringWriter highlSql = new StringWriter();
            try
            {
                SyntaxHighlight.emitHtml("sql", new StringReader(this.sql), highlSql);
            }
            catch (Exception e)
            {
                highlSql.write(e.getMessage());
            }
            sqlText.appendChild(doc.createTextNode(highlSql.toString()));
            stmtElem.appendChild(sqlText);
        }

        sqlMaxLineSize = 0;
        StringTokenizer st = new StringTokenizer(this.sql, "\n");
        while(st.hasMoreTokens())
        {
            String line = st.nextToken();
            if(line.length() > sqlMaxLineSize)
                sqlMaxLineSize = line.length();
        }
    }

    public String getPkgName()
    {
        return pkgName;
    }

    public String getStmtName()
    {
        return stmtName;
    }

    public String getId()
    {
        return pkgName != null ? (pkgName + "." + stmtName) : stmtName;
    }

    public Element getStatementElement()
    {
        return stmtElem;
    }

    public Map getReportElems()
    {
        return reportElems;
    }

    public Element getReportElement(String name)
    {
        return name == null ? defaultReportElem : (Element) reportElems.get(name);
    }

    public Report createReport(Element reportElem)
    {
        if(reportElem != null && defaultReportElem == reportElem)
            return defaultReport;

        if(reportElem == null)
            return new StandardReport();

        Report result = (Report) reports.get(reportElem);
        if(result == null)
        {
            ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(reportElem.getAttribute("class"), StandardReport.class, true);
            result = (Report) instanceGen.getInstance();
            reports.put(reportElem, result);
        }
        return result;
    }

    public SingleValueSource getDataSource()
    {
        return dataSourceValueSource;
    }

    public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public StatementParameter[] getParams()
    {
        return parameters;
    }

    public StatementExecutionLog getExecutionLog()
    {
        return execLog;
    }

    public StatementExecutionLogEntry createNewExecLogEntry(ValueContext vc)
    {
        return execLog.createNewEntry(vc, this);
    }

    public String getSql(ValueContext vc)
    {
        if(!sqlIsDynamic)
            return sql;
        else
            return formatSql(vc);
    }

    public void createDefaultDialog()
    {
        dialog = new StatementDialog(this, null, null, null);
        dialog.setName("statement_" + XmlSource.xmlTextToJavaIdentifier(getId(), false));
        dialog.setHeading("Test " + getId());

        /*
        MemoField sqlField = new MemoField("sql", "SQL", sqlMaxLineSize > 0 ? sqlMaxLineSize+20 : 60, sqlLinesCount > 0 ? sqlLinesCount+1 : 10, 4096);
        sqlField.setDefaultValue(new StaticValue(sql));
        dialog.addField(sqlField);
        */

        if(parameters != null)
        {
            for(int i = 0; i < parameters.length; i++)
            {
                DialogField field = parameters[i].getDialogField();
                dialog.addField(field);
            }
        }

        IntegerField rowsPerPageField = new IntegerField("rows_per_page", "Rows per page");
        rowsPerPageField.setDefaultValue(new StaticValue("10"));
        dialog.addField(rowsPerPageField);

        dialog.setDirector(new DialogDirector());
    }

    public StatementDialog getDialog()
    {
        if(dialog == null)
            createDefaultDialog();
        return dialog;
    }

    /** Replace ${xxx} values
     */
    public String formatSql(ValueContext vc)
    {
        StringBuffer sb = new StringBuffer();
        int prev = 0;

        int pos;
        while((pos = sql.indexOf("$", prev)) >= 0)
        {
            if(pos > 0)
            {
                sb.append(sql.substring(prev, pos));
            }
            if(pos == (sql.length() - 1))
            {
                sb.append('$');
                prev = pos + 1;
            }
            else if(sql.charAt(pos + 1) != '{')
            {
                sb.append(sql.charAt(pos + 1));
                prev = pos + 2;
            }
            else
            {
                int endName = sql.indexOf('}', pos);
                if(endName < 0)
                    throw new RuntimeException("Statement '"+ getId() +"' has syntax error in sql: " + sql);

                String expression = sql.substring(pos + 2, endName);

                if(expression.startsWith(LISTPARAM_PREFIX)) // format is param:# 12 below is length of "param:"
                {
                    try
                    {
                        int paramNum = Integer.parseInt(expression.substring(LISTPARAM_PREFIX.length()));
                        if(paramNum >= 0 && paramNum < parameters.length)
                        {
                            StatementParameter param = parameters[paramNum];
                            if(!param.isListType())
                                throw new RuntimeException("Statement '"+ getId() +"': only list parameters may be specified here (param '" + paramNum + "')");

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
                            throw new RuntimeException("Statement '"+ getId() +"': parameter '" + paramNum + "' does not exist");
                    }
                    catch(Exception e)
                    {
                        sb.append("##" + e.toString() + "##");
                    }
                }
                else
                {
                    SingleValueSource svs = ValueSourceFactory.getSingleValueSource(expression);
                    if(svs == null)
                        throw new RuntimeException("Statement '"+ getId() +"': single value source expected for '"+ expression +"', but none found");
                    sb.append(svs.getValue(vc));
                }

                prev = endName + 1;
            }
        }

        if(prev < sql.length()) sb.append(sql.substring(prev));
        return sb.toString();
    }

    public void applyParams(DatabaseContext dc, ValueContext vc, PreparedStatement stmt) throws SQLException
    {
        if(parameters == null)
            return;

        if(vc instanceof DialogContext && (((DialogContext) vc).getDialog() instanceof StatementDialog) && DatabaseSqlPage.useDialogParams(vc))
        {
            // we're probably running a StatementInfo unit test dialog so get the values from the DialogContext
            StatementParameter.ApplyContext ac = new StatementParameter.ApplyContext(this, true);
            int paramsCount = parameters.length;
            for(int i = 0; i < paramsCount; i++)
            {
                parameters[i].apply(ac, (DialogContext) vc, stmt);
            }
            vc.getRequest().setAttribute(REQ_ATTR_NAME_DEBUG_APPLY_CONTEXT, ac);
        }
        else
        {
            StatementParameter.ApplyContext ac = new StatementParameter.ApplyContext(this, false);
            int paramsCount = parameters.length;
            for(int i = 0; i < paramsCount; i++)
            {
                parameters[i].apply(ac, dc, vc, stmt);
            }
        }
    }

    public void importFromXml(XmlSource xs, Element stmtElem, String pkgName, String pkgDataSourceId)
    {
        if(xs != null)
            xs.processTemplates(stmtElem);

        this.pkgName = pkgName;
        this.stmtElem = stmtElem;
        stmtName = stmtElem.getAttribute("name");
        setSql(stmtElem.getFirstChild().getNodeValue());

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
            if(childName.equals("report"))
            {
                Element reportElem = (Element) stmtChild;
                if(xs != null) xs.processTemplates(reportElem);
                String reportName = reportElem.getAttribute("name");
                if(reportName.length() == 0)
                {
                    defaultReportElem = reportElem;
                    ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(reportElem.getAttribute("class"), StandardReport.class, true);
                    defaultReport = (Report) instanceGen.getInstance();
                }
                else
                {
                    if(reportElems == null)
                    {
                        reportElems = new HashMap();
                        reports = new HashMap();
                    }
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
                parameters[p] = new StatementParameter(this, p);
                parameters[p].importFromXml(paramElem);
            }
        }

        stmtElem.setAttribute("qualified-name", getId());
        stmtElem.setAttribute("package", pkgName);
    }

    public String getDebugHtml(ValueContext vc, boolean showHeading, boolean showId, String exceptionMsg)
    {
        StringBuffer html = new StringBuffer();
        html.append("<table cellspacing=1 border=0 bgcolor=#EEEEEE cellpadding=4>");
        if(showHeading && exceptionMsg != null)
            html.append("<tr valign=top bgcolor=white><td colspan=2 align=center><font color=red><b>Sparx SQL Statement Exception</b></font></td></tr>");
        else if (showHeading)
            html.append("<tr valign=top bgcolor=white><td colspan=2 align=center>Sparx SQL Statement</td></tr>");
        if(showId)
            html.append("<tr valign=top bgcolor=white><td><nobr>Statement id</nobr></td><td><b><code>"+ getId() +"</code></b></td></tr>");
        html.append("<tr valign=top bgcolor=white><td>SQL</td><td>");
        StringWriter highlSql = new StringWriter();
        try
        {
            SyntaxHighlight.emitHtml("sql", new StringReader(this.sql), highlSql);
        }
        catch (Exception e)
        {
            highlSql.write(e.getMessage());
        }

        html.append(highlSql.toString());
        html.append("</td></tr>");

        html.append("<tr valign=top bgcolor=white><td>Bind Parameters</td><td>");
        if(parameters != null)
        {
            html.append("<ol>");
            StatementParameter.ApplyContext debugAC = (StatementParameter.ApplyContext) vc.getRequest().getAttribute(REQ_ATTR_NAME_DEBUG_APPLY_CONTEXT);
            if(debugAC != null)
            {
                Object[] bindValues = debugAC.getDebugBindValues();
                for(int i = 0; i < bindValues.length; i++)
                {
                    Object value = bindValues[i];
                    html.append("<li>" + value.getClass().getName() + " = " + value + "</li>");
                }
            }
            else
            {
                int paramsCount = parameters.length;
                for(int i = 0; i < paramsCount; i++)
                {
                    parameters[i].appendDebugHtml(html, vc);
                }
            }
            html.append("</ol>");
        }
        else
            html.append("None");
        html.append("</td></tr>");
        if(exceptionMsg != null)
        {
            html.append("<tr valign=top bgcolor=white><td>Exception</td><td><pre>");
            html.append(exceptionMsg);
            html.append("</pre></td></tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    public String createExceptionMessage(ValueContext vc)
    {
        StringBuffer text = new StringBuffer();

        text.append("Statement id = ");
        text.append(getId());
        text.append("\n");
        text.append(getSql(vc));
        text.append("\n");
        if(parameters != null)
        {
            text.append("\nBind Parameters:\n");
            StatementParameter.ApplyContext debugAC = (StatementParameter.ApplyContext) vc.getRequest().getAttribute(REQ_ATTR_NAME_DEBUG_APPLY_CONTEXT);
            if(debugAC != null)
            {
                Object[] bindValues = debugAC.getDebugBindValues();
                for(int i = 0; i < bindValues.length; i++)
                {
                    Object value = bindValues[i];
                    text.append(value.getClass().getName() + " = " + value);
                }
            }
            else
            {
                int paramsCount = parameters.length;
                for(int i = 0; i < paramsCount; i++)
                {
                    parameters[i].appendExceptionText(text, vc);
                }
            }
            text.append("\n");
        }
        return text.toString();
    }

    public ResultInfo execute(DatabaseContext dc, ValueContext vc, String dataSourceId, Object[] params, boolean scrollable) throws NamingException, SQLException
    {
        if(dataSourceId == null)
        {
            dataSourceId = getDataSource() != null ? getDataSource().getValue(vc) : null;
        }

        StatementExecutionLogEntry logEntry = createNewExecLogEntry(vc);

        try
        {
            logEntry.registerGetConnectionBegin();
            Connection conn = dc.getConnection(vc, dataSourceId);
            logEntry.registerGetConnectionEnd(conn);
            PreparedStatement stmt = null;
            String sql = getSql(vc);
            if (scrollable)
            {
                int rsType = dc.getScrollableResultSetType(conn);
                stmt = (rsType == DatabaseContext.RESULTSET_NOT_SCROLLABLE ?
                        conn.prepareStatement(sql) :
                        conn.prepareStatement(sql, rsType, ResultSet.CONCUR_READ_ONLY));
            }
            else
            {
                stmt = conn.prepareStatement(sql);
            }

            logEntry.registerBindParamsBegin();
            if(params != null)
            {
                for(int i = 0; i < params.length; i++)
                    stmt.setObject(i + 1, params[i]);
            }
            else
                applyParams(dc, vc, stmt);
            logEntry.registerBindParamsEnd();

            logEntry.registerExecSqlBegin();
            stmt.execute();
            logEntry.registerExecSqlEndSuccess();
            return new ResultInfo(vc, conn, this, stmt, logEntry);
        }
        catch(SQLException e)
        {
            logEntry.registerExecSqlEndFailed();
            LogManager.recordException(this.getClass(), "execute using dbc", createExceptionMessage(vc), e);
            throw e;
        }
        finally
        {
            logEntry.finalize(vc);
        }
    }

    public ResultInfo execute(DatabaseContext dc, ValueContext vc, String dataSourceId, Object[] params) throws NamingException, SQLException
    {
        return execute(dc, vc, dataSourceId, params, false);
    }

    public ResultInfo execute(ConnectionContext cc, ValueContext vc, Object[] params) throws NamingException, SQLException
    {
        StatementExecutionLogEntry logEntry = createNewExecLogEntry(vc);

        try
        {
            logEntry.registerGetConnectionBegin();
            Connection conn = cc.getConnection();
            logEntry.registerGetConnectionEnd(conn);

            PreparedStatement stmt = conn.prepareStatement(getSql(vc));

            logEntry.registerBindParamsBegin();
            if(params != null)
            {
                for(int i = 0; i < params.length; i++)
                    stmt.setObject(i + 1, params[i]);
            }
            else
                applyParams(cc.getDatabaseContext(), vc, stmt);
            logEntry.registerBindParamsEnd();

            logEntry.registerExecSqlBegin();
            stmt.execute();
            logEntry.registerExecSqlEndSuccess();
            return new ResultInfo(vc, conn, this, stmt, logEntry);
        }
        catch(SQLException e)
        {
            logEntry.registerExecSqlEndFailed();
            LogManager.recordException(this.getClass(), "execute using cc", createExceptionMessage(vc), e);
            throw e;
        }
        finally
        {
            logEntry.finalize(vc);
        }
    }

    public ResultInfo executeAndStore(DatabaseContext dc, ValueContext vc, String dataSourceId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException
    {
        ResultInfo ri = execute(dc, vc, dataSourceId, null);
        ResultSet rs = ri.getResultSet();
        vs.setValue(vc, rs, storeType);
        if(storeType != SingleValueSource.RESULTSET_STORETYPE_RESULTSET)
            ri.close();
        return ri;
    }

    public void produceReport(Writer writer, DatabaseContext dc, ValueContext vc, String dataSourceId, ReportSkin skin, Object[] params, String reportId, String[] urlFormats) throws StatementNotFoundException, NamingException, SQLException, IOException
    {
        StatementInfo.ResultInfo ri = execute(dc, vc, dataSourceId, params);
        if(ri != null)
            ri.produceReport(writer, dc, vc, skin, params, reportId, urlFormats);
    }

    public void produceReportAndStoreResultSet(Writer writer, DatabaseContext dc, ValueContext vc, String dataSourceId, ReportSkin skin, Object[] params, String reportId, SingleValueSource vs, int storeType) throws StatementNotFoundException, NamingException, SQLException, IOException
    {
        StatementInfo.ResultInfo ri = execute(dc, vc, dataSourceId, params);
        if(ri != null)
            ri.produceReportAndStoreResultSet(writer, dc, vc, skin, params, reportId, vs, storeType);
    }
}

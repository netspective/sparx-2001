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
 * $Id: QuerySelectDialogTag.java,v 1.4 2003-02-24 03:46:05 aye.thu Exp $
 */

package com.netspective.sparx.xaf.taglib;

import com.netspective.sparx.xaf.querydefn.QueryBuilderDialog;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ServletValueContext;

public class QuerySelectDialogTag extends javax.servlet.jsp.tagext.TagSupport
{
    private String name;
    private String source;
    private String dialogSkinName;
    private String reportSkinName;

    public void release()
    {
        super.release();
        name = null;
        source = null;
        dialogSkinName = null;
    }

    public void setName(String value)
    {
        name = value;
    }

    public void setSkin(String value)
    {
        dialogSkinName = value;
    }

    public void setReportSkin(String value)
    {
        reportSkinName = value;
    }

    public void setSource(String value)
    {
        source = value;
    }

    public int doStartTag() throws javax.servlet.jsp.JspException
    {
        try
        {
            javax.servlet.jsp.JspWriter out = pageContext.getOut();
            javax.servlet.ServletContext context = pageContext.getServletContext();

            com.netspective.sparx.xaf.sql.StatementManager manager = com.netspective.sparx.xaf.sql.StatementManagerFactory.getManager(context);
            if(manager == null)
            {
                out.write("StatementManager not found in ServletContext");
                return SKIP_BODY;
            }

            // this is here so that the default skin for the dialogs will be loaded
            // if it is over written through XML
            com.netspective.sparx.xaf.form.DialogManager dialogmanager = com.netspective.sparx.xaf.form.DialogManagerFactory.getManager(context);
            if(dialogmanager == null)
            {
                out.write("DialogManager not found in ServletContext");
                return SKIP_BODY;
            }

            com.netspective.sparx.xaf.querydefn.QueryDefinition queryDefn = manager.getQueryDefn(context, null, source);
            if(queryDefn == null)
            {
                out.write("QueryDefinition '" + source + "' not found in StatementManager");
                return SKIP_BODY;
            }

            com.netspective.sparx.xaf.querydefn.QuerySelectDialog dialog = queryDefn.getSelectDialog(name);
            if(dialog == null)
            {
                out.write("QuerySelectDialog '" + name + "' not found in QueryDefinition '" + source + "'");
                return SKIP_BODY;
            }
            ValueContext vc = new ServletValueContext(context, (javax.servlet.Servlet) pageContext.getPage(),
                    pageContext.getRequest(), pageContext.getResponse());

            com.netspective.sparx.xaf.form.DialogSkin skin =
                    dialogSkinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(context, dialogSkinName);
            if(skin == null)
            {
                out.write("DialogSkin '" + dialogSkinName + "' not found in skin factory.");
                return SKIP_BODY;
            }

            com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(pageContext.getServletContext(), (javax.servlet.Servlet) pageContext.getPage(), (javax.servlet.http.HttpServletRequest) pageContext.getRequest(), (javax.servlet.http.HttpServletResponse) pageContext.getResponse(), skin);
            dialog.prepareContext(dc);

            if(reportSkinName != null)
                dc.setValue(QueryBuilderDialog.QBDIALOG_REPORT_SKIN_FIELD_NAME, reportSkinName);

            dialog.renderHtml(out, dc, true);
            return SKIP_BODY;
        }
        catch(java.io.IOException e)
        {
            throw new javax.servlet.jsp.JspException(e.toString());
        }
    }

    public int doEndTag() throws javax.servlet.jsp.JspException
    {
        return EVAL_PAGE;
    }
}

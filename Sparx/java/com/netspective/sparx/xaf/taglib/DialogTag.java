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
 * $Id: DialogTag.java,v 1.2 2002-12-15 18:03:18 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.taglib;

public class DialogTag extends javax.servlet.jsp.tagext.TagSupport
{
    private String name;
    private String source;
    private String skinName;

    public void release()
    {
        super.release();
        name = null;
        source = null;
        skinName = null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String value)
    {
        name = value;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String value)
    {
        source = value;
    }

    public String getSkin()
    {
        return skinName;
    }

    public void setSkin(String value)
    {
        skinName = value;
    }

    public int doStartTag() throws javax.servlet.jsp.JspException
    {
        try
        {
            javax.servlet.jsp.JspWriter out = pageContext.getOut();
            javax.servlet.ServletContext context = pageContext.getServletContext();
            com.netspective.sparx.xaf.form.DialogManager manager;
            if(source == null)
            {
                manager = com.netspective.sparx.xaf.form.DialogManagerFactory.getManager(context);
                if(manager == null)
                {
                    out.write("DialogManager not found in ServletContext");
                    return SKIP_BODY;
                }
            }
            else
            {
                manager = com.netspective.sparx.xaf.form.DialogManagerFactory.getManager(source);
                if(manager == null)
                {
                    out.write("DialogManager '" + source + "' not found.");
                    return SKIP_BODY;
                }
            }

            com.netspective.sparx.xaf.form.Dialog dialog = manager.getDialog(pageContext.getServletContext(), name);
            if(dialog == null)
            {
                out.write("Dialog '" + name + "' not found in manager '" + manager + "'.");
                return SKIP_BODY;
            }

            com.netspective.sparx.xaf.form.DialogSkin skin = skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName);
            if(skin == null)
            {
                out.write("DialogSkin '" + skinName + "' not found in skin factory.");
                return SKIP_BODY;
            }

            com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(context, (javax.servlet.Servlet) pageContext.getPage(), (javax.servlet.http.HttpServletRequest) pageContext.getRequest(), (javax.servlet.http.HttpServletResponse) pageContext.getResponse(), skin);
            dialog.prepareContext(dc);

            // if the dialog class has not been overridden (base class) and
            // there are no execut tasks then we will handle the "execute" portion
            if(dc.inExecuteMode())
            {
                dialog.execute(out, dc);
                if(!dc.executeStageHandled())
                {
                    // these two attributes are set because they are defined by
                    // the DialogTagTEI so that the nested body (the "execute" portion
                    // of the dialog) has full access to the dialog that was created
                    // as well as the context it's running in

                    pageContext.setAttribute("dialog", dialog);
                    pageContext.setAttribute("dialogContext", dc);
                    return EVAL_BODY_INCLUDE;
                }
                else
                {
                    return SKIP_BODY;
                }
            }
            else
            {
                dialog.renderHtml(out, dc, true);
                return SKIP_BODY;
            }
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
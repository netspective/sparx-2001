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
 * $Id: QuerySelectDialog.java,v 1.2 2002-10-03 14:54:55 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.field.BooleanField;

public class QuerySelectDialog extends QueryBuilderDialog
{
    static public final String QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME = "queryDefnName";
    static public final String QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME = "queryDefnSelectDialogName";

    private QuerySelect select;

    public QuerySelectDialog(QueryDefinition queryDefn)
    {
        setQueryDefn(queryDefn);
        setLoopEntries(true);
        setFlag(DLGFLAG_READONLY_FIELDS_HIDDEN_UNLESS_HAVE_DATA | DLGFLAG_HIDE_HEADING_IN_EXEC_MODE | QBDLGFLAG_ALWAYS_SHOW_RSNAV);
    }

    public QuerySelect createSelect(DialogContext dc)
    {
        return this.select;
    }

    public void importFromXml(String packageName, Element elem)
    {
        String dialogId = elem.getAttribute("name");
        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) node;
            String childName = node.getNodeName();
            if(childName.startsWith(DialogField.FIELDTAGPREFIX))
            {
                String condFieldId = childElem.getAttribute("query-field");
                if(condFieldId.length() > 0)
                {
                    QueryField condField = getQueryDefn().getField(condFieldId);
                    if(condField == null)
                        throw new RuntimeException("query-field '" + condFieldId + "' in QuerySelectDialog '" + dialogId + "' does not exist");

                    String fieldName = childElem.getAttribute("name");
                    if(fieldName.length() == 0)
                        childElem.setAttribute("name", condFieldId);

                    String childCaption = childElem.getAttribute("caption");
                    if(childCaption.length() == 0)
                        childElem.setAttribute("caption", condField.getCaption());
                }
            }
            else if(childName.equals("select"))
            {
                select = new QuerySelect(getQueryDefn());
                select.importFromXml(childElem);
            }
        }

        if(select == null)
            throw new RuntimeException("'select' child element is required for dialogs in QuerySelectDialog");

        // now we've got all the QueryDefinition-specific information all setup
        // so now we just do a normal dialog initialization from an XML resource

        super.importFromXml(packageName, elem);

        DialogField hiddenName = new DialogField(QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, null);
        hiddenName.setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);
        addField(hiddenName);

        hiddenName = new DialogField(QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME, null);
        hiddenName.setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);
        addField(hiddenName);

        addOutputDestinationFields();

        if(flagIsSet(QBDLGFLAG_ALLOW_DEBUG))
        {
            DialogField options = new DialogField();
            options.setSimpleName("options");
            options.setFlag(DialogField.FLDFLAG_SHOWCAPTIONASCHILD);
            options.addChildField(new BooleanField("debug", "Debug", BooleanField.BOOLSTYLE_CHECK, 0));
            addField(options);
        }

        boolean foundNavigator = false;
        List fields = getFields();
        for(Iterator i = fields.iterator(); i.hasNext();)
        {
            if(i.next() instanceof ResultSetNavigatorButtonsField)
            {
                foundNavigator = true;
                break;
            }
        }
        if(!foundNavigator)
            addField(new ResultSetNavigatorButtonsField());
    }

    public void makeStateChanges(DialogContext dc, int stage)
    {
        Iterator k = this.getFields().iterator();
        while(k.hasNext())
        {
            DialogField field = (DialogField) k.next();
            field.makeStateChanges(dc, stage);
        }

        dc.setValue(QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, getQueryDefn().getName());
        dc.setValue(QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME, getName());
        if(dc.inExecuteMode() && stage == DialogContext.STATECALCSTAGE_FINAL)
        {
            List fields = this.getFields();
            int flag = flagIsSet(QBDLGFLAG_HIDE_CRITERIA) ? DialogField.FLDFLAG_INVISIBLE : DialogField.FLDFLAG_READONLY;
            for(int i = 0; i < fields.size(); i++)
                dc.setFlag(((DialogField) fields.get(i)).getQualifiedName(), flag);

            dc.setFlag("output", DialogField.FLDFLAG_INVISIBLE);
            if(flagIsSet(QBDLGFLAG_ALLOW_DEBUG))
                dc.setFlag("options", DialogField.FLDFLAG_INVISIBLE);

            dc.setFlag("director", DialogField.FLDFLAG_INVISIBLE);
            dc.clearFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);
        }
        else
        {
            /*
			List fields = this.getFields();
			for(int i = 0; i < fields.size(); i++)
				dc.clearFlag(((DialogField) fields.get(i)).getQualifiedName(), DialogField.FLDFLAG_READONLY);
			*/

            if(flagIsSet(QBDLGFLAG_HIDE_OUTPUT_DESTS))
                dc.setFlag("output", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);
        }
    }
}
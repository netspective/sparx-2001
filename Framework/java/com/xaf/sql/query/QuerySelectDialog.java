package com.xaf.sql.query;

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.http.*;
import org.w3c.dom.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.report.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class QuerySelectDialog extends QueryBuilderDialog
{
	static public final String QSDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME = "queryDefnName";
	static public final String QSDIALOG_DIALOG_NAME_PASSTHRU_FIELDNAME = "queryDefnSelectDialogName";

	private QuerySelect select;

    public QuerySelectDialog(QueryDefinition queryDefn)
    {
		setQueryDefn(queryDefn);
		setLoopEntries(true);
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
						throw new RuntimeException("query-field '"+condFieldId+"' in QuerySelectDialog '"+dialogId+"' does not exist");

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
        for(Iterator i = fields.iterator(); i.hasNext(); )
        {
            if(i.next() instanceof ResultSetNavigatorButtonsField)
            {
                foundNavigator = true;
                break;
            }
        }
        if(! foundNavigator)
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
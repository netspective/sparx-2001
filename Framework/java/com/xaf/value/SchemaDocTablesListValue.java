package com.xaf.value;

import java.util.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

public class SchemaDocTablesListValue extends ListSource
{
    public void initializeSource(String srcParams)
    {
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
		SchemaDocument schemaDoc = SchemaDocFactory.getDoc(vc.getServletContext());
		if(schemaDoc == null)
		{
			choices.add(new SelectChoice("Default schema document not found in ServletContext init parameter 'schema.file'"));
			return choices;
		}

		try
		{
			String[] tableNames = schemaDoc.getTableNames(false);
			for(int tn = 0; tn < tableNames.length; tn++)
			{
				choices.add(new SelectChoice(tableNames[tn]));
			}
		}
		catch(Exception e)
		{
			choices.add(new SelectChoice(e.toString()));
		}

        return choices;
	}
}
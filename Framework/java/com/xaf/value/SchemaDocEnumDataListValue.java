package com.xaf.value;

import java.util.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

public class SchemaDocEnumDataListValue extends ListSource
{
    private String enumTableName;

    public String getEnumTableName()
    {
        return enumTableName;
    }

    public void setEnumTableName(String enumTableName)
    {
        this.enumTableName = enumTableName;
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
        enumTableName = srcParams;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SchemaDocument schemaDoc = SchemaDocFactory.getDoc(vc.getServletContext());
		if(schemaDoc == null)
		{
            SelectChoicesList choices = new SelectChoicesList();
			choices.add(new SelectChoice("Default schema document not found in ServletContext init parameter 'schema.file'"));
			return choices;
		}

        return schemaDoc.getEnumTableData(enumTableName);
	}
}
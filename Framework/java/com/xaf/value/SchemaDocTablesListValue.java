package com.xaf.value;

import java.util.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;

public class SchemaDocTablesListValue extends ListSource
{
    static public Perl5Util perlUtil = new Perl5Util();

    private String filter;

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
        filter = "/" + srcParams + "/";
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

        String[] tableNames = schemaDoc.getTableNames(false);
        for(int tn = 0; tn < tableNames.length; tn++)
        {
            String tableName = tableNames[tn];
            try
            {
                if(perlUtil.match(filter, tableName))
                    choices.add(new SelectChoice(tableName));
            }
            catch (MalformedPerl5PatternException e)
            {
                choices.add(new SelectChoice(e.toString()));
            }
        }

        return choices;
	}
}
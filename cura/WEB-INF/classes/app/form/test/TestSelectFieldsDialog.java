package app.form.test;

import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;

public class TestSelectFieldsDialog extends Dialog
{
    public void populateValues(DialogContext dc, int formatType)
    {
        super.populateValues(dc, formatType);
        dc.setValues("sel_field_multidual", new String[] {"NEWA", "NEWB"} );
    }

    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        String[] values = dc.getValues("sel_field_multidual");
        if(values != null)
        {
            for(int i = 0; i < values.length; i++)
            {
                writer.write(values[i]);
                writer.write("<br>");
            }
        }

        app.form.context.Test.DialogTest03Context sfdc = (app.form.context.Test.DialogTest03Context) dc;
        values = sfdc.getSelFieldMultidual();
        if(values != null)
        {
            for(int i = 0; i < values.length; i++)
            {
                writer.write(values[i]);
                writer.write("<br>");
            }
        }
    }
}
/*
 * Description: tutorial.dialog.HelloFirstDialog
 * @author SJaveed
 * @created Apr 11, 2002 15:16
 * @version
 */
package app.form;

import com.netspective.sparx.xaf.form.DialogContext;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class HelloFirstDialog extends com.netspective.sparx.xaf.form.Dialog
{
    /**
     * This dialog greets the user once the user enters a valid name.  The method used to process and
     * respond to the dialog is called execute.
     */
    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks>
        // in the XML; leave it out to override

        // super.execute(writer, dc);
        String personName = "";
        String returnValue = "";

		personName = dc.getValue("personName");

		returnValue = "<b>Hello <i>" + personName + "</i>!!</b>  I'm so glad to finally be introduced to you!";

		writer.write(returnValue);

		// this is necessary to let the framework know we handled the execute
		dc.setExecuteStageHandled(true);
    }
}

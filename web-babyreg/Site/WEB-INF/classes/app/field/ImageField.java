package app.field;

import com.netspective.sparx.xaf.form.field.*;
import com.netspective.sparx.xaf.form.*;
import javax.servlet.http.*;

public class ImageField extends DialogField
{
    public ImageField()
    {
    }

	public ImageField(String aName, String aCaption)
	{
		super(aName, aCaption);
	}

	//public String getControlHtml(DialogContext dc)
	public void renderControlHtml(java.io.Writer writer,
                              DialogContext dc) throws java.io.IOException
	{
		String resourcesUrl = ((HttpServletRequest) dc.getRequest()).getContextPath() + "/resources";
		String value = dc.getValue(this);
		//return "<input type='hidden' name='"+ getId() +"' value=\"" + (value != null ? value : "") + "\"><img src='"+ value +"'>";
		writer.write("<center><img src='"+ resourcesUrl +"/images/"+ value +".jpg'></center>");
	}
}
package app.tag;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.navigate.*;
import com.netspective.sparx.xaf.security.*;
import com.netspective.sparx.xaf.skin.*;

public class AnchorTag extends TagSupport
{
	private String url;
	private String hint;

	public void release()
	{
		super.release();
		url = null;
		hint = null;
	}

	public final String getUrl() { return url; }
	public void setUrl(String value) { url = value; }

	public final String getHint() { return hint; }
	public void setHint(String value) { hint = value;	}

	public int doStartTag() throws JspException
	{
		JspWriter out = pageContext.getOut();

		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		String href = req.getContextPath() + url;

		try
		{
			out.print(hint == null ? "<a href='"+ href +"'>" : "<a href='"+ href +"' title='"+ hint +"'>");
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}

		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		try
		{
			out.print("</a>");
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}
		return EVAL_PAGE;
	}
}

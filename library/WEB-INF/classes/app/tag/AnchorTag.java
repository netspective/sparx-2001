package app.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

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

    public final String getUrl()
    {
        return url;
    }

    public void setUrl(String value)
    {
        url = value;
    }

    public final String getHint()
    {
        return hint;
    }

    public void setHint(String value)
    {
        hint = value;
    }

    public int doStartTag() throws JspException
    {
        JspWriter out = pageContext.getOut();

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        String href = req.getContextPath() + url;

        try
        {
            out.print(hint == null ? "<a href='" + href + "'>" : "<a href='" + href + "' title='" + hint + "'>");
        }
        catch (IOException e)
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
        catch (IOException e)
        {
            throw new JspException(e.toString());
        }
        return EVAL_PAGE;
    }
}

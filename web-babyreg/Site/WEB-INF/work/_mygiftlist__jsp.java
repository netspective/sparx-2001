/*
 * JSP generated by Resin 2.1.0 (built Tue Mar 26 14:12:50 PST 2002)
 */

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;

public class _mygiftlist__jsp extends com.caucho.jsp.JavaPage{
  private boolean _caucho_isDead;
  
  public void
  _jspService(javax.servlet.http.HttpServletRequest request,
              javax.servlet.http.HttpServletResponse response)
    throws java.io.IOException, javax.servlet.ServletException
  {
    com.caucho.jsp.QPageContext pageContext = (com.caucho.jsp.QPageContext) com.caucho.jsp.QJspFactory.create().getPageContext(this, request, response, null, true, 8192, true);
    javax.servlet.jsp.JspWriter out = (javax.servlet.jsp.JspWriter) pageContext.getOut();
    javax.servlet.ServletConfig config = getServletConfig();
    javax.servlet.Servlet page = this;
    javax.servlet.http.HttpSession session = pageContext.getSession();
    javax.servlet.ServletContext application = pageContext.getServletContext();
    response.setContentType("text/html");
    app.tag.PageTag _jsp_tag0 = null;
    com.netspective.sparx.xaf.taglib.StatementTag _jsp_tag1 = null;
    try {
      pageContext.write(_jsp_string0, 0, _jsp_string0.length);
      pageContext.write(_jsp_string1, 0, _jsp_string1.length);
      if (_jsp_tag0 == null) {
        _jsp_tag0 = new app.tag.PageTag();
        _jsp_tag0.setPageContext(pageContext);
        _jsp_tag0.setParent((javax.servlet.jsp.tagext.Tag) null);
          _jsp_tag0.setHeading("Welcome to Cura");
          _jsp_tag0.setTitle("Cura");
      }

      int _jspEval1 = _jsp_tag0.doStartTag();
      if (_jspEval1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        pageContext.write(_jsp_string2, 0, _jsp_string2.length);
        if (_jsp_tag1 == null) {
          _jsp_tag1 = new com.netspective.sparx.xaf.taglib.StatementTag();
          _jsp_tag1.setPageContext(pageContext);
          _jsp_tag1.setParent(_jsp_tag0);
            _jsp_tag1.setSkin("report");
            _jsp_tag1.setName("item.MyGiftList");
        }

        _jsp_tag1.doStartTag();
        int _jsp_endTagVar4 = _jsp_tag1.doEndTag();
        if (_jsp_endTagVar4 == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
          return;
        pageContext.write(_jsp_string3, 0, _jsp_string3.length);
      }
      int _jsp_endTagVar5 = _jsp_tag0.doEndTag();
      if (_jsp_endTagVar5 == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      pageContext.write(_jsp_string0, 0, _jsp_string0.length);
    } catch (java.lang.Throwable _jsp_e) {
      pageContext.handlePageException(_jsp_e);
    } finally {
      if (_jsp_tag0 != null)
        _jsp_tag0.release();
      if (_jsp_tag1 != null)
        _jsp_tag1.release();
      JspFactory.getDefaultFactory().releasePageContext(pageContext);
    }
  }

  private com.caucho.java.LineMap _caucho_line_map;
  private java.util.ArrayList _caucho_depends = new java.util.ArrayList();

  public boolean _caucho_isModified()
  {
    if (_caucho_isDead)
      return true;
    if (com.caucho.util.CauchoSystem.getVersionId() != 2057024142)
      return true;
    for (int i = _caucho_depends.size() - 1; i >= 0; i--) {
      com.caucho.vfs.Depend depend;
      depend = (com.caucho.vfs.Depend) _caucho_depends.get(i);
      if (depend.isModified())
        return true;
    }
    return false;
  }

  public long _caucho_lastModified()
  {
    return 0;
  }

  public com.caucho.java.LineMap _caucho_getLineMap()
  {
    return _caucho_line_map;
  }

  public void destroy()
  {
      _caucho_isDead = true;
      super.destroy();
  }

  public void init(com.caucho.java.LineMap lineMap,
                   com.caucho.vfs.Path appDir)
    throws javax.servlet.ServletException
  {
    com.caucho.vfs.Path resinHome = com.caucho.util.CauchoSystem.getResinHome();
    com.caucho.vfs.MergePath mergePath = new com.caucho.vfs.MergePath();
    mergePath.addMergePath(appDir);
    mergePath.addMergePath(resinHome);
    mergePath.addClassPath(getClass().getClassLoader());
    _caucho_line_map = new com.caucho.java.LineMap("_mygiftlist__jsp.java", "/babyreg/MyGiftList.jsp");
    _caucho_line_map.add(1, 1);
    _caucho_line_map.add(1, 27);
    _caucho_line_map.add(5, 29);
    _caucho_line_map.add(5, 39);
    _caucho_line_map.add(14, 40);
    _caucho_line_map.add(14, 49);
    com.caucho.vfs.Depend depend;
    depend = new com.caucho.vfs.Depend(appDir.lookup("MyGiftList.jsp"), 1025493532754L, 435L);
    _caucho_depends.add(depend);
  }

  private static byte []_jsp_string1;
  private static byte []_jsp_string2;
  private static byte []_jsp_string0;
  private static byte []_jsp_string3;
  static {
    _jsp_string1 = "\r\n\r\n<!-- retrieve the organization name for the page heading/title -->\r\n".getBytes();
    _jsp_string2 = "\r\n<table>\r\n<tr>\r\n<td>\r\nThese are the gift(s) you picked for me.\r\n</td>\r\n</tr>\r\n<tr>\r\n<td>\r\n    ".getBytes();
    _jsp_string0 = "\r\n".getBytes();
    _jsp_string3 = "\r\n</td>\r\n</tr>\r\n</table>\r\n".getBytes();
  }
}

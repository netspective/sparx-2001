package com.xaf.ace;

import java.io.*;
import java.util.*;

import com.xaf.form.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class LoginDialog extends com.xaf.security.LoginDialog
{
	protected StandardDialogSkin skin;

	public void initialize()
	{
		super.initialize();

		skin = new StandardDialogSkin();
		skin.setOuterTableAttrs("cellspacing='1' cellpadding='0'");
		skin.setInnerTableAttrs("cellspacing='0' cellpadding='4'");
		skin.setCaptionFontAttrs("size='2' face='tahoma,arial,helvetica' style='font-size:8pt' color='navy'");

		setHeading((SingleValueSource) null);
	}

	public DialogSkin getSkin()
	{
		return skin;
	}

	public void producePage(DialogContext dc, Writer writer) throws IOException
	{
		AppComponentsExplorerServlet servlet = (AppComponentsExplorerServlet) dc.getServlet();
		String sharedImagesRootURL = servlet.getSharedImagesRootURL();

		writer.write("<head>");
		writer.write("<title>Welcome to ACE</title>");
		writer.write("</head>");
		//writer.write("<body background='"+ sharedImagesRootURL +"/ace/2tone.gif'>");
		writer.write("<body background='white'>");
		writer.write("	<center><br>");
		//writer.write("		<img src='"+ sharedImagesRootURL +"/ace/login_splash.gif' width='473' height='132' border='0'>");
		writer.write("		<img src='"+ sharedImagesRootURL +"/ace/login_splash.gif' width='351' height='335' border='0'>");
		//writer.write("		<table cellpadding='0' cellspacing='0' width='473' bgcolor='#003366'>");
		writer.write("		<table cellpadding='0' cellspacing='0' width='351'>");
		writer.write("			<tr>");
		writer.write("				<td valign='middle' align='center'>");
		writer.write(               getHtml(dc, true));
		writer.write("				</td>");
		writer.write("			</tr>");
		writer.write("		</table>");
		writer.write("	</center>");
		writer.write("</body>");
	}
}
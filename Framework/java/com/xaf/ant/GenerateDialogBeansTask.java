package com.xaf.ant;

import org.apache.tools.ant.*;
import com.xaf.form.*;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;

public class GenerateDialogBeansTask extends Task
{
    private String sourceFile;
    private String destRoot;
    private String pkgName;
    private boolean debug;

    public GenerateDialogBeansTask()
    {
    }

    public void setSource(String src)
    {
        sourceFile = src;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public void setDest(String dest)
    {
        destRoot = dest;
    }

    public void setPackage(String pgkName)
    {
        this.pkgName = pgkName;
    }

    public void init() throws BuildException
    {
        destRoot = null;
        pkgName = null;
        sourceFile = null;
        debug = false;
    }

	public void execute() throws BuildException
	{
        log("Opening Dialogs (XML) file " + sourceFile + "...");
        DialogManager manager = DialogManagerFactory.getManager(sourceFile);
        Map dialogsInfo = manager.getDialogs();

        List errors = manager.getErrors();
        if(errors.size() > 0)
		{
			for(Iterator ei = errors.iterator(); ei.hasNext(); )
                log("Dialogs Warning: " + (String) ei.next());
		}

        File destDir = new File(destRoot, pkgName.replace('.', '/'));
        destDir.mkdirs();

        log("Generating dialog context beans in "+ destDir.getAbsolutePath() +"...");

        int generated = 0;
        for(Iterator i = dialogsInfo.values().iterator(); i.hasNext(); )
        {
            try
            {
                DialogManager.DialogInfo activeDialogInfo = (DialogManager.DialogInfo) i.next();
                File javaFile = activeDialogInfo.generateDialogBean(destDir.getAbsolutePath(), pkgName);
                if(debug) log("Generated dialog context bean for '"+ activeDialogInfo.getLookupName() +"' in '"+ javaFile.getAbsolutePath() +"'");
                generated++;
            }
            catch(IOException e)
            {
                throw new BuildException(e);
            }
        }

        log("Generated " + generated + " dialog context beans in package '"+ pkgName +"'");
	}
}
package com.xaf.ant;

import org.apache.tools.ant.*;
import com.xaf.*;

public class BuildConfigurationTask extends Task
{
    public BuildConfigurationTask()
    {
    }

	public void execute() throws BuildException
	{
		project.setProperty("build.product.name", BuildConfiguration.productName);
		project.setProperty("build.product.id", BuildConfiguration.productId);

        project.setProperty("build.version.complete", BuildConfiguration.getVersionAndBuild());
		project.setProperty("build.release", Integer.toString(BuildConfiguration.releaseNumber));
		project.setProperty("build.version.major", Integer.toString(BuildConfiguration.versionMajor));
		project.setProperty("build.version.minor", Integer.toString(BuildConfiguration.versionMinor));
		project.setProperty("build.number", Integer.toString(BuildConfiguration.buildNumber));

		project.setProperty("build.libitem.path.prefix", BuildConfiguration.getBuildPathPrefix());
		project.setProperty("build.libitem.file.prefix", BuildConfiguration.getBuildFilePrefix());
		project.setProperty("build.date", java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));

		log("Setup build.* properties for "+ BuildConfiguration.productName +" "+ BuildConfiguration.getVersionAndBuild() +".");
	}
}
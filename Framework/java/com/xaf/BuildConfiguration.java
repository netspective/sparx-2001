package com.xaf;

public class BuildConfiguration
{
	public static final String productName = "Sparx";
	public static final String productId   = "xaf";

	public static final int releaseNumber = 1;
	public static final int versionMajor = 2;
	public static final int versionMinor = 8;
	public static final int buildNumber = 26;

	static public final int getReleaseNumber() { return releaseNumber; }
	static public final int getVersionMajor() { return versionMajor; }
	static public final int getVersionMinor() { return versionMinor; }
	static public final int getBuildNumber() { return buildNumber; }

	static public final String getBuildPathPrefix()
	{
		return productId + "-" + releaseNumber + "_" + versionMajor + "_" + versionMinor + "-" + buildNumber;
	}

	static public final String getBuildFilePrefix()
	{
		return productId + "-" + releaseNumber + "_" + versionMajor + "_" + versionMinor;
	}

	static public final String getVersion()
	{
		return releaseNumber + "." + versionMajor + "." + versionMinor;
	}

	static public final String getVersionAndBuild()
	{
		return "Version " + getVersion() + " Build " + buildNumber;
	}

	static public final String getProductBuild()
	{
		return productName + " Version " + getVersion() + " Build " + buildNumber;
	}

	static public final String getVersionAndBuildShort()
	{
		return "v" + getVersion() + " b" + buildNumber;
	}

	/**
	 * Prints the absolute pathname of the class file
	 * containing the specified class name, as prescribed
	 * by the class path.
	 *
	 * @param className Name of the class.
	 */
	public static String getClassFileName(String className)
	{

		String resource = new String(className);

		if (!resource.startsWith("/"))
			resource = "/" + resource;

		resource = resource.replace('.', '/');
		resource = resource + ".class";

		java.net.URL classUrl = BuildConfiguration.class.getResource(resource);

		if (classUrl == null)
			return null;
		else
			return classUrl.getFile();
	}

}
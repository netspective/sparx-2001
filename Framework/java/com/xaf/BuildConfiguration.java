package com.xaf;

public class BuildConfiguration
{
	public static final String productName = "Sparx";
	public static final String productId   = "xaf";

	public static final int releaseNumber = 1;
	public static final int versionMajor = 2;
	public static final int versionMinor = 7;
	public static final int buildNumber = 79;

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
}
<?xml version='1.0'?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/TR/WD-xsl">

	<xsl:template match="/">
	  	<xsl:apply-templates select="test-plan"/>
	</xsl:template>		

	<xsl:template><xsl:copy><xsl:apply-templates select="@*"/><xsl:apply-templates/></xsl:copy></xsl:template>
	<xsl:template match="text()"><xsl:value-of select="."/></xsl:template>
	
	<xsl:template match="test-plan">
		<head>
			<title><xsl:value-of select="@title"/></title>
			<LINK REL="STYLESHEET" HREF="/lib/default.css" TYPE="text/css"/>
			<style>
				ol { padding-left: 10px; }
			</style>
		</head>
		<body>
			<xsl:if test=".[not(introduction)]">
				<h1>Introduction</h1>
				This test plan documents the preparation and testing of the 
				<b><xsl:value-of select="//module"/></b> module of the Healthcare Enterprise 
				Applications Suite (HEAS). 
			</xsl:if>
			<xsl:if test=".[not(test-items)]">
				<div class="error">&lt;test-items&gt; tag required.</div>
			</xsl:if>
			<xsl:if test=".[not(features-tested)]">
				<div class="error">&lt;features-tested&gt; tag required.</div>
			</xsl:if>
			<xsl:if test=".[not(tasks)]">
				<div class="error">&lt;tasks&gt; tag required.</div>
			</xsl:if>
			<xsl:if test=".[not(//app-area)]">
				<div class="error">&lt;app-area&gt; tag required in &lt;test-items&gt; tag.</div>
			</xsl:if>
			<xsl:apply-templates/>
		</body>
	</xsl:template>

	<xsl:template match="background | introduction | assumptions | approach">
		<h1><xsl:eval>capitalize(this.nodeName);</xsl:eval></h1>
  		<xsl:apply-templates/>		
	</xsl:template>
	
	<xsl:template match="test-items | features-tested | features-not-tested | deliverables | references | schedule | resources | staffing-training">
		<h1><xsl:eval>capitalize(this.nodeName);</xsl:eval></h1>
		<xsl:choose>
			<xsl:when test="li">
				<ul>
				<xsl:apply-templates/>		
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>		
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="preparation | risks">
		<h1><xsl:eval>capitalize(this.nodeName);</xsl:eval></h1>
		<xsl:choose>
			<xsl:when test=".[not(ol)]">
				<ol>
				<xsl:apply-templates/>		
				</ol>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>		
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="tasks">
		<xsl:if expr="manualApproach(this)">
			<h1>Approach</h1>
			This test plan outlines the testing tasks such that the testing will be
			performed manually (by a person, with little or no software automation). 
			In order for this test plan to work, a tester must be somewhat knowledgeable 
			about the application in general.
		</xsl:if>
	
		<xsl:if expr="this.parentNode.selectSingleNode('preparation') == null">
			<h1>Preparation</h1>
			No special preparation required.
		</xsl:if>
		<h1><xsl:eval>capitalize(this.nodeName);</xsl:eval></h1>
		<xsl:choose>
			<xsl:when test=".[not(test-case)]">
				<ol>
				<xsl:apply-templates/>		
				</ol>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>		
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if expr="defaultPassFail(this)">
			<h1>Pass/Fail Criteria</h1>
			Unless otherwise noted, <xsl:eval>passFailMessage</xsl:eval> fails
			if any of the steps marked <font color="red">VERIFY</font> are not verifable or
			fail the verification step.
		</xsl:if>
		<xsl:if expr="this.parentNode.selectSingleNode('references') == null">
			<h1>References</h1>
			None.
		</xsl:if>
	</xsl:template>

	<xsl:template match="pass-fail-criteria">
		<h1>Pass/Fail Criteria</h1>
  		<xsl:apply-templates/>		
	</xsl:template>

	<xsl:template match="suspend-resume-criteria">
		<h1>Suspension/Resumption Criteria</h1>
  		<xsl:apply-templates/>		
	</xsl:template>
	
	<xsl:template match="test-case">
		<table>
			<tr valign="top">
			<td width="50px"><div style="background-color: beige; text-align: center; border: solid 1px navy;"><font color="navy" size="1">CASE <b><xsl:eval>childNumber(this)</xsl:eval></b></font></div></td>
			<td>
				<h2><xsl:value-of select="@name"/></h2>
				<xsl:apply-templates select="case-summary"/>
				<ol>
				<xsl:apply-templates/>
				</ol>
			</td>
			</tr>
		</table>
	</xsl:template>
	
	<xsl:template match="app-area">
		<xsl:eval>var area = this.getAttribute('loc') ? this.getAttribute('loc').split(/\:\:/) : [];</xsl:eval>
		<xsl:choose>
			<xsl:when expr="area.length == 1">
				<xsl:eval>activePane = area[0];"";</xsl:eval>
				Pane <b><xsl:value-of select="@loc"/></b>
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:when expr="area.length == 2">
				<xsl:eval>
					activeModule = area[0];
					activePane = area[1];
					"";
				</xsl:eval>
				Module: <b><xsl:eval>area[0]</xsl:eval></b>
				<div style="margin-left:10px">
					<li>Pane: <b><xsl:eval>area[1]</xsl:eval></b></li>
					<xsl:apply-templates/>
				</div>
			</xsl:when>
			<xsl:when expr="area.length == 3">
				<xsl:eval>
					activeSystem = area[0];
					activeModule = area[1];
					activePane = area[2];
					"";
				</xsl:eval>
				Subsystem: <b><xsl:eval>area[0]</xsl:eval></b>
				<div style="margin-left:10px">
					Module: <b><xsl:eval>area[1]</xsl:eval></b>
					<div style="margin-left:10px">
						Pane: <b><xsl:eval>area[2]</xsl:eval></b>
						<xsl:apply-templates/>
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:eval>
					activeSystem = this.getAttribute("system");
					activeModule = this.getAttribute("module");
					activePane = this.getAttribute("pane");
					"";
				</xsl:eval>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="system-active"><i><xsl:eval>activeSystem</xsl:eval></i> Subsystem</xsl:template>
	<xsl:template match="module-active"><i><xsl:eval>activeModule</xsl:eval></i> Module</xsl:template>
	<xsl:template match="pane-active"><i><xsl:eval>activePane</xsl:eval></i> Pane</xsl:template>
	
	<xsl:template match="step">
		<li><xsl:apply-templates/></li>
	</xsl:template>
	
	<xsl:template match="step-prepare">
		<li>Execute the steps listed in the <i>Preparation</i> section.</li>
	</xsl:template>
	
	<xsl:template match="step-verify">
		<li><font color="red" size="-1">VERIFY</font> <xsl:apply-templates/></li>
	</xsl:template>
	
	<xsl:template match="step-verify-dialog">
		<li><font color="red" size="-1">VERIFY</font> 
			The <font color="green"><xsl:apply-templates/></font> dialog is displayed.
		</li>
	</xsl:template>
	
	<xsl:template match="step-verify-field">
		<li><font color="red" size="-1">VERIFY</font> 
		The dialog contains a 
		<xsl:choose>
			<xsl:when test="@type">
			<xsl:value-of select="@type"/>
			</xsl:when>
			<xsl:otherwise>
			text
			</xsl:otherwise>
		</xsl:choose>
		field to accept data entry for: 
		<font color="green"><xsl:apply-templates/></font>.
		</li>
	</xsl:template>
	
	<xsl:template match="step-view-fields">
		<li><font color="red" size="-1">VERIFY</font>
		The dialog contains the Data in the following fields:
		<font color="green"><xsl:apply-templates/></font>.
		</li>
	</xsl:template>	
	
	<xsl:template match="step-login">
		<li>Login to md.physia.com.</li>
	</xsl:template>
	
	<xsl:template match="steps-go-person">
		<li>Login to md.physia.com.</li>
		<li>Go to either the Directory system and find an existing person record or create a new person record.</li>
	</xsl:template>
	
	<xsl:template match="steps-go-org">
		<li>Login to md.physia.com.</li>
		<li>Go to either the Directory system and find an existing organization record or create a new organization record.</li>
	</xsl:template>
	
	<xsl:template match="step-go-tab">
		<li>Select the <font color="green"><xsl:apply-templates/></font> tab.</li>
	</xsl:template>
		
	<xsl:template match="step-locate">
		<li>Locate the <font color="green"><xsl:apply-templates/></font>.</li>
	</xsl:template>
	
	<xsl:template match="step-accept">
		<li>Press the OK button.</li>
	</xsl:template>
	
	<xsl:template match="step-cancel">
		<li>Press the Cancel button.</li>
	</xsl:template>
	
	<xsl:template match="step-go-link">
		<li>Click on the <font color="green"><xsl:apply-templates/></font> hyperlink.</li>
	</xsl:template>
	
	<xsl:template match="step-go-button">
		<li>Click on the <font color="green"><xsl:apply-templates/></font> button.</li>
	</xsl:template>
	
	<xsl:template match="step-select">
		<li>Select <font color="green"><xsl:apply-templates/></font> option of the <i><xsl:value-of select="@name"/></i> field.</li>
	</xsl:template>
	
	<xsl:template match="step-de">
		<li>
			Type <font color="green"><code><xsl:apply-templates/></code></font> in the <i><xsl:value-of select="@name"/></i> field.
			<xsl:if test="@fmt"><br/>The format of this field is <font color="navy"><xsl:value-of select="@fmt"/></font>. 
			Please try additional variations of this format
			to make sure that the field does not accept invalid entries.</xsl:if>
		</li>
	</xsl:template>
		
	<!-- the following tags like <system> and <module> are simple replacements -->
	<xsl:template match="system | module | file | pane | dialog | testplan | pane-cmd-add | pane-cmd-update | pane-cmd-remove"><xsl:eval>capitalize(this.nodeName);</xsl:eval> <xsl:if test="text()"><xsl:value-of select="text()"/></xsl:if></xsl:template>
	
	<xsl:template match="application">Healthcare Enterprise Applications Suite (HEAS)</xsl:template>
	
	<xsl:template match="references-none">
		<h1>References</h1>
		None.
	</xsl:template>
	
	<xsl:script><![CDATA[
	
	function manualApproach(e)
	{
		var approachAttr = e.getAttribute('approach');
		var noApproachElem = e.parentNode.selectSingleNode('approach') == null;
		var manualApproach = approachAttr == 'manual' || (noApproachElem && approachAttr == null);
		return manualApproach;
	}
	
	var passFailMessage = "a test case (and potentially this whole test plan)";
	
	function defaultPassFail(e)
	{
		noPassFail = this.parentNode.selectSingleNode('pass-fail-criteria') == null;
		var testCasesCount = this.selectNodes("test-case").length;
		if(testCasesCount < 2)
			passFailMessage = "this test plan";
		else
			passFailMessage = "a test case";
		return noPassFail;
	}

	var activeSystem = "NO ACTIVE SYSTEM";
	var activeModule = "NO ACTIVE MODULE";
	var activePane = "NO ACTIVE PANE";
	
	var dontCap = new Array('and', 'or', 'am', 'is', 'are', 'was', 'were', 'be', 'being', 'been', 'of', 'a', 'as', 'for');
	var dontCapDict = new Array;
	for(i = 0; i < dontCap.length; i++)
	{
		dontCapDict[dontCap[i]] = 1;
	}

	function capitalize(str)
	{
		var result = "";
		var words = str.split(/[\-\s]+/);
		var word;
		for(i = 0; i < words.length; i++)
		{
			word = words[i];
			result += dontCapDict[word] == 1 ?
				word :
				word.charAt(0).toUpperCase() + word.substring(1, 1000);
			if(i < words.length) result += ' ';
		}
		return result;
	}

	function elemNum(elem, ancestorName) 
	{
		if (elem)
		{
			var prefix = ancestorName != null ? 
				elemNum(elem.selectSingleNode("ancestor("+ancestorName+")"), ancestorName) :
				"";
			return prefix + formatIndex(childNumber(elem), "1") + ".";
		}
		else
		{
			return "";
		}
	}
	
	]]></xsl:script>
  
</xsl:stylesheet>

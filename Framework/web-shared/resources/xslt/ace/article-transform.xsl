<?xml version='1.0'?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xalan="http://xml.apache.org/xalan"
				exclude-result-prefixes="xalan">
				
	<xsl:param name="file-date"/>

	<xsl:template match="article | release-notes">
		<style>
			h1 { font-family: arial; font-size: 10pt; font-weight: bold; color: darkred; }
			h2 { font-family: arial; font-size: 9pt; font-weight: bold; color: navy; }
			td { font-family: 'verdana,arial'; font-size: 9pt; }
			ol { padding : 0; margin: 0; margin-left: 15pt }
			select { font-family: tahoma; font-size: 8pt; }
		</style>
		
		<xsl:if test="count(section | menu | project) > 1">
		<center>
		<b>Contents</b>: <select>
  		<xsl:apply-templates mode="toc"/>
  		</select>
  		</center>
  		<p/>
  		</xsl:if>
  		
  		<div style="margin: 5;">
  		<xsl:apply-templates mode="data"/>
  		</div>
	</xsl:template>

	<xsl:template match="bug">
		<a>
			<xsl:attribute name="href">http://bugs.physia.com/show_bug.cgi?id=<xsl:value-of select="."/></xsl:attribute>
			<xsl:attribute name="target">BUG<xsl:value-of select="."/></xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	
	<xsl:template match="release-info" mode="data">
		<table bgcolor="#EEEEEE">
			<xsl:for-each select="*">
			<tr valign="top">
				<xsl:choose>
					<xsl:when test="name() = 'build-id'">
						<td align="right">Build ID:</td><td><b><xsl:apply-templates/></b></td>
					</xsl:when>
					<xsl:when test="name() = 'build-manager'">
						<td align="right">Build Manager:</td><td><b><xsl:apply-templates/></b></td>
					</xsl:when>
					<xsl:when test="name() = 'tested-date'">
						<td align="right">Test Date:</td><td><b><xsl:apply-templates/></b></td>
					</xsl:when>
					<xsl:when test="name() = 'release-date'">
						<td align="right">Release Date:</td><td><b><xsl:apply-templates/></b></td>
					</xsl:when>
					<xsl:when test="name() = 'overview'">
						<td align="right">Overview:</td><td><b><xsl:apply-templates/></b></td>
					</xsl:when>
					<xsl:when test="name() = 'description'">
						<td align="right">Description:</td><td><b><xsl:apply-templates/></b></td>
					</xsl:when>
				</xsl:choose>
			</tr>
			</xsl:for-each>
		</table>
		<p/>
	</xsl:template>

	<xsl:template match="deliverables" mode="data">
		<table cellspacing="5">
	  		<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="deliverable">
		<tr valign="top">
			<td><h1>
				<a>
				<xsl:attribute name="href">http://bugs.physia.com/show_bug.cgi?id=<xsl:value-of select="@id"/></xsl:attribute>
				<xsl:attribute name="target">BUG<xsl:value-of select="@id"/></xsl:attribute>
				<xsl:value-of select="@id"/>
				</a>
				</h1>
			</td>
			<td>
				<xsl:apply-templates/>
				<hr size="1" color="#DDDDDD"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="link">
		<a>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</a>
	</xsl:template>
		
	<xsl:template match="section" mode="toc">
		<option><xsl:attribute name="value"><xsl:value-of select="@heading | heading"/></xsl:attribute><xsl:value-of select="@heading | heading"/></option>
	</xsl:template>
	<xsl:template match="section" mode="data">
		<tr valign="top">
			<td><h1><a><xsl:attribute name="name"><xsl:value-of select="@heading | heading"/></xsl:attribute><xsl:value-of select="@heading | heading"/></a></h1></td>
			<td>
				<xsl:apply-templates/>
				<hr size="1" color="#DDDDDD"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="menu" mode="toc">
		<option><xsl:attribute name="value"><xsl:value-of select="@heading | @caption"/></xsl:attribute><xsl:value-of select="@heading | @caption"/></option>
	</xsl:template>
	<xsl:template match="menu" mode="data">
		<b><xsl:value-of select="@heading | @caption"/></b>
		<ul>
		<xsl:for-each select="menu-item">
			<li>
			<xsl:choose>
				<xsl:when test="@href">
					<a><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute><xsl:value-of select="@caption"/></a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@caption"/>
				</xsl:otherwise>
			</xsl:choose>
			</li>
			<xsl:apply-templates mode="data"/>
		</xsl:for-each>
		</ul>
	</xsl:template>
	
	<xsl:template match="menu" mode="toc">
		<option><xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="@name"/></option>
	</xsl:template>
	<xsl:template match="project" mode="data">		
		<script>
			top.document.title = "<xsl:value-of select="@name"/>";
		</script>
		<style>
			.projectHead { font-family: arial; font-size: 14pt; font-weight: bold; color: navy; margin-bottom: 5pt; border-bottom: solid 1px red; padding-top: 2pt; }
			.projectSumm { font-family: 'arial,arial'; font-size: 10pt; padding-bottom: 5pt; }
			.projectAttrName { font-family: 'arial'; font-size: 10pt; color: navy; padding-bottom: 1; }
			.projectAttrValue { font-family: 'arial'; font-size: 10pt; border-bottom: solid 1px silver; padding-bottom: 1; }
			h1 { font-family: arial; font-size: 12pt; font-weight: bold; color: darkred; }
			h2 { font-family: arial; font-size: 10pt; font-weight: bold; color: navy; }
			td { font-family: 'tahoma,arial'; font-size: 8pt; }
			.tableColHead { font-family: 'tahoma,arial'; font-size: 8pt; color: navy; border-bottom: solid 2px black; }
			ol { padding : 0; margin: 0; margin-left: 15pt }
		</style>
		<xsl:if test="@name">
			<div class="projectHead"><xsl:value-of select="@name"/></div>
		</xsl:if>
		<xsl:if test="summary">
			<div class="projectSumm"><xsl:apply-templates select="summary"/></div>
		</xsl:if>
		<xsl:if test="project-attribute">
			<table border="0" cellspacing="2" cellpadding="0">
				<xsl:apply-templates select="project-attribute">
					<xsl:with-param name="allResources">
							<xsl:copy-of select="xalan:distinct(.//resource)"/>
					</xsl:with-param>
				</xsl:apply-templates>
			</table>
			<p/>
			<xsl:if test=".//needinfo">
				<b>Need Information</b>
				<table>
					<xsl:for-each select=".//needinfo">
						<xsl:sort select="@source"/>
						<tr valign="top">
							<td style="font-family:tahoma;size:8pt;color:red"><xsl:value-of select="@urgency"/></td>
							<td style="font-family:tahoma;size:8pt"><NOBR>Task</NOBR></td>
							<td style="font-family:tahoma;size:8pt">[<B><xsl:value-of select="@source"/></B>]</td>
							<td style="font-family:tahoma;size:8pt"><xsl:value-of select="."/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:if>
			<p/>
		</xsl:if>
		<xsl:if test="task">
		<table border="0" cellspacing="0" cellpadding="2" >
			<tr bgcolor="#EEEEEE">
				<th class="tableColHead" align="left">No.</th>
				<xsl:if test=".//priority">
				<th class="tableColHead" title="Priority">P</th>
				</xsl:if>
				<th class="tableColHead">Task</th>
				<xsl:if test=".//resource">
				<th class="tableColHead"><nobr>Resource(s)</nobr></th>
				</xsl:if>
				<xsl:if test=".//completed">
				<th class="tableColHead">Completed</th>
				</xsl:if>
				<xsl:if test=".//duration">
				<th class="tableColHead">Duration</th>
				</xsl:if>
				<xsl:if test=".//start">
				<th class="tableColHead">Start</th>
				</xsl:if>
				<xsl:if test=".//finish">
				<th class="tableColHead">Finish</th>
				</xsl:if>
				<xsl:if test=".//remarks">
				<th class="tableColHead">Remarks</th>
				</xsl:if>
			</tr>
			<xsl:apply-templates select="task">
				<xsl:with-param name="haveAnyPriority" select="boolean(.//priority)"/>
				<xsl:with-param name="haveAnyResource" select="boolean(.//resource)"/>
				<xsl:with-param name="haveAnyCompleted" select="boolean(.//completed)"/>
				<xsl:with-param name="haveAnyDuration" select="boolean(.//duration)"/>
				<xsl:with-param name="haveAnyStart" select="boolean(.//start)"/>
				<xsl:with-param name="haveAnyFinish" select="boolean(.//finish)"/>
				<xsl:with-param name="haveAnyRemarks" select="boolean(.//remarks)"/>
			</xsl:apply-templates>
		</table>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="project-attribute">
		<xsl:param name="allResources"/>
		
		<tr valign="top">
			<td align="right" class="projectAttrName">
				<xsl:value-of select="@name"/>:
			</td>
			<td class="projectAttrValue">
				<xsl:if test="@name = 'Resources'">
				<ol>
				<xsl:for-each select="xalan:nodeset($allResources)/*">
					<xsl:sort select="."/>
					<li><xsl:value-of select="."/></li>
				</xsl:for-each>
				</ol>
				</xsl:if>
				<xsl:if test="@name = 'Last Update'">
				<xsl:value-of select="$file-date"/>
				</xsl:if>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="task">
		<xsl:param name="haveAnyPriority"/>
		<xsl:param name="haveAnyResource"/>
		<xsl:param name="haveAnyCompleted"/>
		<xsl:param name="haveAnyDuration"/>
		<xsl:param name="haveAnyStart"/>
		<xsl:param name="haveAnyFinish"/>
		<xsl:param name="haveAnyRemarks"/>
		<xsl:variable name="taskNum"><xsl:number level="multiple"/></xsl:variable>
		<tr valign="top">
			<td align="left" style="color: silver">
				<xsl:value-of select="$taskNum"/>
			</td>			
			<xsl:if test="$haveAnyPriority">
			<td align="center" style=""><div style="color: red"><xsl:value-of select="priority"/></div></td>
			</xsl:if>
			<td>
				<xsl:if test="task">
					<xsl:attribute name="COLSPAN">0</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="STYLE">
					padding-left: <xsl:value-of select="count(ancestor::*) * 25"/>;
					<xsl:if test="task">
					font-weight: bold;
					color: navy;
					</xsl:if>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="name">
						<xsl:if test="new">
							<font color="red"><u>[<B>NEW</B>]</u>: 
							</font>
						</xsl:if>
						<xsl:apply-templates select="name"/>
						<xsl:if test="comments">
						<DIV>
							<xsl:attribute name="STYLE">
								font-family: arial,helvetica;
								font-size: 9pt;
								font-weight: normal;
								color: green;
							</xsl:attribute>
							<xsl:apply-templates select="comments"/>
						</DIV>
						</xsl:if>
						<xsl:if test="needinfo">
						<DIV>
							<xsl:attribute name="STYLE">
								font-family: arial,helvetica;
								font-size: 9pt;
								font-weight: normal;
								color: red;
							</xsl:attribute>
							<font color="darkred">[Waiting for <B><xsl:value-of select="needinfo/@source"/></B>]</font>
							<xsl:apply-templates select="needinfo"/>
						</DIV>
						</xsl:if>
					</xsl:when>
					<xsl:when test="project/@href">
						<a><xsl:attribute name="href">/lib/page.asp?src=<xsl:value-of select="project/@href"/>&amp;sty=/stylesheets/project.xsl</xsl:attribute><xsl:value-of select="project"/></a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<xsl:if test="$haveAnyResource">
			<td style="color:green">
				<xsl:for-each select="resource">
					<xsl:value-of select="."/>
					<xsl:if test="position() != last()">, </xsl:if>
				</xsl:for-each>
			</td>
			</xsl:if>
			<xsl:if test="$haveAnyCompleted">
			<td align="center">
				<xsl:if test="completed">
				<DIV STYLE="text-align:left; position:relative; border:1px solid black; width:50px; background-color: white">
				  <DIV>
				    <xsl:attribute name="STYLE">
				      text-align:center;
				      position:relative;
				      background-color:green;
				      width: <xsl:value-of select="number(completed) div 2"/>px;
				    </xsl:attribute>
				  </DIV>
				</DIV>
				</xsl:if>
			</td>
			</xsl:if>
			<xsl:if test="$haveAnyDuration">
			<td align="center"><xsl:value-of select="duration"/></td>
			</xsl:if>
			<xsl:if test="$haveAnyStart">
			<td><xsl:value-of select="start"/></td>
			</xsl:if>
			<xsl:if test="$haveAnyFinish">
			<td><xsl:value-of select="finish"/></td>
			</xsl:if>
			<xsl:if test="$haveAnyRemarks">
			<td><xsl:value-of select="remarks"/></td>
			</xsl:if>
		</tr>
		<xsl:apply-templates select="task">
			<xsl:with-param name="haveAnyPriority" select="$haveAnyPriority"/>
			<xsl:with-param name="haveAnyResource" select="$haveAnyResource"/>
			<xsl:with-param name="haveAnyCompleted" select="$haveAnyCompleted"/>
			<xsl:with-param name="haveAnyDuration" select="$haveAnyDuration"/>
			<xsl:with-param name="haveAnyStart" select="$haveAnyStart"/>
			<xsl:with-param name="haveAnyFinish" select="$haveAnyFinish"/>
			<xsl:with-param name="haveAnyRemarks" select="$haveAnyRemarks"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy>
			<xsl:for-each select="@*">
				<xsl:copy/>
			</xsl:for-each>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>

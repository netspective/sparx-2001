<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
    version='1.0'>


  <xsl:param name="title"/>
  <xsl:param name="module"/>
  <xsl:param name="cvsweb"/>

  <xsl:output method="html" indent="yes"  encoding="US-ASCII"/>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="attribute::*[. != '']"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="changelog">
    <HTML>
      <HEAD>
        <TITLE><xsl:value-of select="$title"/></TITLE>
      </HEAD>
      <BODY link="#000000" alink="#000000" vlink="#000000" text="#000000">
        <style type="text/css">
          body, p
          {
            font-family: tahoma,arial,helvetica;
            font-size: 8pt;
            color:#000000;
          }
	      .dateAndAuthor
          {
            font-family: tahoma,arial,helvetica;
            font-size: 8pt;
            text-align: leftt;
            border-top: solid 1px #aaaaaa;
            border-bottom: solid 1px #000000;
            background:#bbbbbb;
            font-weight: bold;
    	  }
            .files_affected
            {
              font-family: tahoma,arial,helvetica;
              font-size: 8pt;
              text-align: leftt;
              border-top: solid 1px #cccccc;
              border-bottom: solid 1px #bbbbbb;
              background:#dddddd;
            }
          tr, td
          {
            font-family: verdana,arial,helvetica;
            background:#eeeeee;
          }
          td.msg
          {
            font-family:lucida console,monospace;
            font-size: 9pt;
          }
          span.files_button
          {
            cursor: hand;
          }
          ol.files_hidden
          {
            display: none;
          }
          ol.files_visible
          {
          }
	  </style>
          <script>
              function toggleFilesDisplay(entryId)
              {
                var filesButtonElem = document.all[entryId + '_files_button'];
                var filesListElem = document.all[entryId + '_files_list'];
                if(filesListElem.className == 'files_visible')
                {
                    filesButtonElem.innerHTML = " [&lt;u&gt;View files&lt;/u&gt;]";
                    filesListElem.className = 'files_hidden';
                }
                else
                {
                    filesButtonElem.innerHTML = " [&lt;u&gt;Hide files&lt;/u&gt;]";
                    filesListElem.className = 'files_visible';
                }
              }
          </script>
          <h1>
            <a name="top"><xsl:value-of select="$title"/></a>
          </h1>

          <xsl:apply-templates select=".//entry[not(msg = following::msg)]">
            <xsl:sort select="date" data-type="text" order="descending"/>
            <xsl:sort select="time" data-type="text" order="descending"/>
            <xsl:sort select="msg" data-type="text"/>
          </xsl:apply-templates>

      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="entry">
      <xsl:variable name="entry_id">entry_<xsl:value-of select="position()"/></xsl:variable>
      <xsl:variable name="message" select="msg"/>
      <!-- we filtered out unique messages so now we need to find all files that belong to our message -->
      <xsl:variable name="files" select="//entry[msg = $message]/file"/>

    <table cellspacing="0" cellpadding="2" border="0" width="100%">
    <TR>
      <TD class="dateAndAuthor" colspan="2">
        <xsl:value-of select="date"/><xsl:text> </xsl:text><xsl:value-of select="time"/><xsl:text> </xsl:text><xsl:value-of select="author"/>
      </TD>
    </TR>
    <TR>
      <td width="40">&#160;</td>
      <TD class="msg">
        <xsl:apply-templates select="msg"/>
      </TD>
    </TR>
    <TD class="files_affected" colspan="2">
        Files affected: <xsl:value-of select="count($files)"/>
        <span class="files_button">
            <xsl:attribute name="id"><xsl:value-of select="$entry_id"/>_files_button</xsl:attribute>
            <xsl:attribute name="onclick">javascript:toggleFilesDisplay("<xsl:value-of select="$entry_id"/>")</xsl:attribute>
            [<u>View files</u>]
        </span>
        <ol class="files_hidden" id="{$entry_id}_files_list">
          <!-- we've removed the duplicates in the <changelog> element so grab all the files with the same message -->
          <xsl:apply-templates select="$files">
              <xsl:sort select="." data-type="text"/>
          </xsl:apply-templates>
        </ol>
    </TD>
    </table>
      <p/>
  </xsl:template>

    <xsl:template match="msg">
       <xsl:call-template name="br-replace">
          <xsl:with-param name="word" select="."/>
       </xsl:call-template>
     </xsl:template>

    <!-- replace newlines with <br> -->
     <xsl:template name="br-replace">
       <xsl:param name="word"/>
    <!-- </xsl:text> on next line on purpose to get newline -->
       <xsl:variable name="cr"><xsl:text>
</xsl:text></xsl:variable>
       <xsl:choose>
       <xsl:when test="contains($word,$cr)">
           <xsl:value-of select="substring-before($word,$cr)"/>
           <br/>
           <xsl:call-template name="space-replace">
              <xsl:with-param name="word" select="substring-after($word,$cr)"/>
           </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="$word"/>
       </xsl:otherwise>
      </xsl:choose>
     </xsl:template>

    <!-- replace spaces with non-breakable spaces -->
    <xsl:template name="space-replace">
      <xsl:param name="word"/>
      <xsl:choose>
      <xsl:when test="starts-with($word, ' ')">&#160;<xsl:call-template name="space-replace"><xsl:with-param name="word" select="substring-after($word, ' ')"/></xsl:call-template></xsl:when>
      <xsl:otherwise>
          <xsl:call-template name="br-replace">
            <xsl:with-param name="word" select="$word"/>
          </xsl:call-template>
      </xsl:otherwise>
     </xsl:choose>
    </xsl:template>

  <xsl:template match="date">
    <i><xsl:value-of select="."/></i>
  </xsl:template>

  <xsl:template match="time">
    <i><xsl:value-of select="."/></i>
  </xsl:template>

  <xsl:template match="author">
    <i>
      <a>
        <xsl:attribute name="href">mailto:<xsl:value-of select="."/></xsl:attribute>
        <xsl:value-of select="."/>
      </a>
    </i>
  </xsl:template>

  <xsl:template match="file">
    <li>
      <a>
        <xsl:choose>
          <xsl:when test="string-length(prevrevision) = 0 ">
            <xsl:attribute name="href"><xsl:value-of select="$cvsweb"/><xsl:value-of select="$module" />/<xsl:value-of select="name" />?rev=<xsl:value-of select="revision" />&amp;content-type=text/x-cvsweb-markup</xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="href"><xsl:value-of select="$cvsweb"/><xsl:value-of select="$module" />/<xsl:value-of select="name" />?r1=<xsl:value-of select="revision" />&amp;r2=<xsl:value-of select="prevrevision"/></xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="name" /> (<xsl:value-of select="revision"/>)
      </a>
    </li>
  </xsl:template>

  <!-- Any elements within a msg are processed,
       so that we can preserve HTML tags. -->
    <!-- REPLACED BY SHAHID N. SHAH
  <xsl:template match="msg">
    <b><xsl:apply-templates/></b>
  </xsl:template>
  -->

</xsl:stylesheet>


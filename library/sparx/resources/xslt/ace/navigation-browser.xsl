<?xml version="1.0"?>
    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:param name="root-url"/>
    <xsl:param name="page-heading"/>
    <xsl:param name="detail-type"/>
    <xsl:param name="detail-name"/>
    <xsl:param name="sub-detail-name"/>

    <!-- all of the appConfig variables are passed in, so we can use them -->
    <xsl:param name="sparx.shared.images-url"/>
    <xsl:param name="sparx.ace.images-root-url"/>

    <xsl:param name="ui-images-root-url"><xsl:value-of select="$sparx.shared.images-url"/>/dbdd</xsl:param>

    <xsl:template match="xaf">
        <xsl:choose>
            <xsl:when test="$detail-type = 'describe' and $detail-name">
                <xsl:apply-templates select="structure//page[@id = $detail-name]" mode="detail"/>
            </xsl:when>
            <xsl:otherwise>
                <div class="content">
                    <div class="content_head">Pages (total of <xsl:value-of select="count(structure//page)"/>)</div>
                    <table class="data_table" cellspacing="0" cellpadding="2" border="0">
                    <tr class="data_table_header">
                        <th class="data_table">Actions</th>
                        <th class="data_table">ID</th>
                        <th class="data_table">Caption</th>
                        <th class="data_table">Class</th>
                        <th class="data_table">Attributes</th>
                    </tr>
                    <xsl:apply-templates select="structure" mode="toc"/>
                    </table>

                    <div class="content_head">Options</div>
                    <table class="data_table" cellspacing="0" cellpadding="2" border="0">
                        <tr class="data_table_header">
                            <th class="data_table">Name</th>
                            <th class="data_table">Value</th>
                        </tr>
                        <xsl:for-each select="meta-info/options">
                            <xsl:sort select="@name"/>
                            <tr valign="top" class="data_table">
                                <td class="data_table"><xsl:value-of select="@name"/></td>
                                <td class="data_table"><font color="green"><xsl:value-of select="@value"/></font></td>
                            </tr>
                        </xsl:for-each>
                    </table>

                    <div class="content_head">Source Files</div>
                    <table class="data_table" cellspacing="0" cellpadding="2" border="0">
                    <tr class="data_table_header">
                        <th class="data_table">File</th>
                        <th class="data_table">Included-from</th>
                    </tr>
                    <xsl:for-each select="meta-info/source-files/source-file">
                        <tr class="data_table">
                            <td class="data_table">
                            <a class="data_table">
                            <xsl:attribute name="href"><xsl:value-of select="concat($root-url, '../../../documents?browseDoc=', @abs-path)"/></xsl:attribute>
                            <xsl:attribute name="target"><xsl:value-of select="@abs-path"/></xsl:attribute>
                            <xsl:value-of select="@abs-path"/>
                            </a>
                            </td>
                            <td class="data_table">
                                <xsl:value-of select="@included-from"/>
                                <xsl:if test="not(@included-from)">&#160;</xsl:if>
                            </td>
                        </tr>
                    </xsl:for-each>
                    </table>

                    <p/>
                    <xsl:if test="meta-info/errors">
                        <div class="content_head">Errors</div>
                        <ol>
                        <xsl:for-each select="meta-info/errors/error">
                            <li><xsl:value-of select="."/></li>
                        </xsl:for-each>
                        </ol>
                    </xsl:if>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="structure" mode="toc">
        <xsl:apply-templates select="page" mode="toc">
            <xsl:with-param name="level">&#160;&#160;</xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="page" mode="toc">
        <xsl:param name="level"/>

        <tr class="data_table">
            <td class="data_table">&#160;</td>
            <td class="data_table"><nobr><xsl:value-of select="$level"/><xsl:value-of select="@id"/></nobr>&#160;</td>
            <td class="data_table"><font color="navy"><xsl:value-of select="@caption"/>&#160;</font></td>
            <td class="data_table"><xsl:value-of select="@class"/>&#160;</td>
            <td class="data_table">
                <xsl:for-each select="@*[name() != 'id' and name() != 'caption' and name() != 'class']">
                    <font color="red"><xsl:value-of select="name()"/></font> =
                    <font color="green"><xsl:value-of select="."/></font><br/>
                </xsl:for-each>
            </td>
        </tr>

        <xsl:apply-templates select="page" mode="toc">
            <xsl:with-param name="level"><xsl:value-of select="$level"/>&#160;&#160;</xsl:with-param>
        </xsl:apply-templates>

    </xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>

<!-- take a schema and normalize it (remove everything but elements and attributes and resolve types) -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

	<xsl:template match="xsd:schema">
		<xsl:apply-templates select="xsd:element"/>
	</xsl:template>

	<xsl:template match="xsd:annotation"/>

	<xsl:template match="xsd:group[@ref]">
		<xsl:variable name="group-name" select="@ref"/>
		<xsl:apply-templates select="/xsd:schema/xsd:group[@name = $group-name]"/>
	</xsl:template>

	<xsl:template match="xsd:complexType[@name]">
		<xsl:if test="xsd:complexContent/xsd:extension[@base]">
			<xsl:variable name="type-name" select="xsd:complexContent/xsd:extension/@base"/>
			<xsl:apply-templates select="/xsd:schema/xsd:complexType[@name = $type-name]"/>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="xsd:element[@name]">
		<xsl:variable name="elem-defn" select="."/>

		<xsl:element name="{$elem-defn/@name}">
			<elem.attributes>
				<xsl:call-template name="get-xsd-attributes">
					<xsl:with-param name="type-defn" select="$elem-defn"/>
					<xsl:with-param name="inheritance-depth" select="0"/>
				</xsl:call-template>
			</elem.attributes>
			<elem.summary><xsl:value-of select="$elem-defn/xsd:annotation/xsd:documentation"/></elem.summary>
			<elem.remarks><xsl:value-of select="$elem-defn/xsd:annotation"/></elem.remarks>
			<elem.use>
				<xsl:variable name="min-occurs">
					<xsl:choose>
						<xsl:when test="$elem-defn/@minOccurs"><xsl:value-of select="$elem-defn/@minOccurs"/></xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="max-occurs">
					<xsl:choose>
						<xsl:when test="$elem-defn/@maxOccurs"><xsl:value-of select="$elem-defn/@maxOccurs"/></xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$min-occurs = 1 and $max-occurs = 'unbounded'">1..n</xsl:when>
					<xsl:when test="$min-occurs = 0 and $max-occurs = 'unbounded'">0..n</xsl:when>
					<xsl:when test="$min-occurs = 0 and $max-occurs = 1">0..1</xsl:when>
					<xsl:when test="$min-occurs = 1 and $max-occurs = 1">1</xsl:when>
					<xsl:otherwise><xsl:value-of select="concat($min-occurs, '..', $max-occurs)"/></xsl:otherwise>
				</xsl:choose>
			</elem.use>
			<xsl:if test="@type">
				<xsl:variable name="type-name" select="@type"/>
				<xsl:apply-templates select="/xsd:schema/xsd:complexType[@name = $type-name]"/>
			</xsl:if>
			<xsl:if test="xsd:complexType/xsd:complexContent/xsd:extension[@base]">
				<xsl:variable name="type-name" select="xsd:complexType/xsd:complexContent/xsd:extension/@base"/>
				<xsl:apply-templates select="/xsd:schema/xsd:complexType[@name = $type-name]"/>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<!-- given an attribute, get actual type and enumerations -->
	<xsl:template name="get-attr-type">
		<xsl:param name="attr"/>
		<xsl:choose>
			<xsl:when test="$attr/@type">
				<type><xsl:value-of select="$attr/@type"/></type>
			</xsl:when>
			<xsl:when test="$attr/xsd:simpleType/xsd:restriction[@base]">
				<type><xsl:value-of select="$attr/xsd:simpleType/xsd:restriction/@base"/></type>
				<enums>
				<xsl:for-each select="$attr/xsd:simpleType/xsd:restriction/xsd:enumeration">
					<enum><xsl:value-of select="@value"/></enum>
				</xsl:for-each>
				</enums>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- given a type definition get all attributes from inheritance tree -->
	<xsl:template name="get-xsd-attributes">
		<xsl:param name="type-defn"/>
		<xsl:param name="inheritance-depth"/>
		<xsl:if test="$type-defn/@type">
			<xsl:call-template name="get-xsd-attributes">
				<xsl:with-param name="type-defn" select="/xsd:schema/xsd:complexType[@name = $type-defn/@type]"/>
				<xsl:with-param name="inheritance-depth" select="$inheritance-depth + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$type-defn/xsd:complexContent/xsd:extension">
			<xsl:call-template name="get-xsd-attributes">
				<xsl:with-param name="type-defn" select="/xsd:schema/xsd:complexType[@name = $type-defn/xsd:complexContent/xsd:extension/@base]"/>
				<xsl:with-param name="inheritance-depth" select="$inheritance-depth + 1"/>
			</xsl:call-template>
			<xsl:call-template name="get-xsd-attributes">
				<xsl:with-param name="type-defn" select="$type-defn/xsd:complexContent/xsd:extension"/>
				<xsl:with-param name="inheritance-depth" select="$inheritance-depth"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$type-defn/xsd:complexType">
			<xsl:call-template name="get-xsd-attributes">
				<xsl:with-param name="type-defn" select="$type-defn/xsd:complexType"/>
				<xsl:with-param name="inheritance-depth" select="$inheritance-depth"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$type-defn/xsd:attributeGroup">
			<xsl:call-template name="get-xsd-attributes">
				<xsl:with-param name="type-defn" select="/xsd:schema/xsd:attributeGroup[@name = $type-defn/xsd:attributeGroup/@ref]"/>
				<xsl:with-param name="inheritance-depth" select="$inheritance-depth"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:for-each select="$type-defn/xsd:attribute">
			<attribute>
				<xsl:attribute name="inheritance-depth"><xsl:value-of select="$inheritance-depth"/></xsl:attribute>
				<name><xsl:value-of select="@name"/></name>
				<xsl:call-template name="get-attr-type"><xsl:with-param name="attr" select="."/></xsl:call-template>
				<use><xsl:value-of select="@use"/></use>
				<xsl:if test="@default">
					<default><xsl:value-of select="@default"/></default>
				</xsl:if>
				<remarks><xsl:value-of select="xsd:annotation"/></remarks>
			</attribute>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>



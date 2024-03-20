/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.osm.jalo;

import com.klarna.osm.constants.KlarnaosmaddonConstants;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.klarna.osm.jalo.KlarnaOSMConfig KlarnaOSMConfig}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedKlarnaOSMConfig extends GenericItem
{
	/** Qualifier of the <code>KlarnaOSMConfig.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute **/
	public static final String PDPENABLED = "pdpEnabled";
	/** Qualifier of the <code>KlarnaOSMConfig.cartEnabled</code> attribute **/
	public static final String CARTENABLED = "cartEnabled";
	/** Qualifier of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute **/
	public static final String DATAINLINEENABLED = "dataInlineEnabled";
	/** Qualifier of the <code>KlarnaOSMConfig.scriptUrl</code> attribute **/
	public static final String SCRIPTURL = "scriptUrl";
	/** Qualifier of the <code>KlarnaOSMConfig.country</code> attribute **/
	public static final String COUNTRY = "country";
	/** Qualifier of the <code>KlarnaOSMConfig.uci</code> attribute **/
	public static final String UCI = "uci";
	/** Qualifier of the <code>KlarnaOSMConfig.productPlacementTagID</code> attribute **/
	public static final String PRODUCTPLACEMENTTAGID = "productPlacementTagID";
	/** Qualifier of the <code>KlarnaOSMConfig.cartPlacementTagID</code> attribute **/
	public static final String CARTPLACEMENTTAGID = "cartPlacementTagID";
	/** Qualifier of the <code>KlarnaOSMConfig.cartTheme</code> attribute **/
	public static final String CARTTHEME = "cartTheme";
	/** Qualifier of the <code>KlarnaOSMConfig.pdpTheme</code> attribute **/
	public static final String PDPTHEME = "pdpTheme";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(PDPENABLED, AttributeMode.INITIAL);
		tmp.put(CARTENABLED, AttributeMode.INITIAL);
		tmp.put(DATAINLINEENABLED, AttributeMode.INITIAL);
		tmp.put(SCRIPTURL, AttributeMode.INITIAL);
		tmp.put(COUNTRY, AttributeMode.INITIAL);
		tmp.put(UCI, AttributeMode.INITIAL);
		tmp.put(PRODUCTPLACEMENTTAGID, AttributeMode.INITIAL);
		tmp.put(CARTPLACEMENTTAGID, AttributeMode.INITIAL);
		tmp.put(CARTTHEME, AttributeMode.INITIAL);
		tmp.put(PDPTHEME, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute.
	 * @return the cartEnabled - OSM enabled for Cart
	 */
	public Boolean isCartEnabled(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, CARTENABLED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute.
	 * @return the cartEnabled - OSM enabled for Cart
	 */
	public Boolean isCartEnabled()
	{
		return isCartEnabled( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute. 
	 * @return the cartEnabled - OSM enabled for Cart
	 */
	public boolean isCartEnabledAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isCartEnabled( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute. 
	 * @return the cartEnabled - OSM enabled for Cart
	 */
	public boolean isCartEnabledAsPrimitive()
	{
		return isCartEnabledAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute. 
	 * @param value the cartEnabled - OSM enabled for Cart
	 */
	public void setCartEnabled(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, CARTENABLED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute. 
	 * @param value the cartEnabled - OSM enabled for Cart
	 */
	public void setCartEnabled(final Boolean value)
	{
		setCartEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute. 
	 * @param value the cartEnabled - OSM enabled for Cart
	 */
	public void setCartEnabled(final SessionContext ctx, final boolean value)
	{
		setCartEnabled( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartEnabled</code> attribute. 
	 * @param value the cartEnabled - OSM enabled for Cart
	 */
	public void setCartEnabled(final boolean value)
	{
		setCartEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartPlacementTagID</code> attribute.
	 * @return the cartPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public String getCartPlacementTagID(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CARTPLACEMENTTAGID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartPlacementTagID</code> attribute.
	 * @return the cartPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public String getCartPlacementTagID()
	{
		return getCartPlacementTagID( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartPlacementTagID</code> attribute. 
	 * @param value the cartPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public void setCartPlacementTagID(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CARTPLACEMENTTAGID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartPlacementTagID</code> attribute. 
	 * @param value the cartPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public void setCartPlacementTagID(final String value)
	{
		setCartPlacementTagID( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartTheme</code> attribute.
	 * @return the cartTheme - The data theme of KlarnaConfiguration
	 */
	public EnumerationValue getCartTheme(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, CARTTHEME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.cartTheme</code> attribute.
	 * @return the cartTheme - The data theme of KlarnaConfiguration
	 */
	public EnumerationValue getCartTheme()
	{
		return getCartTheme( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartTheme</code> attribute. 
	 * @param value the cartTheme - The data theme of KlarnaConfiguration
	 */
	public void setCartTheme(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, CARTTHEME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.cartTheme</code> attribute. 
	 * @param value the cartTheme - The data theme of KlarnaConfiguration
	 */
	public void setCartTheme(final EnumerationValue value)
	{
		setCartTheme( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.code</code> attribute.
	 * @return the code - The Id of KlarnaConfiguration
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.code</code> attribute.
	 * @return the code - The Id of KlarnaConfiguration
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.code</code> attribute. 
	 * @param value the code - The Id of KlarnaConfiguration
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.code</code> attribute. 
	 * @param value the code - The Id of KlarnaConfiguration
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.country</code> attribute.
	 * @return the country - Country support by Klarna
	 */
	public Country getCountry(final SessionContext ctx)
	{
		return (Country)getProperty( ctx, COUNTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.country</code> attribute.
	 * @return the country - Country support by Klarna
	 */
	public Country getCountry()
	{
		return getCountry( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.country</code> attribute. 
	 * @param value the country - Country support by Klarna
	 */
	public void setCountry(final SessionContext ctx, final Country value)
	{
		setProperty(ctx, COUNTRY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.country</code> attribute. 
	 * @param value the country - Country support by Klarna
	 */
	public void setCountry(final Country value)
	{
		setCountry( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute.
	 * @return the dataInlineEnabled - OSM Datainline enabled
	 */
	public Boolean isDataInlineEnabled(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, DATAINLINEENABLED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute.
	 * @return the dataInlineEnabled - OSM Datainline enabled
	 */
	public Boolean isDataInlineEnabled()
	{
		return isDataInlineEnabled( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute. 
	 * @return the dataInlineEnabled - OSM Datainline enabled
	 */
	public boolean isDataInlineEnabledAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isDataInlineEnabled( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute. 
	 * @return the dataInlineEnabled - OSM Datainline enabled
	 */
	public boolean isDataInlineEnabledAsPrimitive()
	{
		return isDataInlineEnabledAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute. 
	 * @param value the dataInlineEnabled - OSM Datainline enabled
	 */
	public void setDataInlineEnabled(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, DATAINLINEENABLED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute. 
	 * @param value the dataInlineEnabled - OSM Datainline enabled
	 */
	public void setDataInlineEnabled(final Boolean value)
	{
		setDataInlineEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute. 
	 * @param value the dataInlineEnabled - OSM Datainline enabled
	 */
	public void setDataInlineEnabled(final SessionContext ctx, final boolean value)
	{
		setDataInlineEnabled( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.dataInlineEnabled</code> attribute. 
	 * @param value the dataInlineEnabled - OSM Datainline enabled
	 */
	public void setDataInlineEnabled(final boolean value)
	{
		setDataInlineEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute.
	 * @return the pdpEnabled - OSM enabled for PD
	 */
	public Boolean isPdpEnabled(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, PDPENABLED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute.
	 * @return the pdpEnabled - OSM enabled for PD
	 */
	public Boolean isPdpEnabled()
	{
		return isPdpEnabled( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute. 
	 * @return the pdpEnabled - OSM enabled for PD
	 */
	public boolean isPdpEnabledAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isPdpEnabled( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute. 
	 * @return the pdpEnabled - OSM enabled for PD
	 */
	public boolean isPdpEnabledAsPrimitive()
	{
		return isPdpEnabledAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute. 
	 * @param value the pdpEnabled - OSM enabled for PD
	 */
	public void setPdpEnabled(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, PDPENABLED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute. 
	 * @param value the pdpEnabled - OSM enabled for PD
	 */
	public void setPdpEnabled(final Boolean value)
	{
		setPdpEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute. 
	 * @param value the pdpEnabled - OSM enabled for PD
	 */
	public void setPdpEnabled(final SessionContext ctx, final boolean value)
	{
		setPdpEnabled( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.pdpEnabled</code> attribute. 
	 * @param value the pdpEnabled - OSM enabled for PD
	 */
	public void setPdpEnabled(final boolean value)
	{
		setPdpEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.pdpTheme</code> attribute.
	 * @return the pdpTheme - The data theme of KlarnaConfiguration
	 */
	public EnumerationValue getPdpTheme(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, PDPTHEME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.pdpTheme</code> attribute.
	 * @return the pdpTheme - The data theme of KlarnaConfiguration
	 */
	public EnumerationValue getPdpTheme()
	{
		return getPdpTheme( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.pdpTheme</code> attribute. 
	 * @param value the pdpTheme - The data theme of KlarnaConfiguration
	 */
	public void setPdpTheme(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, PDPTHEME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.pdpTheme</code> attribute. 
	 * @param value the pdpTheme - The data theme of KlarnaConfiguration
	 */
	public void setPdpTheme(final EnumerationValue value)
	{
		setPdpTheme( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.productPlacementTagID</code> attribute.
	 * @return the productPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public String getProductPlacementTagID(final SessionContext ctx)
	{
		return (String)getProperty( ctx, PRODUCTPLACEMENTTAGID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.productPlacementTagID</code> attribute.
	 * @return the productPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public String getProductPlacementTagID()
	{
		return getProductPlacementTagID( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.productPlacementTagID</code> attribute. 
	 * @param value the productPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public void setProductPlacementTagID(final SessionContext ctx, final String value)
	{
		setProperty(ctx, PRODUCTPLACEMENTTAGID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.productPlacementTagID</code> attribute. 
	 * @param value the productPlacementTagID - The placement Id of KlarnaConfiguration
	 */
	public void setProductPlacementTagID(final String value)
	{
		setProductPlacementTagID( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.scriptUrl</code> attribute.
	 * @return the scriptUrl - The placement Id of KlarnaConfiguration
	 */
	public String getScriptUrl(final SessionContext ctx)
	{
		return (String)getProperty( ctx, SCRIPTURL);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.scriptUrl</code> attribute.
	 * @return the scriptUrl - The placement Id of KlarnaConfiguration
	 */
	public String getScriptUrl()
	{
		return getScriptUrl( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.scriptUrl</code> attribute. 
	 * @param value the scriptUrl - The placement Id of KlarnaConfiguration
	 */
	public void setScriptUrl(final SessionContext ctx, final String value)
	{
		setProperty(ctx, SCRIPTURL,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.scriptUrl</code> attribute. 
	 * @param value the scriptUrl - The placement Id of KlarnaConfiguration
	 */
	public void setScriptUrl(final String value)
	{
		setScriptUrl( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.uci</code> attribute.
	 * @return the uci - The placement Id of KlarnaConfiguration
	 */
	public String getUci(final SessionContext ctx)
	{
		return (String)getProperty( ctx, UCI);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaOSMConfig.uci</code> attribute.
	 * @return the uci - The placement Id of KlarnaConfiguration
	 */
	public String getUci()
	{
		return getUci( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.uci</code> attribute. 
	 * @param value the uci - The placement Id of KlarnaConfiguration
	 */
	public void setUci(final SessionContext ctx, final String value)
	{
		setProperty(ctx, UCI,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaOSMConfig.uci</code> attribute. 
	 * @param value the uci - The placement Id of KlarnaConfiguration
	 */
	public void setUci(final String value)
	{
		setUci( getSession().getSessionContext(), value );
	}
	
}

/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 22-Mar-2024, 12:29:46 pm                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.klarna.payment.jalo.KlarnaExpCheckoutConfig KlarnaExpCheckoutConfig}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedKlarnaExpCheckoutConfig extends GenericItem
{
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.active</code> attribute **/
	public static final String ACTIVE = "active";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.clientKey</code> attribute **/
	public static final String CLIENTKEY = "clientKey";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.scriptUrl</code> attribute **/
	public static final String SCRIPTURL = "scriptUrl";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute **/
	public static final String COLLECTSHIPPINGADDRESS = "collectShippingAddress";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.buttonTheme</code> attribute **/
	public static final String BUTTONTHEME = "buttonTheme";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.buttonShape</code> attribute **/
	public static final String BUTTONSHAPE = "buttonShape";
	/** Qualifier of the <code>KlarnaExpCheckoutConfig.country</code> attribute **/
	public static final String COUNTRY = "country";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(ACTIVE, AttributeMode.INITIAL);
		tmp.put(CLIENTKEY, AttributeMode.INITIAL);
		tmp.put(SCRIPTURL, AttributeMode.INITIAL);
		tmp.put(COLLECTSHIPPINGADDRESS, AttributeMode.INITIAL);
		tmp.put(BUTTONTHEME, AttributeMode.INITIAL);
		tmp.put(BUTTONSHAPE, AttributeMode.INITIAL);
		tmp.put(COUNTRY, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.active</code> attribute.
	 * @return the active - Active Klarna 's express checkout
	 */
	public Boolean isActive(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, ACTIVE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.active</code> attribute.
	 * @return the active - Active Klarna 's express checkout
	 */
	public Boolean isActive()
	{
		return isActive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.active</code> attribute. 
	 * @return the active - Active Klarna 's express checkout
	 */
	public boolean isActiveAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isActive( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.active</code> attribute. 
	 * @return the active - Active Klarna 's express checkout
	 */
	public boolean isActiveAsPrimitive()
	{
		return isActiveAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's express checkout
	 */
	public void setActive(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, ACTIVE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's express checkout
	 */
	public void setActive(final Boolean value)
	{
		setActive( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's express checkout
	 */
	public void setActive(final SessionContext ctx, final boolean value)
	{
		setActive( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's express checkout
	 */
	public void setActive(final boolean value)
	{
		setActive( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.buttonShape</code> attribute.
	 * @return the buttonShape - Determines the button shape
	 */
	public EnumerationValue getButtonShape(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, BUTTONSHAPE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.buttonShape</code> attribute.
	 * @return the buttonShape - Determines the button shape
	 */
	public EnumerationValue getButtonShape()
	{
		return getButtonShape( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.buttonShape</code> attribute. 
	 * @param value the buttonShape - Determines the button shape
	 */
	public void setButtonShape(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, BUTTONSHAPE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.buttonShape</code> attribute. 
	 * @param value the buttonShape - Determines the button shape
	 */
	public void setButtonShape(final EnumerationValue value)
	{
		setButtonShape( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.buttonTheme</code> attribute.
	 * @return the buttonTheme - Determines the button theme
	 */
	public EnumerationValue getButtonTheme(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, BUTTONTHEME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.buttonTheme</code> attribute.
	 * @return the buttonTheme - Determines the button theme
	 */
	public EnumerationValue getButtonTheme()
	{
		return getButtonTheme( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.buttonTheme</code> attribute. 
	 * @param value the buttonTheme - Determines the button theme
	 */
	public void setButtonTheme(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, BUTTONTHEME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.buttonTheme</code> attribute. 
	 * @param value the buttonTheme - Determines the button theme
	 */
	public void setButtonTheme(final EnumerationValue value)
	{
		setButtonTheme( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.clientKey</code> attribute.
	 * @return the clientKey - Client Key
	 */
	public String getClientKey(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CLIENTKEY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.clientKey</code> attribute.
	 * @return the clientKey - Client Key
	 */
	public String getClientKey()
	{
		return getClientKey( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.clientKey</code> attribute. 
	 * @param value the clientKey - Client Key
	 */
	public void setClientKey(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CLIENTKEY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.clientKey</code> attribute. 
	 * @param value the clientKey - Client Key
	 */
	public void setClientKey(final String value)
	{
		setClientKey( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.code</code> attribute.
	 * @return the code - The Id of Klarna Express Checkout Configuration
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.code</code> attribute.
	 * @return the code - The Id of Klarna Express Checkout Configuration
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.code</code> attribute. 
	 * @param value the code - The Id of Klarna Express Checkout Configuration
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.code</code> attribute. 
	 * @param value the code - The Id of Klarna Express Checkout Configuration
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute.
	 * @return the collectShippingAddress - Collect Shipping Address
	 */
	public Boolean isCollectShippingAddress(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, COLLECTSHIPPINGADDRESS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute.
	 * @return the collectShippingAddress - Collect Shipping Address
	 */
	public Boolean isCollectShippingAddress()
	{
		return isCollectShippingAddress( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute. 
	 * @return the collectShippingAddress - Collect Shipping Address
	 */
	public boolean isCollectShippingAddressAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isCollectShippingAddress( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute. 
	 * @return the collectShippingAddress - Collect Shipping Address
	 */
	public boolean isCollectShippingAddressAsPrimitive()
	{
		return isCollectShippingAddressAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute. 
	 * @param value the collectShippingAddress - Collect Shipping Address
	 */
	public void setCollectShippingAddress(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, COLLECTSHIPPINGADDRESS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute. 
	 * @param value the collectShippingAddress - Collect Shipping Address
	 */
	public void setCollectShippingAddress(final Boolean value)
	{
		setCollectShippingAddress( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute. 
	 * @param value the collectShippingAddress - Collect Shipping Address
	 */
	public void setCollectShippingAddress(final SessionContext ctx, final boolean value)
	{
		setCollectShippingAddress( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.collectShippingAddress</code> attribute. 
	 * @param value the collectShippingAddress - Collect Shipping Address
	 */
	public void setCollectShippingAddress(final boolean value)
	{
		setCollectShippingAddress( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.country</code> attribute.
	 * @return the country - Country for Locale
	 */
	public Country getCountry(final SessionContext ctx)
	{
		return (Country)getProperty( ctx, COUNTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.country</code> attribute.
	 * @return the country - Country for Locale
	 */
	public Country getCountry()
	{
		return getCountry( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.country</code> attribute. 
	 * @param value the country - Country for Locale
	 */
	public void setCountry(final SessionContext ctx, final Country value)
	{
		setProperty(ctx, COUNTRY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.country</code> attribute. 
	 * @param value the country - Country for Locale
	 */
	public void setCountry(final Country value)
	{
		setCountry( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.scriptUrl</code> attribute.
	 * @return the scriptUrl - URL of Klarna Express Checkout javascript library
	 */
	public String getScriptUrl(final SessionContext ctx)
	{
		return (String)getProperty( ctx, SCRIPTURL);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaExpCheckoutConfig.scriptUrl</code> attribute.
	 * @return the scriptUrl - URL of Klarna Express Checkout javascript library
	 */
	public String getScriptUrl()
	{
		return getScriptUrl( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.scriptUrl</code> attribute. 
	 * @param value the scriptUrl - URL of Klarna Express Checkout javascript library
	 */
	public void setScriptUrl(final SessionContext ctx, final String value)
	{
		setProperty(ctx, SCRIPTURL,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaExpCheckoutConfig.scriptUrl</code> attribute. 
	 * @param value the scriptUrl - URL of Klarna Express Checkout javascript library
	 */
	public void setScriptUrl(final String value)
	{
		setScriptUrl( getSession().getSessionContext(), value );
	}
	
}

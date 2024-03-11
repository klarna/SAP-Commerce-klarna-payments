/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import de.hybris.platform.commerceservices.jalo.process.StoreFrontCustomerProcess;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link de.hybris.platform.commerceservices.jalo.process.StoreFrontCustomerProcess OrderFailedEmailProcess}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedOrderFailedEmailProcess extends StoreFrontCustomerProcess
{
	/** Qualifier of the <code>OrderFailedEmailProcess.cart</code> attribute **/
	public static final String CART = "cart";
	/** Qualifier of the <code>OrderFailedEmailProcess.kperrorMessage</code> attribute **/
	public static final String KPERRORMESSAGE = "kperrorMessage";
	/** Qualifier of the <code>OrderFailedEmailProcess.kpOrderId</code> attribute **/
	public static final String KPORDERID = "kpOrderId";
	/** Qualifier of the <code>OrderFailedEmailProcess.countryName</code> attribute **/
	public static final String COUNTRYNAME = "countryName";
	/** Qualifier of the <code>OrderFailedEmailProcess.cartId</code> attribute **/
	public static final String CARTID = "cartId";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(StoreFrontCustomerProcess.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(CART, AttributeMode.INITIAL);
		tmp.put(KPERRORMESSAGE, AttributeMode.INITIAL);
		tmp.put(KPORDERID, AttributeMode.INITIAL);
		tmp.put(COUNTRYNAME, AttributeMode.INITIAL);
		tmp.put(CARTID, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.cart</code> attribute.
	 * @return the cart - Cart Model
	 */
	public AbstractOrder getCart(final SessionContext ctx)
	{
		return (AbstractOrder)getProperty( ctx, CART);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.cart</code> attribute.
	 * @return the cart - Cart Model
	 */
	public AbstractOrder getCart()
	{
		return getCart( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.cart</code> attribute. 
	 * @param value the cart - Cart Model
	 */
	public void setCart(final SessionContext ctx, final AbstractOrder value)
	{
		setProperty(ctx, CART,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.cart</code> attribute. 
	 * @param value the cart - Cart Model
	 */
	public void setCart(final AbstractOrder value)
	{
		setCart( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.cartId</code> attribute.
	 * @return the cartId - Error Message
	 */
	public String getCartId(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CARTID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.cartId</code> attribute.
	 * @return the cartId - Error Message
	 */
	public String getCartId()
	{
		return getCartId( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.cartId</code> attribute. 
	 * @param value the cartId - Error Message
	 */
	public void setCartId(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CARTID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.cartId</code> attribute. 
	 * @param value the cartId - Error Message
	 */
	public void setCartId(final String value)
	{
		setCartId( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.countryName</code> attribute.
	 * @return the countryName - Error Message
	 */
	public String getCountryName(final SessionContext ctx)
	{
		return (String)getProperty( ctx, COUNTRYNAME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.countryName</code> attribute.
	 * @return the countryName - Error Message
	 */
	public String getCountryName()
	{
		return getCountryName( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.countryName</code> attribute. 
	 * @param value the countryName - Error Message
	 */
	public void setCountryName(final SessionContext ctx, final String value)
	{
		setProperty(ctx, COUNTRYNAME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.countryName</code> attribute. 
	 * @param value the countryName - Error Message
	 */
	public void setCountryName(final String value)
	{
		setCountryName( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.kperrorMessage</code> attribute.
	 * @return the kperrorMessage - Error Message
	 */
	public String getKperrorMessage(final SessionContext ctx)
	{
		return (String)getProperty( ctx, KPERRORMESSAGE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.kperrorMessage</code> attribute.
	 * @return the kperrorMessage - Error Message
	 */
	public String getKperrorMessage()
	{
		return getKperrorMessage( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.kperrorMessage</code> attribute. 
	 * @param value the kperrorMessage - Error Message
	 */
	public void setKperrorMessage(final SessionContext ctx, final String value)
	{
		setProperty(ctx, KPERRORMESSAGE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.kperrorMessage</code> attribute. 
	 * @param value the kperrorMessage - Error Message
	 */
	public void setKperrorMessage(final String value)
	{
		setKperrorMessage( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.kpOrderId</code> attribute.
	 * @return the kpOrderId - Error Message
	 */
	public String getKpOrderId(final SessionContext ctx)
	{
		return (String)getProperty( ctx, KPORDERID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderFailedEmailProcess.kpOrderId</code> attribute.
	 * @return the kpOrderId - Error Message
	 */
	public String getKpOrderId()
	{
		return getKpOrderId( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.kpOrderId</code> attribute. 
	 * @param value the kpOrderId - Error Message
	 */
	public void setKpOrderId(final SessionContext ctx, final String value)
	{
		setProperty(ctx, KPORDERID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderFailedEmailProcess.kpOrderId</code> attribute. 
	 * @param value the kpOrderId - Error Message
	 */
	public void setKpOrderId(final String value)
	{
		setKpOrderId( getSession().getSessionContext(), value );
	}
	
}

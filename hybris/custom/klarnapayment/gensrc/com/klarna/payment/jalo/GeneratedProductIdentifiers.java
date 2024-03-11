/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.klarna.payment.jalo.ProductIdentifiers ProductIdentifiers}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedProductIdentifiers extends GenericItem
{
	/** Qualifier of the <code>ProductIdentifiers.categoryPath</code> attribute **/
	public static final String CATEGORYPATH = "categoryPath";
	/** Qualifier of the <code>ProductIdentifiers.gtin</code> attribute **/
	public static final String GTIN = "gtin";
	/** Qualifier of the <code>ProductIdentifiers.manufaturerpartnumber</code> attribute **/
	public static final String MANUFATURERPARTNUMBER = "manufaturerpartnumber";
	/** Qualifier of the <code>ProductIdentifiers.brand</code> attribute **/
	public static final String BRAND = "brand";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CATEGORYPATH, AttributeMode.INITIAL);
		tmp.put(GTIN, AttributeMode.INITIAL);
		tmp.put(MANUFATURERPARTNUMBER, AttributeMode.INITIAL);
		tmp.put(BRAND, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.brand</code> attribute.
	 * @return the brand - brand of Product
	 */
	public String getBrand(final SessionContext ctx)
	{
		return (String)getProperty( ctx, BRAND);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.brand</code> attribute.
	 * @return the brand - brand of Product
	 */
	public String getBrand()
	{
		return getBrand( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.brand</code> attribute. 
	 * @param value the brand - brand of Product
	 */
	public void setBrand(final SessionContext ctx, final String value)
	{
		setProperty(ctx, BRAND,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.brand</code> attribute. 
	 * @param value the brand - brand of Product
	 */
	public void setBrand(final String value)
	{
		setBrand( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.categoryPath</code> attribute.
	 * @return the categoryPath - category Path  of Product
	 */
	public String getCategoryPath(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CATEGORYPATH);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.categoryPath</code> attribute.
	 * @return the categoryPath - category Path  of Product
	 */
	public String getCategoryPath()
	{
		return getCategoryPath( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.categoryPath</code> attribute. 
	 * @param value the categoryPath - category Path  of Product
	 */
	public void setCategoryPath(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CATEGORYPATH,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.categoryPath</code> attribute. 
	 * @param value the categoryPath - category Path  of Product
	 */
	public void setCategoryPath(final String value)
	{
		setCategoryPath( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.gtin</code> attribute.
	 * @return the gtin - Global trade item number of Product
	 */
	public String getGtin(final SessionContext ctx)
	{
		return (String)getProperty( ctx, GTIN);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.gtin</code> attribute.
	 * @return the gtin - Global trade item number of Product
	 */
	public String getGtin()
	{
		return getGtin( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.gtin</code> attribute. 
	 * @param value the gtin - Global trade item number of Product
	 */
	public void setGtin(final SessionContext ctx, final String value)
	{
		setProperty(ctx, GTIN,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.gtin</code> attribute. 
	 * @param value the gtin - Global trade item number of Product
	 */
	public void setGtin(final String value)
	{
		setGtin( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.manufaturerpartnumber</code> attribute.
	 * @return the manufaturerpartnumber - manufacturer partnumber of Product
	 */
	public String getManufaturerpartnumber(final SessionContext ctx)
	{
		return (String)getProperty( ctx, MANUFATURERPARTNUMBER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductIdentifiers.manufaturerpartnumber</code> attribute.
	 * @return the manufaturerpartnumber - manufacturer partnumber of Product
	 */
	public String getManufaturerpartnumber()
	{
		return getManufaturerpartnumber( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.manufaturerpartnumber</code> attribute. 
	 * @param value the manufaturerpartnumber - manufacturer partnumber of Product
	 */
	public void setManufaturerpartnumber(final SessionContext ctx, final String value)
	{
		setProperty(ctx, MANUFATURERPARTNUMBER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ProductIdentifiers.manufaturerpartnumber</code> attribute. 
	 * @param value the manufaturerpartnumber - manufacturer partnumber of Product
	 */
	public void setManufaturerpartnumber(final String value)
	{
		setManufaturerpartnumber( getSession().getSessionContext(), value );
	}
	
}

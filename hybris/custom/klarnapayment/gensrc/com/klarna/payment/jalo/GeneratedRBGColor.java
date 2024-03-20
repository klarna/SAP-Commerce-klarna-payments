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
 * Generated class for type {@link com.klarna.payment.jalo.RBGColor RBGColor}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedRBGColor extends GenericItem
{
	/** Qualifier of the <code>RBGColor.redField</code> attribute **/
	public static final String REDFIELD = "redField";
	/** Qualifier of the <code>RBGColor.blueField</code> attribute **/
	public static final String BLUEFIELD = "blueField";
	/** Qualifier of the <code>RBGColor.greenField</code> attribute **/
	public static final String GREENFIELD = "greenField";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(REDFIELD, AttributeMode.INITIAL);
		tmp.put(BLUEFIELD, AttributeMode.INITIAL);
		tmp.put(GREENFIELD, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.blueField</code> attribute.
	 * @return the blueField
	 */
	public Integer getBlueField(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, BLUEFIELD);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.blueField</code> attribute.
	 * @return the blueField
	 */
	public Integer getBlueField()
	{
		return getBlueField( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.blueField</code> attribute. 
	 * @return the blueField
	 */
	public int getBlueFieldAsPrimitive(final SessionContext ctx)
	{
		Integer value = getBlueField( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.blueField</code> attribute. 
	 * @return the blueField
	 */
	public int getBlueFieldAsPrimitive()
	{
		return getBlueFieldAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.blueField</code> attribute. 
	 * @param value the blueField
	 */
	public void setBlueField(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, BLUEFIELD,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.blueField</code> attribute. 
	 * @param value the blueField
	 */
	public void setBlueField(final Integer value)
	{
		setBlueField( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.blueField</code> attribute. 
	 * @param value the blueField
	 */
	public void setBlueField(final SessionContext ctx, final int value)
	{
		setBlueField( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.blueField</code> attribute. 
	 * @param value the blueField
	 */
	public void setBlueField(final int value)
	{
		setBlueField( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.greenField</code> attribute.
	 * @return the greenField
	 */
	public Integer getGreenField(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, GREENFIELD);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.greenField</code> attribute.
	 * @return the greenField
	 */
	public Integer getGreenField()
	{
		return getGreenField( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.greenField</code> attribute. 
	 * @return the greenField
	 */
	public int getGreenFieldAsPrimitive(final SessionContext ctx)
	{
		Integer value = getGreenField( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.greenField</code> attribute. 
	 * @return the greenField
	 */
	public int getGreenFieldAsPrimitive()
	{
		return getGreenFieldAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.greenField</code> attribute. 
	 * @param value the greenField
	 */
	public void setGreenField(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, GREENFIELD,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.greenField</code> attribute. 
	 * @param value the greenField
	 */
	public void setGreenField(final Integer value)
	{
		setGreenField( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.greenField</code> attribute. 
	 * @param value the greenField
	 */
	public void setGreenField(final SessionContext ctx, final int value)
	{
		setGreenField( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.greenField</code> attribute. 
	 * @param value the greenField
	 */
	public void setGreenField(final int value)
	{
		setGreenField( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.redField</code> attribute.
	 * @return the redField
	 */
	public Integer getRedField(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, REDFIELD);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.redField</code> attribute.
	 * @return the redField
	 */
	public Integer getRedField()
	{
		return getRedField( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.redField</code> attribute. 
	 * @return the redField
	 */
	public int getRedFieldAsPrimitive(final SessionContext ctx)
	{
		Integer value = getRedField( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RBGColor.redField</code> attribute. 
	 * @return the redField
	 */
	public int getRedFieldAsPrimitive()
	{
		return getRedFieldAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.redField</code> attribute. 
	 * @param value the redField
	 */
	public void setRedField(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, REDFIELD,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.redField</code> attribute. 
	 * @param value the redField
	 */
	public void setRedField(final Integer value)
	{
		setRedField( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.redField</code> attribute. 
	 * @param value the redField
	 */
	public void setRedField(final SessionContext ctx, final int value)
	{
		setRedField( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>RBGColor.redField</code> attribute. 
	 * @param value the redField
	 */
	public void setRedField(final int value)
	{
		setRedField( getSession().getSessionContext(), value );
	}
	
}

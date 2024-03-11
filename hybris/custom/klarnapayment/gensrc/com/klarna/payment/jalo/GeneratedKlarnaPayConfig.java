/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.jalo.ProductIdentifiers;
import com.klarna.payment.jalo.RBGColor;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.klarna.payment.jalo.KlarnaPayConfig KlarnaPayConfig}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedKlarnaPayConfig extends GenericItem
{
	/** Qualifier of the <code>KlarnaPayConfig.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>KlarnaPayConfig.active</code> attribute **/
	public static final String ACTIVE = "active";
	/** Qualifier of the <code>KlarnaPayConfig.merchantID</code> attribute **/
	public static final String MERCHANTID = "merchantID";
	/** Qualifier of the <code>KlarnaPayConfig.sharedSecret</code> attribute **/
	public static final String SHAREDSECRET = "sharedSecret";
	/** Qualifier of the <code>KlarnaPayConfig.merchantEmail</code> attribute **/
	public static final String MERCHANTEMAIL = "merchantEmail";
	/** Qualifier of the <code>KlarnaPayConfig.endpointType</code> attribute **/
	public static final String ENDPOINTTYPE = "endpointType";
	/** Qualifier of the <code>KlarnaPayConfig.endpointMode</code> attribute **/
	public static final String ENDPOINTMODE = "endpointMode";
	/** Qualifier of the <code>KlarnaPayConfig.merchantReference2</code> attribute **/
	public static final String MERCHANTREFERENCE2 = "merchantReference2";
	/** Qualifier of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute **/
	public static final String PRODUCTURLSREQUIRED = "productUrlsRequired";
	/** Qualifier of the <code>KlarnaPayConfig.attachementRequired</code> attribute **/
	public static final String ATTACHEMENTREQUIRED = "attachementRequired";
	/** Qualifier of the <code>KlarnaPayConfig.purchaseCurrency</code> attribute **/
	public static final String PURCHASECURRENCY = "purchaseCurrency";
	/** Qualifier of the <code>KlarnaPayConfig.prodIdentifiers</code> attribute **/
	public static final String PRODIDENTIFIERS = "prodIdentifiers";
	/** Qualifier of the <code>KlarnaPayConfig.colorButton</code> attribute **/
	public static final String COLORBUTTON = "colorButton";
	/** Qualifier of the <code>KlarnaPayConfig.colorButtonText</code> attribute **/
	public static final String COLORBUTTONTEXT = "colorButtonText";
	/** Qualifier of the <code>KlarnaPayConfig.colorCheckbox</code> attribute **/
	public static final String COLORCHECKBOX = "colorCheckbox";
	/** Qualifier of the <code>KlarnaPayConfig.colorCheckboxCheckMark</code> attribute **/
	public static final String COLORCHECKBOXCHECKMARK = "colorCheckboxCheckMark";
	/** Qualifier of the <code>KlarnaPayConfig.colorHeader</code> attribute **/
	public static final String COLORHEADER = "colorHeader";
	/** Qualifier of the <code>KlarnaPayConfig.colorLink</code> attribute **/
	public static final String COLORLINK = "colorLink";
	/** Qualifier of the <code>KlarnaPayConfig.colorBorder</code> attribute **/
	public static final String COLORBORDER = "colorBorder";
	/** Qualifier of the <code>KlarnaPayConfig.colorBorderSelected</code> attribute **/
	public static final String COLORBORDERSELECTED = "colorBorderSelected";
	/** Qualifier of the <code>KlarnaPayConfig.colorText</code> attribute **/
	public static final String COLORTEXT = "colorText";
	/** Qualifier of the <code>KlarnaPayConfig.colorDetails</code> attribute **/
	public static final String COLORDETAILS = "colorDetails";
	/** Qualifier of the <code>KlarnaPayConfig.colorTextSecondary</code> attribute **/
	public static final String COLORTEXTSECONDARY = "colorTextSecondary";
	/** Qualifier of the <code>KlarnaPayConfig.radiusborder</code> attribute **/
	public static final String RADIUSBORDER = "radiusborder";
	/** Qualifier of the <code>KlarnaPayConfig.autoCapture</code> attribute **/
	public static final String AUTOCAPTURE = "autoCapture";
	/** Qualifier of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute **/
	public static final String ISVCNENABLED = "isVCNEnabled";
	/** Qualifier of the <code>KlarnaPayConfig.vcnPublicKey</code> attribute **/
	public static final String VCNPUBLICKEY = "vcnPublicKey";
	/** Qualifier of the <code>KlarnaPayConfig.vcnPrivateKey</code> attribute **/
	public static final String VCNPRIVATEKEY = "vcnPrivateKey";
	/** Qualifier of the <code>KlarnaPayConfig.vcnKeyID</code> attribute **/
	public static final String VCNKEYID = "vcnKeyID";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(ACTIVE, AttributeMode.INITIAL);
		tmp.put(MERCHANTID, AttributeMode.INITIAL);
		tmp.put(SHAREDSECRET, AttributeMode.INITIAL);
		tmp.put(MERCHANTEMAIL, AttributeMode.INITIAL);
		tmp.put(ENDPOINTTYPE, AttributeMode.INITIAL);
		tmp.put(ENDPOINTMODE, AttributeMode.INITIAL);
		tmp.put(MERCHANTREFERENCE2, AttributeMode.INITIAL);
		tmp.put(PRODUCTURLSREQUIRED, AttributeMode.INITIAL);
		tmp.put(ATTACHEMENTREQUIRED, AttributeMode.INITIAL);
		tmp.put(PURCHASECURRENCY, AttributeMode.INITIAL);
		tmp.put(PRODIDENTIFIERS, AttributeMode.INITIAL);
		tmp.put(COLORBUTTON, AttributeMode.INITIAL);
		tmp.put(COLORBUTTONTEXT, AttributeMode.INITIAL);
		tmp.put(COLORCHECKBOX, AttributeMode.INITIAL);
		tmp.put(COLORCHECKBOXCHECKMARK, AttributeMode.INITIAL);
		tmp.put(COLORHEADER, AttributeMode.INITIAL);
		tmp.put(COLORLINK, AttributeMode.INITIAL);
		tmp.put(COLORBORDER, AttributeMode.INITIAL);
		tmp.put(COLORBORDERSELECTED, AttributeMode.INITIAL);
		tmp.put(COLORTEXT, AttributeMode.INITIAL);
		tmp.put(COLORDETAILS, AttributeMode.INITIAL);
		tmp.put(COLORTEXTSECONDARY, AttributeMode.INITIAL);
		tmp.put(RADIUSBORDER, AttributeMode.INITIAL);
		tmp.put(AUTOCAPTURE, AttributeMode.INITIAL);
		tmp.put(ISVCNENABLED, AttributeMode.INITIAL);
		tmp.put(VCNPUBLICKEY, AttributeMode.INITIAL);
		tmp.put(VCNPRIVATEKEY, AttributeMode.INITIAL);
		tmp.put(VCNKEYID, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.active</code> attribute.
	 * @return the active - Active Klarna 's checkout
	 */
	public Boolean isActive(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, ACTIVE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.active</code> attribute.
	 * @return the active - Active Klarna 's checkout
	 */
	public Boolean isActive()
	{
		return isActive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.active</code> attribute. 
	 * @return the active - Active Klarna 's checkout
	 */
	public boolean isActiveAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isActive( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.active</code> attribute. 
	 * @return the active - Active Klarna 's checkout
	 */
	public boolean isActiveAsPrimitive()
	{
		return isActiveAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's checkout
	 */
	public void setActive(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, ACTIVE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's checkout
	 */
	public void setActive(final Boolean value)
	{
		setActive( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's checkout
	 */
	public void setActive(final SessionContext ctx, final boolean value)
	{
		setActive( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.active</code> attribute. 
	 * @param value the active - Active Klarna 's checkout
	 */
	public void setActive(final boolean value)
	{
		setActive( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.attachementRequired</code> attribute.
	 * @return the attachementRequired - option for sending Attachements
	 */
	public Boolean isAttachementRequired(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, ATTACHEMENTREQUIRED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.attachementRequired</code> attribute.
	 * @return the attachementRequired - option for sending Attachements
	 */
	public Boolean isAttachementRequired()
	{
		return isAttachementRequired( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.attachementRequired</code> attribute. 
	 * @return the attachementRequired - option for sending Attachements
	 */
	public boolean isAttachementRequiredAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isAttachementRequired( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.attachementRequired</code> attribute. 
	 * @return the attachementRequired - option for sending Attachements
	 */
	public boolean isAttachementRequiredAsPrimitive()
	{
		return isAttachementRequiredAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.attachementRequired</code> attribute. 
	 * @param value the attachementRequired - option for sending Attachements
	 */
	public void setAttachementRequired(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, ATTACHEMENTREQUIRED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.attachementRequired</code> attribute. 
	 * @param value the attachementRequired - option for sending Attachements
	 */
	public void setAttachementRequired(final Boolean value)
	{
		setAttachementRequired( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.attachementRequired</code> attribute. 
	 * @param value the attachementRequired - option for sending Attachements
	 */
	public void setAttachementRequired(final SessionContext ctx, final boolean value)
	{
		setAttachementRequired( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.attachementRequired</code> attribute. 
	 * @param value the attachementRequired - option for sending Attachements
	 */
	public void setAttachementRequired(final boolean value)
	{
		setAttachementRequired( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.autoCapture</code> attribute.
	 * @return the autoCapture - Payment capture automatically
	 */
	public Boolean isAutoCapture(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, AUTOCAPTURE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.autoCapture</code> attribute.
	 * @return the autoCapture - Payment capture automatically
	 */
	public Boolean isAutoCapture()
	{
		return isAutoCapture( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.autoCapture</code> attribute. 
	 * @return the autoCapture - Payment capture automatically
	 */
	public boolean isAutoCaptureAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isAutoCapture( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.autoCapture</code> attribute. 
	 * @return the autoCapture - Payment capture automatically
	 */
	public boolean isAutoCaptureAsPrimitive()
	{
		return isAutoCaptureAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.autoCapture</code> attribute. 
	 * @param value the autoCapture - Payment capture automatically
	 */
	public void setAutoCapture(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, AUTOCAPTURE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.autoCapture</code> attribute. 
	 * @param value the autoCapture - Payment capture automatically
	 */
	public void setAutoCapture(final Boolean value)
	{
		setAutoCapture( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.autoCapture</code> attribute. 
	 * @param value the autoCapture - Payment capture automatically
	 */
	public void setAutoCapture(final SessionContext ctx, final boolean value)
	{
		setAutoCapture( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.autoCapture</code> attribute. 
	 * @param value the autoCapture - Payment capture automatically
	 */
	public void setAutoCapture(final boolean value)
	{
		setAutoCapture( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.code</code> attribute.
	 * @return the code - The Id of KlarnaConfiguration
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.code</code> attribute.
	 * @return the code - The Id of KlarnaConfiguration
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.code</code> attribute. 
	 * @param value the code - The Id of KlarnaConfiguration
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.code</code> attribute. 
	 * @param value the code - The Id of KlarnaConfiguration
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorBorder</code> attribute.
	 * @return the colorBorder - Color code for border
	 */
	public RBGColor getColorBorder(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORBORDER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorBorder</code> attribute.
	 * @return the colorBorder - Color code for border
	 */
	public RBGColor getColorBorder()
	{
		return getColorBorder( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorBorder</code> attribute. 
	 * @param value the colorBorder - Color code for border
	 */
	public void setColorBorder(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORBORDER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorBorder</code> attribute. 
	 * @param value the colorBorder - Color code for border
	 */
	public void setColorBorder(final RBGColor value)
	{
		setColorBorder( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorBorderSelected</code> attribute.
	 * @return the colorBorderSelected - Color code for border selected
	 */
	public RBGColor getColorBorderSelected(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORBORDERSELECTED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorBorderSelected</code> attribute.
	 * @return the colorBorderSelected - Color code for border selected
	 */
	public RBGColor getColorBorderSelected()
	{
		return getColorBorderSelected( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorBorderSelected</code> attribute. 
	 * @param value the colorBorderSelected - Color code for border selected
	 */
	public void setColorBorderSelected(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORBORDERSELECTED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorBorderSelected</code> attribute. 
	 * @param value the colorBorderSelected - Color code for border selected
	 */
	public void setColorBorderSelected(final RBGColor value)
	{
		setColorBorderSelected( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorButton</code> attribute.
	 * @return the colorButton - Color code for button
	 */
	public RBGColor getColorButton(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORBUTTON);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorButton</code> attribute.
	 * @return the colorButton - Color code for button
	 */
	public RBGColor getColorButton()
	{
		return getColorButton( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorButton</code> attribute. 
	 * @param value the colorButton - Color code for button
	 */
	public void setColorButton(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORBUTTON,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorButton</code> attribute. 
	 * @param value the colorButton - Color code for button
	 */
	public void setColorButton(final RBGColor value)
	{
		setColorButton( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorButtonText</code> attribute.
	 * @return the colorButtonText - Color code for button text
	 */
	public RBGColor getColorButtonText(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORBUTTONTEXT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorButtonText</code> attribute.
	 * @return the colorButtonText - Color code for button text
	 */
	public RBGColor getColorButtonText()
	{
		return getColorButtonText( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorButtonText</code> attribute. 
	 * @param value the colorButtonText - Color code for button text
	 */
	public void setColorButtonText(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORBUTTONTEXT,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorButtonText</code> attribute. 
	 * @param value the colorButtonText - Color code for button text
	 */
	public void setColorButtonText(final RBGColor value)
	{
		setColorButtonText( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorCheckbox</code> attribute.
	 * @return the colorCheckbox - Color code for checkbox
	 */
	public RBGColor getColorCheckbox(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORCHECKBOX);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorCheckbox</code> attribute.
	 * @return the colorCheckbox - Color code for checkbox
	 */
	public RBGColor getColorCheckbox()
	{
		return getColorCheckbox( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorCheckbox</code> attribute. 
	 * @param value the colorCheckbox - Color code for checkbox
	 */
	public void setColorCheckbox(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORCHECKBOX,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorCheckbox</code> attribute. 
	 * @param value the colorCheckbox - Color code for checkbox
	 */
	public void setColorCheckbox(final RBGColor value)
	{
		setColorCheckbox( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorCheckboxCheckMark</code> attribute.
	 * @return the colorCheckboxCheckMark - Color code for checkbox checkMark
	 */
	public RBGColor getColorCheckboxCheckMark(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORCHECKBOXCHECKMARK);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorCheckboxCheckMark</code> attribute.
	 * @return the colorCheckboxCheckMark - Color code for checkbox checkMark
	 */
	public RBGColor getColorCheckboxCheckMark()
	{
		return getColorCheckboxCheckMark( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorCheckboxCheckMark</code> attribute. 
	 * @param value the colorCheckboxCheckMark - Color code for checkbox checkMark
	 */
	public void setColorCheckboxCheckMark(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORCHECKBOXCHECKMARK,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorCheckboxCheckMark</code> attribute. 
	 * @param value the colorCheckboxCheckMark - Color code for checkbox checkMark
	 */
	public void setColorCheckboxCheckMark(final RBGColor value)
	{
		setColorCheckboxCheckMark( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorDetails</code> attribute.
	 * @return the colorDetails - Color code for Text
	 */
	public RBGColor getColorDetails(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORDETAILS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorDetails</code> attribute.
	 * @return the colorDetails - Color code for Text
	 */
	public RBGColor getColorDetails()
	{
		return getColorDetails( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorDetails</code> attribute. 
	 * @param value the colorDetails - Color code for Text
	 */
	public void setColorDetails(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORDETAILS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorDetails</code> attribute. 
	 * @param value the colorDetails - Color code for Text
	 */
	public void setColorDetails(final RBGColor value)
	{
		setColorDetails( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorHeader</code> attribute.
	 * @return the colorHeader - Color code for header
	 */
	public RBGColor getColorHeader(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORHEADER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorHeader</code> attribute.
	 * @return the colorHeader - Color code for header
	 */
	public RBGColor getColorHeader()
	{
		return getColorHeader( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorHeader</code> attribute. 
	 * @param value the colorHeader - Color code for header
	 */
	public void setColorHeader(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORHEADER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorHeader</code> attribute. 
	 * @param value the colorHeader - Color code for header
	 */
	public void setColorHeader(final RBGColor value)
	{
		setColorHeader( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorLink</code> attribute.
	 * @return the colorLink - Color code for link
	 */
	public RBGColor getColorLink(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORLINK);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorLink</code> attribute.
	 * @return the colorLink - Color code for link
	 */
	public RBGColor getColorLink()
	{
		return getColorLink( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorLink</code> attribute. 
	 * @param value the colorLink - Color code for link
	 */
	public void setColorLink(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORLINK,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorLink</code> attribute. 
	 * @param value the colorLink - Color code for link
	 */
	public void setColorLink(final RBGColor value)
	{
		setColorLink( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorText</code> attribute.
	 * @return the colorText - Color code for Text
	 */
	public RBGColor getColorText(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORTEXT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorText</code> attribute.
	 * @return the colorText - Color code for Text
	 */
	public RBGColor getColorText()
	{
		return getColorText( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorText</code> attribute. 
	 * @param value the colorText - Color code for Text
	 */
	public void setColorText(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORTEXT,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorText</code> attribute. 
	 * @param value the colorText - Color code for Text
	 */
	public void setColorText(final RBGColor value)
	{
		setColorText( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorTextSecondary</code> attribute.
	 * @return the colorTextSecondary - Color code for Text
	 */
	public RBGColor getColorTextSecondary(final SessionContext ctx)
	{
		return (RBGColor)getProperty( ctx, COLORTEXTSECONDARY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.colorTextSecondary</code> attribute.
	 * @return the colorTextSecondary - Color code for Text
	 */
	public RBGColor getColorTextSecondary()
	{
		return getColorTextSecondary( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorTextSecondary</code> attribute. 
	 * @param value the colorTextSecondary - Color code for Text
	 */
	public void setColorTextSecondary(final SessionContext ctx, final RBGColor value)
	{
		setProperty(ctx, COLORTEXTSECONDARY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.colorTextSecondary</code> attribute. 
	 * @param value the colorTextSecondary - Color code for Text
	 */
	public void setColorTextSecondary(final RBGColor value)
	{
		setColorTextSecondary( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.endpointMode</code> attribute.
	 * @return the endpointMode - Determines the endpoint mode of the merchant
	 */
	public EnumerationValue getEndpointMode(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, ENDPOINTMODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.endpointMode</code> attribute.
	 * @return the endpointMode - Determines the endpoint mode of the merchant
	 */
	public EnumerationValue getEndpointMode()
	{
		return getEndpointMode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.endpointMode</code> attribute. 
	 * @param value the endpointMode - Determines the endpoint mode of the merchant
	 */
	public void setEndpointMode(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, ENDPOINTMODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.endpointMode</code> attribute. 
	 * @param value the endpointMode - Determines the endpoint mode of the merchant
	 */
	public void setEndpointMode(final EnumerationValue value)
	{
		setEndpointMode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.endpointType</code> attribute.
	 * @return the endpointType - Determines the endpoint type of the merchant
	 */
	public EnumerationValue getEndpointType(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, ENDPOINTTYPE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.endpointType</code> attribute.
	 * @return the endpointType - Determines the endpoint type of the merchant
	 */
	public EnumerationValue getEndpointType()
	{
		return getEndpointType( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.endpointType</code> attribute. 
	 * @param value the endpointType - Determines the endpoint type of the merchant
	 */
	public void setEndpointType(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, ENDPOINTTYPE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.endpointType</code> attribute. 
	 * @param value the endpointType - Determines the endpoint type of the merchant
	 */
	public void setEndpointType(final EnumerationValue value)
	{
		setEndpointType( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute.
	 * @return the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public Boolean isIsVCNEnabled(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, ISVCNENABLED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute.
	 * @return the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public Boolean isIsVCNEnabled()
	{
		return isIsVCNEnabled( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute. 
	 * @return the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public boolean isIsVCNEnabledAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isIsVCNEnabled( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute. 
	 * @return the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public boolean isIsVCNEnabledAsPrimitive()
	{
		return isIsVCNEnabledAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute. 
	 * @param value the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public void setIsVCNEnabled(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, ISVCNENABLED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute. 
	 * @param value the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public void setIsVCNEnabled(final Boolean value)
	{
		setIsVCNEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute. 
	 * @param value the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public void setIsVCNEnabled(final SessionContext ctx, final boolean value)
	{
		setIsVCNEnabled( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.isVCNEnabled</code> attribute. 
	 * @param value the isVCNEnabled - To make VCN is enabled and disabled
	 */
	public void setIsVCNEnabled(final boolean value)
	{
		setIsVCNEnabled( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.merchantEmail</code> attribute.
	 * @return the merchantEmail - Merchant email address
	 */
	public String getMerchantEmail(final SessionContext ctx)
	{
		return (String)getProperty( ctx, MERCHANTEMAIL);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.merchantEmail</code> attribute.
	 * @return the merchantEmail - Merchant email address
	 */
	public String getMerchantEmail()
	{
		return getMerchantEmail( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.merchantEmail</code> attribute. 
	 * @param value the merchantEmail - Merchant email address
	 */
	public void setMerchantEmail(final SessionContext ctx, final String value)
	{
		setProperty(ctx, MERCHANTEMAIL,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.merchantEmail</code> attribute. 
	 * @param value the merchantEmail - Merchant email address
	 */
	public void setMerchantEmail(final String value)
	{
		setMerchantEmail( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.merchantID</code> attribute.
	 * @return the merchantID - Merchant ID
	 */
	public String getMerchantID(final SessionContext ctx)
	{
		return (String)getProperty( ctx, MERCHANTID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.merchantID</code> attribute.
	 * @return the merchantID - Merchant ID
	 */
	public String getMerchantID()
	{
		return getMerchantID( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.merchantID</code> attribute. 
	 * @param value the merchantID - Merchant ID
	 */
	public void setMerchantID(final SessionContext ctx, final String value)
	{
		setProperty(ctx, MERCHANTID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.merchantID</code> attribute. 
	 * @param value the merchantID - Merchant ID
	 */
	public void setMerchantID(final String value)
	{
		setMerchantID( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.merchantReference2</code> attribute.
	 * @return the merchantReference2 - Merchant Reference field2 from hybris
	 */
	public String getMerchantReference2(final SessionContext ctx)
	{
		return (String)getProperty( ctx, MERCHANTREFERENCE2);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.merchantReference2</code> attribute.
	 * @return the merchantReference2 - Merchant Reference field2 from hybris
	 */
	public String getMerchantReference2()
	{
		return getMerchantReference2( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.merchantReference2</code> attribute. 
	 * @param value the merchantReference2 - Merchant Reference field2 from hybris
	 */
	public void setMerchantReference2(final SessionContext ctx, final String value)
	{
		setProperty(ctx, MERCHANTREFERENCE2,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.merchantReference2</code> attribute. 
	 * @param value the merchantReference2 - Merchant Reference field2 from hybris
	 */
	public void setMerchantReference2(final String value)
	{
		setMerchantReference2( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.prodIdentifiers</code> attribute.
	 * @return the prodIdentifiers - Product Identifier mapping settings
	 */
	public ProductIdentifiers getProdIdentifiers(final SessionContext ctx)
	{
		return (ProductIdentifiers)getProperty( ctx, PRODIDENTIFIERS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.prodIdentifiers</code> attribute.
	 * @return the prodIdentifiers - Product Identifier mapping settings
	 */
	public ProductIdentifiers getProdIdentifiers()
	{
		return getProdIdentifiers( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.prodIdentifiers</code> attribute. 
	 * @param value the prodIdentifiers - Product Identifier mapping settings
	 */
	public void setProdIdentifiers(final SessionContext ctx, final ProductIdentifiers value)
	{
		setProperty(ctx, PRODIDENTIFIERS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.prodIdentifiers</code> attribute. 
	 * @param value the prodIdentifiers - Product Identifier mapping settings
	 */
	public void setProdIdentifiers(final ProductIdentifiers value)
	{
		setProdIdentifiers( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute.
	 * @return the productUrlsRequired - option for sending product url and image ur
	 */
	public Boolean isProductUrlsRequired(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, PRODUCTURLSREQUIRED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute.
	 * @return the productUrlsRequired - option for sending product url and image ur
	 */
	public Boolean isProductUrlsRequired()
	{
		return isProductUrlsRequired( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute. 
	 * @return the productUrlsRequired - option for sending product url and image ur
	 */
	public boolean isProductUrlsRequiredAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isProductUrlsRequired( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute. 
	 * @return the productUrlsRequired - option for sending product url and image ur
	 */
	public boolean isProductUrlsRequiredAsPrimitive()
	{
		return isProductUrlsRequiredAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute. 
	 * @param value the productUrlsRequired - option for sending product url and image ur
	 */
	public void setProductUrlsRequired(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, PRODUCTURLSREQUIRED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute. 
	 * @param value the productUrlsRequired - option for sending product url and image ur
	 */
	public void setProductUrlsRequired(final Boolean value)
	{
		setProductUrlsRequired( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute. 
	 * @param value the productUrlsRequired - option for sending product url and image ur
	 */
	public void setProductUrlsRequired(final SessionContext ctx, final boolean value)
	{
		setProductUrlsRequired( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.productUrlsRequired</code> attribute. 
	 * @param value the productUrlsRequired - option for sending product url and image ur
	 */
	public void setProductUrlsRequired(final boolean value)
	{
		setProductUrlsRequired( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.purchaseCurrency</code> attribute.
	 * @return the purchaseCurrency - Currency support by Klarna
	 */
	public Currency getPurchaseCurrency(final SessionContext ctx)
	{
		return (Currency)getProperty( ctx, PURCHASECURRENCY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.purchaseCurrency</code> attribute.
	 * @return the purchaseCurrency - Currency support by Klarna
	 */
	public Currency getPurchaseCurrency()
	{
		return getPurchaseCurrency( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.purchaseCurrency</code> attribute. 
	 * @param value the purchaseCurrency - Currency support by Klarna
	 */
	public void setPurchaseCurrency(final SessionContext ctx, final Currency value)
	{
		setProperty(ctx, PURCHASECURRENCY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.purchaseCurrency</code> attribute. 
	 * @param value the purchaseCurrency - Currency support by Klarna
	 */
	public void setPurchaseCurrency(final Currency value)
	{
		setPurchaseCurrency( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.radiusborder</code> attribute.
	 * @return the radiusborder - Color code for radius border
	 */
	public Integer getRadiusborder(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, RADIUSBORDER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.radiusborder</code> attribute.
	 * @return the radiusborder - Color code for radius border
	 */
	public Integer getRadiusborder()
	{
		return getRadiusborder( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.radiusborder</code> attribute. 
	 * @return the radiusborder - Color code for radius border
	 */
	public int getRadiusborderAsPrimitive(final SessionContext ctx)
	{
		Integer value = getRadiusborder( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.radiusborder</code> attribute. 
	 * @return the radiusborder - Color code for radius border
	 */
	public int getRadiusborderAsPrimitive()
	{
		return getRadiusborderAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.radiusborder</code> attribute. 
	 * @param value the radiusborder - Color code for radius border
	 */
	public void setRadiusborder(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, RADIUSBORDER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.radiusborder</code> attribute. 
	 * @param value the radiusborder - Color code for radius border
	 */
	public void setRadiusborder(final Integer value)
	{
		setRadiusborder( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.radiusborder</code> attribute. 
	 * @param value the radiusborder - Color code for radius border
	 */
	public void setRadiusborder(final SessionContext ctx, final int value)
	{
		setRadiusborder( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.radiusborder</code> attribute. 
	 * @param value the radiusborder - Color code for radius border
	 */
	public void setRadiusborder(final int value)
	{
		setRadiusborder( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.sharedSecret</code> attribute.
	 * @return the sharedSecret - Merchant password
	 */
	public String getSharedSecret(final SessionContext ctx)
	{
		return (String)getProperty( ctx, SHAREDSECRET);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.sharedSecret</code> attribute.
	 * @return the sharedSecret - Merchant password
	 */
	public String getSharedSecret()
	{
		return getSharedSecret( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.sharedSecret</code> attribute. 
	 * @param value the sharedSecret - Merchant password
	 */
	public void setSharedSecret(final SessionContext ctx, final String value)
	{
		setProperty(ctx, SHAREDSECRET,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.sharedSecret</code> attribute. 
	 * @param value the sharedSecret - Merchant password
	 */
	public void setSharedSecret(final String value)
	{
		setSharedSecret( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.vcnKeyID</code> attribute.
	 * @return the vcnKeyID - Key ID in settlement request
	 */
	public String getVcnKeyID(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VCNKEYID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.vcnKeyID</code> attribute.
	 * @return the vcnKeyID - Key ID in settlement request
	 */
	public String getVcnKeyID()
	{
		return getVcnKeyID( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.vcnKeyID</code> attribute. 
	 * @param value the vcnKeyID - Key ID in settlement request
	 */
	public void setVcnKeyID(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VCNKEYID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.vcnKeyID</code> attribute. 
	 * @param value the vcnKeyID - Key ID in settlement request
	 */
	public void setVcnKeyID(final String value)
	{
		setVcnKeyID( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.vcnPrivateKey</code> attribute.
	 * @return the vcnPrivateKey - Your 4096 bit RSA Private Key
	 */
	public String getVcnPrivateKey(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VCNPRIVATEKEY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.vcnPrivateKey</code> attribute.
	 * @return the vcnPrivateKey - Your 4096 bit RSA Private Key
	 */
	public String getVcnPrivateKey()
	{
		return getVcnPrivateKey( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.vcnPrivateKey</code> attribute. 
	 * @param value the vcnPrivateKey - Your 4096 bit RSA Private Key
	 */
	public void setVcnPrivateKey(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VCNPRIVATEKEY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.vcnPrivateKey</code> attribute. 
	 * @param value the vcnPrivateKey - Your 4096 bit RSA Private Key
	 */
	public void setVcnPrivateKey(final String value)
	{
		setVcnPrivateKey( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.vcnPublicKey</code> attribute.
	 * @return the vcnPublicKey - Your 4096 bit RSA Public Key
	 */
	public String getVcnPublicKey(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VCNPUBLICKEY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KlarnaPayConfig.vcnPublicKey</code> attribute.
	 * @return the vcnPublicKey - Your 4096 bit RSA Public Key
	 */
	public String getVcnPublicKey()
	{
		return getVcnPublicKey( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.vcnPublicKey</code> attribute. 
	 * @param value the vcnPublicKey - Your 4096 bit RSA Public Key
	 */
	public void setVcnPublicKey(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VCNPUBLICKEY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KlarnaPayConfig.vcnPublicKey</code> attribute. 
	 * @param value the vcnPublicKey - Your 4096 bit RSA Public Key
	 */
	public void setVcnPublicKey(final String value)
	{
		setVcnPublicKey( getSession().getSessionContext(), value );
	}
	
}

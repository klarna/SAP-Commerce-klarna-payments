/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.payment.PaymentInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.klarna.payment.jalo.KlarnaPaymentInfo KPPaymentInfo}.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedKlarnaPaymentInfo extends PaymentInfo
{
	/** Qualifier of the <code>KPPaymentInfo.paymentOption</code> attribute **/
	public static final String PAYMENTOPTION = "paymentOption";
	/** Qualifier of the <code>KPPaymentInfo.finalizeRequired</code> attribute **/
	public static final String FINALIZEREQUIRED = "finalizeRequired";
	/** Qualifier of the <code>KPPaymentInfo.description</code> attribute **/
	public static final String DESCRIPTION = "description";
	/** Qualifier of the <code>KPPaymentInfo.authToken</code> attribute **/
	public static final String AUTHTOKEN = "authToken";
	/** Qualifier of the <code>KPPaymentInfo.isVCNUsed</code> attribute **/
	public static final String ISVCNUSED = "isVCNUsed";
	/** Qualifier of the <code>KPPaymentInfo.vcnBrand</code> attribute **/
	public static final String VCNBRAND = "vcnBrand";
	/** Qualifier of the <code>KPPaymentInfo.vcnCSC</code> attribute **/
	public static final String VCNCSC = "vcnCSC";
	/** Qualifier of the <code>KPPaymentInfo.vcnHolder</code> attribute **/
	public static final String VCNHOLDER = "vcnHolder";
	/** Qualifier of the <code>KPPaymentInfo.kpVCNPCIData</code> attribute **/
	public static final String KPVCNPCIDATA = "kpVCNPCIData";
	/** Qualifier of the <code>KPPaymentInfo.kpVCNIV</code> attribute **/
	public static final String KPVCNIV = "kpVCNIV";
	/** Qualifier of the <code>KPPaymentInfo.kpVCNAESKey</code> attribute **/
	public static final String KPVCNAESKEY = "kpVCNAESKey";
	/** Qualifier of the <code>KPPaymentInfo.kpVCNCardID</code> attribute **/
	public static final String KPVCNCARDID = "kpVCNCardID";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(PaymentInfo.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(PAYMENTOPTION, AttributeMode.INITIAL);
		tmp.put(FINALIZEREQUIRED, AttributeMode.INITIAL);
		tmp.put(DESCRIPTION, AttributeMode.INITIAL);
		tmp.put(AUTHTOKEN, AttributeMode.INITIAL);
		tmp.put(ISVCNUSED, AttributeMode.INITIAL);
		tmp.put(VCNBRAND, AttributeMode.INITIAL);
		tmp.put(VCNCSC, AttributeMode.INITIAL);
		tmp.put(VCNHOLDER, AttributeMode.INITIAL);
		tmp.put(KPVCNPCIDATA, AttributeMode.INITIAL);
		tmp.put(KPVCNIV, AttributeMode.INITIAL);
		tmp.put(KPVCNAESKEY, AttributeMode.INITIAL);
		tmp.put(KPVCNCARDID, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.authToken</code> attribute.
	 * @return the authToken
	 */
	public String getAuthToken(final SessionContext ctx)
	{
		return (String)getProperty( ctx, AUTHTOKEN);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.authToken</code> attribute.
	 * @return the authToken
	 */
	public String getAuthToken()
	{
		return getAuthToken( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.authToken</code> attribute. 
	 * @param value the authToken
	 */
	public void setAuthToken(final SessionContext ctx, final String value)
	{
		setProperty(ctx, AUTHTOKEN,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.authToken</code> attribute. 
	 * @param value the authToken
	 */
	public void setAuthToken(final String value)
	{
		setAuthToken( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.description</code> attribute.
	 * @return the description
	 */
	public String getDescription(final SessionContext ctx)
	{
		return (String)getProperty( ctx, DESCRIPTION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.description</code> attribute.
	 * @return the description
	 */
	public String getDescription()
	{
		return getDescription( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.description</code> attribute. 
	 * @param value the description
	 */
	public void setDescription(final SessionContext ctx, final String value)
	{
		setProperty(ctx, DESCRIPTION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.description</code> attribute. 
	 * @param value the description
	 */
	public void setDescription(final String value)
	{
		setDescription( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.finalizeRequired</code> attribute.
	 * @return the finalizeRequired
	 */
	public Boolean isFinalizeRequired(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, FINALIZEREQUIRED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.finalizeRequired</code> attribute.
	 * @return the finalizeRequired
	 */
	public Boolean isFinalizeRequired()
	{
		return isFinalizeRequired( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.finalizeRequired</code> attribute. 
	 * @return the finalizeRequired
	 */
	public boolean isFinalizeRequiredAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isFinalizeRequired( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.finalizeRequired</code> attribute. 
	 * @return the finalizeRequired
	 */
	public boolean isFinalizeRequiredAsPrimitive()
	{
		return isFinalizeRequiredAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.finalizeRequired</code> attribute. 
	 * @param value the finalizeRequired
	 */
	public void setFinalizeRequired(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, FINALIZEREQUIRED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.finalizeRequired</code> attribute. 
	 * @param value the finalizeRequired
	 */
	public void setFinalizeRequired(final Boolean value)
	{
		setFinalizeRequired( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.finalizeRequired</code> attribute. 
	 * @param value the finalizeRequired
	 */
	public void setFinalizeRequired(final SessionContext ctx, final boolean value)
	{
		setFinalizeRequired( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.finalizeRequired</code> attribute. 
	 * @param value the finalizeRequired
	 */
	public void setFinalizeRequired(final boolean value)
	{
		setFinalizeRequired( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.isVCNUsed</code> attribute.
	 * @return the isVCNUsed
	 */
	public Boolean isIsVCNUsed(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, ISVCNUSED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.isVCNUsed</code> attribute.
	 * @return the isVCNUsed
	 */
	public Boolean isIsVCNUsed()
	{
		return isIsVCNUsed( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.isVCNUsed</code> attribute. 
	 * @return the isVCNUsed
	 */
	public boolean isIsVCNUsedAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isIsVCNUsed( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.isVCNUsed</code> attribute. 
	 * @return the isVCNUsed
	 */
	public boolean isIsVCNUsedAsPrimitive()
	{
		return isIsVCNUsedAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.isVCNUsed</code> attribute. 
	 * @param value the isVCNUsed
	 */
	public void setIsVCNUsed(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, ISVCNUSED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.isVCNUsed</code> attribute. 
	 * @param value the isVCNUsed
	 */
	public void setIsVCNUsed(final Boolean value)
	{
		setIsVCNUsed( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.isVCNUsed</code> attribute. 
	 * @param value the isVCNUsed
	 */
	public void setIsVCNUsed(final SessionContext ctx, final boolean value)
	{
		setIsVCNUsed( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.isVCNUsed</code> attribute. 
	 * @param value the isVCNUsed
	 */
	public void setIsVCNUsed(final boolean value)
	{
		setIsVCNUsed( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNAESKey</code> attribute.
	 * @return the kpVCNAESKey
	 */
	public String getKpVCNAESKey(final SessionContext ctx)
	{
		return (String)getProperty( ctx, KPVCNAESKEY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNAESKey</code> attribute.
	 * @return the kpVCNAESKey
	 */
	public String getKpVCNAESKey()
	{
		return getKpVCNAESKey( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNAESKey</code> attribute. 
	 * @param value the kpVCNAESKey
	 */
	public void setKpVCNAESKey(final SessionContext ctx, final String value)
	{
		setProperty(ctx, KPVCNAESKEY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNAESKey</code> attribute. 
	 * @param value the kpVCNAESKey
	 */
	public void setKpVCNAESKey(final String value)
	{
		setKpVCNAESKey( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNCardID</code> attribute.
	 * @return the kpVCNCardID
	 */
	public String getKpVCNCardID(final SessionContext ctx)
	{
		return (String)getProperty( ctx, KPVCNCARDID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNCardID</code> attribute.
	 * @return the kpVCNCardID
	 */
	public String getKpVCNCardID()
	{
		return getKpVCNCardID( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNCardID</code> attribute. 
	 * @param value the kpVCNCardID
	 */
	public void setKpVCNCardID(final SessionContext ctx, final String value)
	{
		setProperty(ctx, KPVCNCARDID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNCardID</code> attribute. 
	 * @param value the kpVCNCardID
	 */
	public void setKpVCNCardID(final String value)
	{
		setKpVCNCardID( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNIV</code> attribute.
	 * @return the kpVCNIV
	 */
	public String getKpVCNIV(final SessionContext ctx)
	{
		return (String)getProperty( ctx, KPVCNIV);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNIV</code> attribute.
	 * @return the kpVCNIV
	 */
	public String getKpVCNIV()
	{
		return getKpVCNIV( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNIV</code> attribute. 
	 * @param value the kpVCNIV
	 */
	public void setKpVCNIV(final SessionContext ctx, final String value)
	{
		setProperty(ctx, KPVCNIV,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNIV</code> attribute. 
	 * @param value the kpVCNIV
	 */
	public void setKpVCNIV(final String value)
	{
		setKpVCNIV( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNPCIData</code> attribute.
	 * @return the kpVCNPCIData
	 */
	public String getKpVCNPCIData(final SessionContext ctx)
	{
		return (String)getProperty( ctx, KPVCNPCIDATA);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.kpVCNPCIData</code> attribute.
	 * @return the kpVCNPCIData
	 */
	public String getKpVCNPCIData()
	{
		return getKpVCNPCIData( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNPCIData</code> attribute. 
	 * @param value the kpVCNPCIData
	 */
	public void setKpVCNPCIData(final SessionContext ctx, final String value)
	{
		setProperty(ctx, KPVCNPCIDATA,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.kpVCNPCIData</code> attribute. 
	 * @param value the kpVCNPCIData
	 */
	public void setKpVCNPCIData(final String value)
	{
		setKpVCNPCIData( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.paymentOption</code> attribute.
	 * @return the paymentOption
	 */
	public String getPaymentOption(final SessionContext ctx)
	{
		return (String)getProperty( ctx, PAYMENTOPTION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.paymentOption</code> attribute.
	 * @return the paymentOption
	 */
	public String getPaymentOption()
	{
		return getPaymentOption( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.paymentOption</code> attribute. 
	 * @param value the paymentOption
	 */
	public void setPaymentOption(final SessionContext ctx, final String value)
	{
		setProperty(ctx, PAYMENTOPTION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.paymentOption</code> attribute. 
	 * @param value the paymentOption
	 */
	public void setPaymentOption(final String value)
	{
		setPaymentOption( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.vcnBrand</code> attribute.
	 * @return the vcnBrand - VCN Brand
	 */
	public String getVcnBrand(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VCNBRAND);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.vcnBrand</code> attribute.
	 * @return the vcnBrand - VCN Brand
	 */
	public String getVcnBrand()
	{
		return getVcnBrand( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.vcnBrand</code> attribute. 
	 * @param value the vcnBrand - VCN Brand
	 */
	public void setVcnBrand(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VCNBRAND,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.vcnBrand</code> attribute. 
	 * @param value the vcnBrand - VCN Brand
	 */
	public void setVcnBrand(final String value)
	{
		setVcnBrand( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.vcnCSC</code> attribute.
	 * @return the vcnCSC - option Vcn CSC
	 */
	public String getVcnCSC(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VCNCSC);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.vcnCSC</code> attribute.
	 * @return the vcnCSC - option Vcn CSC
	 */
	public String getVcnCSC()
	{
		return getVcnCSC( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.vcnCSC</code> attribute. 
	 * @param value the vcnCSC - option Vcn CSC
	 */
	public void setVcnCSC(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VCNCSC,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.vcnCSC</code> attribute. 
	 * @param value the vcnCSC - option Vcn CSC
	 */
	public void setVcnCSC(final String value)
	{
		setVcnCSC( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.vcnHolder</code> attribute.
	 * @return the vcnHolder
	 */
	public String getVcnHolder(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VCNHOLDER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>KPPaymentInfo.vcnHolder</code> attribute.
	 * @return the vcnHolder
	 */
	public String getVcnHolder()
	{
		return getVcnHolder( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.vcnHolder</code> attribute. 
	 * @param value the vcnHolder
	 */
	public void setVcnHolder(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VCNHOLDER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>KPPaymentInfo.vcnHolder</code> attribute. 
	 * @param value the vcnHolder
	 */
	public void setVcnHolder(final String value)
	{
		setVcnHolder( getSession().getSessionContext(), value );
	}
	
}

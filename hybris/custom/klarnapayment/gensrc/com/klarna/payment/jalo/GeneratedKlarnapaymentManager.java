/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.jalo.KlarnaPayConfig;
import com.klarna.payment.jalo.KlarnaPaymentInfo;
import com.klarna.payment.jalo.OrderFailedEmailProcess;
import com.klarna.payment.jalo.ProductIdentifiers;
import com.klarna.payment.jalo.RBGColor;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.store.BaseStore;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type <code>KlarnapaymentManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedKlarnapaymentManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("klarnaPayConfig", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.store.BaseStore", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("kpIdentifier", AttributeMode.INITIAL);
		tmp.put("kpAnonymousGUID", AttributeMode.INITIAL);
		tmp.put("kpOrderId", AttributeMode.INITIAL);
		tmp.put("kpFraudStatus", AttributeMode.INITIAL);
		tmp.put("isKpPendingOrder", AttributeMode.INITIAL);
		tmp.put("isAutoCaptured", AttributeMode.INITIAL);
		tmp.put("isKpAuthorised", AttributeMode.INITIAL);
		tmp.put("isKpFraudRiskStopped", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.AbstractOrder", Collections.unmodifiableMap(tmp));
		DEFAULT_INITIAL_ATTRIBUTES = ttmp;
	}
	@Override
	public Map<String, AttributeMode> getDefaultAttributeModes(final Class<? extends Item> itemClass)
	{
		Map<String, AttributeMode> ret = new HashMap<>();
		final Map<String, AttributeMode> attr = DEFAULT_INITIAL_ATTRIBUTES.get(itemClass.getName());
		if (attr != null)
		{
			ret.putAll(attr);
		}
		return ret;
	}
	
	public KlarnaPayConfig createKlarnaPayConfig(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( KlarnapaymentConstants.TC.KLARNAPAYCONFIG );
			return (KlarnaPayConfig)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating KlarnaPayConfig : "+e.getMessage(), 0 );
		}
	}
	
	public KlarnaPayConfig createKlarnaPayConfig(final Map attributeValues)
	{
		return createKlarnaPayConfig( getSession().getSessionContext(), attributeValues );
	}
	
	public KlarnaPaymentInfo createKPPaymentInfo(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( KlarnapaymentConstants.TC.KPPAYMENTINFO );
			return (KlarnaPaymentInfo)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating KPPaymentInfo : "+e.getMessage(), 0 );
		}
	}
	
	public KlarnaPaymentInfo createKPPaymentInfo(final Map attributeValues)
	{
		return createKPPaymentInfo( getSession().getSessionContext(), attributeValues );
	}
	
	public OrderFailedEmailProcess createOrderFailedEmailProcess(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( KlarnapaymentConstants.TC.ORDERFAILEDEMAILPROCESS );
			return (OrderFailedEmailProcess)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating OrderFailedEmailProcess : "+e.getMessage(), 0 );
		}
	}
	
	public OrderFailedEmailProcess createOrderFailedEmailProcess(final Map attributeValues)
	{
		return createOrderFailedEmailProcess( getSession().getSessionContext(), attributeValues );
	}
	
	public ProductIdentifiers createProductIdentifiers(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( KlarnapaymentConstants.TC.PRODUCTIDENTIFIERS );
			return (ProductIdentifiers)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating ProductIdentifiers : "+e.getMessage(), 0 );
		}
	}
	
	public ProductIdentifiers createProductIdentifiers(final Map attributeValues)
	{
		return createProductIdentifiers( getSession().getSessionContext(), attributeValues );
	}
	
	public RBGColor createRBGColor(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( KlarnapaymentConstants.TC.RBGCOLOR );
			return (RBGColor)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating RBGColor : "+e.getMessage(), 0 );
		}
	}
	
	public RBGColor createRBGColor(final Map attributeValues)
	{
		return createRBGColor( getSession().getSessionContext(), attributeValues );
	}
	
	@Override
	public String getName()
	{
		return KlarnapaymentConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isAutoCaptured</code> attribute.
	 * @return the isAutoCaptured
	 */
	public Boolean isIsAutoCaptured(final SessionContext ctx, final AbstractOrder item)
	{
		return (Boolean)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISAUTOCAPTURED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isAutoCaptured</code> attribute.
	 * @return the isAutoCaptured
	 */
	public Boolean isIsAutoCaptured(final AbstractOrder item)
	{
		return isIsAutoCaptured( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isAutoCaptured</code> attribute. 
	 * @return the isAutoCaptured
	 */
	public boolean isIsAutoCapturedAsPrimitive(final SessionContext ctx, final AbstractOrder item)
	{
		Boolean value = isIsAutoCaptured( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isAutoCaptured</code> attribute. 
	 * @return the isAutoCaptured
	 */
	public boolean isIsAutoCapturedAsPrimitive(final AbstractOrder item)
	{
		return isIsAutoCapturedAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isAutoCaptured</code> attribute. 
	 * @param value the isAutoCaptured
	 */
	public void setIsAutoCaptured(final SessionContext ctx, final AbstractOrder item, final Boolean value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISAUTOCAPTURED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isAutoCaptured</code> attribute. 
	 * @param value the isAutoCaptured
	 */
	public void setIsAutoCaptured(final AbstractOrder item, final Boolean value)
	{
		setIsAutoCaptured( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isAutoCaptured</code> attribute. 
	 * @param value the isAutoCaptured
	 */
	public void setIsAutoCaptured(final SessionContext ctx, final AbstractOrder item, final boolean value)
	{
		setIsAutoCaptured( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isAutoCaptured</code> attribute. 
	 * @param value the isAutoCaptured
	 */
	public void setIsAutoCaptured(final AbstractOrder item, final boolean value)
	{
		setIsAutoCaptured( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpAuthorised</code> attribute.
	 * @return the isKpAuthorised
	 */
	public Boolean isIsKpAuthorised(final SessionContext ctx, final AbstractOrder item)
	{
		return (Boolean)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISKPAUTHORISED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpAuthorised</code> attribute.
	 * @return the isKpAuthorised
	 */
	public Boolean isIsKpAuthorised(final AbstractOrder item)
	{
		return isIsKpAuthorised( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpAuthorised</code> attribute. 
	 * @return the isKpAuthorised
	 */
	public boolean isIsKpAuthorisedAsPrimitive(final SessionContext ctx, final AbstractOrder item)
	{
		Boolean value = isIsKpAuthorised( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpAuthorised</code> attribute. 
	 * @return the isKpAuthorised
	 */
	public boolean isIsKpAuthorisedAsPrimitive(final AbstractOrder item)
	{
		return isIsKpAuthorisedAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpAuthorised</code> attribute. 
	 * @param value the isKpAuthorised
	 */
	public void setIsKpAuthorised(final SessionContext ctx, final AbstractOrder item, final Boolean value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISKPAUTHORISED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpAuthorised</code> attribute. 
	 * @param value the isKpAuthorised
	 */
	public void setIsKpAuthorised(final AbstractOrder item, final Boolean value)
	{
		setIsKpAuthorised( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpAuthorised</code> attribute. 
	 * @param value the isKpAuthorised
	 */
	public void setIsKpAuthorised(final SessionContext ctx, final AbstractOrder item, final boolean value)
	{
		setIsKpAuthorised( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpAuthorised</code> attribute. 
	 * @param value the isKpAuthorised
	 */
	public void setIsKpAuthorised(final AbstractOrder item, final boolean value)
	{
		setIsKpAuthorised( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute.
	 * @return the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public Boolean isIsKpFraudRiskStopped(final SessionContext ctx, final AbstractOrder item)
	{
		return (Boolean)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISKPFRAUDRISKSTOPPED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute.
	 * @return the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public Boolean isIsKpFraudRiskStopped(final AbstractOrder item)
	{
		return isIsKpFraudRiskStopped( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute. 
	 * @return the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public boolean isIsKpFraudRiskStoppedAsPrimitive(final SessionContext ctx, final AbstractOrder item)
	{
		Boolean value = isIsKpFraudRiskStopped( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute. 
	 * @return the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public boolean isIsKpFraudRiskStoppedAsPrimitive(final AbstractOrder item)
	{
		return isIsKpFraudRiskStoppedAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute. 
	 * @param value the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public void setIsKpFraudRiskStopped(final SessionContext ctx, final AbstractOrder item, final Boolean value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISKPFRAUDRISKSTOPPED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute. 
	 * @param value the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public void setIsKpFraudRiskStopped(final AbstractOrder item, final Boolean value)
	{
		setIsKpFraudRiskStopped( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute. 
	 * @param value the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public void setIsKpFraudRiskStopped(final SessionContext ctx, final AbstractOrder item, final boolean value)
	{
		setIsKpFraudRiskStopped( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpFraudRiskStopped</code> attribute. 
	 * @param value the isKpFraudRiskStopped - Shipment able to be cancelled upon receipt of Fraud Stopped notification from Klarna
	 */
	public void setIsKpFraudRiskStopped(final AbstractOrder item, final boolean value)
	{
		setIsKpFraudRiskStopped( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpPendingOrder</code> attribute.
	 * @return the isKpPendingOrder
	 */
	public Boolean isIsKpPendingOrder(final SessionContext ctx, final AbstractOrder item)
	{
		return (Boolean)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISKPPENDINGORDER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpPendingOrder</code> attribute.
	 * @return the isKpPendingOrder
	 */
	public Boolean isIsKpPendingOrder(final AbstractOrder item)
	{
		return isIsKpPendingOrder( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpPendingOrder</code> attribute. 
	 * @return the isKpPendingOrder
	 */
	public boolean isIsKpPendingOrderAsPrimitive(final SessionContext ctx, final AbstractOrder item)
	{
		Boolean value = isIsKpPendingOrder( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.isKpPendingOrder</code> attribute. 
	 * @return the isKpPendingOrder
	 */
	public boolean isIsKpPendingOrderAsPrimitive(final AbstractOrder item)
	{
		return isIsKpPendingOrderAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpPendingOrder</code> attribute. 
	 * @param value the isKpPendingOrder
	 */
	public void setIsKpPendingOrder(final SessionContext ctx, final AbstractOrder item, final Boolean value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.ISKPPENDINGORDER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpPendingOrder</code> attribute. 
	 * @param value the isKpPendingOrder
	 */
	public void setIsKpPendingOrder(final AbstractOrder item, final Boolean value)
	{
		setIsKpPendingOrder( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpPendingOrder</code> attribute. 
	 * @param value the isKpPendingOrder
	 */
	public void setIsKpPendingOrder(final SessionContext ctx, final AbstractOrder item, final boolean value)
	{
		setIsKpPendingOrder( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.isKpPendingOrder</code> attribute. 
	 * @param value the isKpPendingOrder
	 */
	public void setIsKpPendingOrder(final AbstractOrder item, final boolean value)
	{
		setIsKpPendingOrder( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>BaseStore.klarnaPayConfig</code> attribute.
	 * @return the klarnaPayConfig
	 */
	public KlarnaPayConfig getKlarnaPayConfig(final SessionContext ctx, final BaseStore item)
	{
		return (KlarnaPayConfig)item.getProperty( ctx, KlarnapaymentConstants.Attributes.BaseStore.KLARNAPAYCONFIG);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>BaseStore.klarnaPayConfig</code> attribute.
	 * @return the klarnaPayConfig
	 */
	public KlarnaPayConfig getKlarnaPayConfig(final BaseStore item)
	{
		return getKlarnaPayConfig( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>BaseStore.klarnaPayConfig</code> attribute. 
	 * @param value the klarnaPayConfig
	 */
	public void setKlarnaPayConfig(final SessionContext ctx, final BaseStore item, final KlarnaPayConfig value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.BaseStore.KLARNAPAYCONFIG,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>BaseStore.klarnaPayConfig</code> attribute. 
	 * @param value the klarnaPayConfig
	 */
	public void setKlarnaPayConfig(final BaseStore item, final KlarnaPayConfig value)
	{
		setKlarnaPayConfig( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpAnonymousGUID</code> attribute.
	 * @return the kpAnonymousGUID - Anonymous user GUID
	 */
	public String getKpAnonymousGUID(final SessionContext ctx, final AbstractOrder item)
	{
		return (String)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPANONYMOUSGUID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpAnonymousGUID</code> attribute.
	 * @return the kpAnonymousGUID - Anonymous user GUID
	 */
	public String getKpAnonymousGUID(final AbstractOrder item)
	{
		return getKpAnonymousGUID( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpAnonymousGUID</code> attribute. 
	 * @param value the kpAnonymousGUID - Anonymous user GUID
	 */
	public void setKpAnonymousGUID(final SessionContext ctx, final AbstractOrder item, final String value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPANONYMOUSGUID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpAnonymousGUID</code> attribute. 
	 * @param value the kpAnonymousGUID - Anonymous user GUID
	 */
	public void setKpAnonymousGUID(final AbstractOrder item, final String value)
	{
		setKpAnonymousGUID( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpFraudStatus</code> attribute.
	 * @return the kpFraudStatus - Klarna Fraud Status
	 */
	public String getKpFraudStatus(final SessionContext ctx, final AbstractOrder item)
	{
		return (String)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPFRAUDSTATUS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpFraudStatus</code> attribute.
	 * @return the kpFraudStatus - Klarna Fraud Status
	 */
	public String getKpFraudStatus(final AbstractOrder item)
	{
		return getKpFraudStatus( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpFraudStatus</code> attribute. 
	 * @param value the kpFraudStatus - Klarna Fraud Status
	 */
	public void setKpFraudStatus(final SessionContext ctx, final AbstractOrder item, final String value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPFRAUDSTATUS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpFraudStatus</code> attribute. 
	 * @param value the kpFraudStatus - Klarna Fraud Status
	 */
	public void setKpFraudStatus(final AbstractOrder item, final String value)
	{
		setKpFraudStatus( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpIdentifier</code> attribute.
	 * @return the kpIdentifier - Klarna Payment identifiere- Cart ID suffixed with KLARNA
	 */
	public String getKpIdentifier(final SessionContext ctx, final AbstractOrder item)
	{
		return (String)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPIDENTIFIER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpIdentifier</code> attribute.
	 * @return the kpIdentifier - Klarna Payment identifiere- Cart ID suffixed with KLARNA
	 */
	public String getKpIdentifier(final AbstractOrder item)
	{
		return getKpIdentifier( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpIdentifier</code> attribute. 
	 * @param value the kpIdentifier - Klarna Payment identifiere- Cart ID suffixed with KLARNA
	 */
	public void setKpIdentifier(final SessionContext ctx, final AbstractOrder item, final String value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPIDENTIFIER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpIdentifier</code> attribute. 
	 * @param value the kpIdentifier - Klarna Payment identifiere- Cart ID suffixed with KLARNA
	 */
	public void setKpIdentifier(final AbstractOrder item, final String value)
	{
		setKpIdentifier( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpOrderId</code> attribute.
	 * @return the kpOrderId - Klarna Order ID
	 */
	public String getKpOrderId(final SessionContext ctx, final AbstractOrder item)
	{
		return (String)item.getProperty( ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPORDERID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.kpOrderId</code> attribute.
	 * @return the kpOrderId - Klarna Order ID
	 */
	public String getKpOrderId(final AbstractOrder item)
	{
		return getKpOrderId( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpOrderId</code> attribute. 
	 * @param value the kpOrderId - Klarna Order ID
	 */
	public void setKpOrderId(final SessionContext ctx, final AbstractOrder item, final String value)
	{
		item.setProperty(ctx, KlarnapaymentConstants.Attributes.AbstractOrder.KPORDERID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.kpOrderId</code> attribute. 
	 * @param value the kpOrderId - Klarna Order ID
	 */
	public void setKpOrderId(final AbstractOrder item, final String value)
	{
		setKpOrderId( getSession().getSessionContext(), item, value );
	}
	
}

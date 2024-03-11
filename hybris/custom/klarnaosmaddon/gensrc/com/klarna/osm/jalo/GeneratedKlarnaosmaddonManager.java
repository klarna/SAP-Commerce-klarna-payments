/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.osm.jalo;

import com.klarna.osm.constants.KlarnaosmaddonConstants;
import com.klarna.osm.jalo.KlarnaOSMConfig;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.store.BaseStore;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type <code>KlarnaosmaddonManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedKlarnaosmaddonManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("klarnaOsmConfig", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.store.BaseStore", Collections.unmodifiableMap(tmp));
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
	
	public KlarnaOSMConfig createKlarnaOSMConfig(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( KlarnaosmaddonConstants.TC.KLARNAOSMCONFIG );
			return (KlarnaOSMConfig)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating KlarnaOSMConfig : "+e.getMessage(), 0 );
		}
	}
	
	public KlarnaOSMConfig createKlarnaOSMConfig(final Map attributeValues)
	{
		return createKlarnaOSMConfig( getSession().getSessionContext(), attributeValues );
	}
	
	@Override
	public String getName()
	{
		return KlarnaosmaddonConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>BaseStore.klarnaOsmConfig</code> attribute.
	 * @return the klarnaOsmConfig
	 */
	public KlarnaOSMConfig getKlarnaOsmConfig(final SessionContext ctx, final BaseStore item)
	{
		return (KlarnaOSMConfig)item.getProperty( ctx, KlarnaosmaddonConstants.Attributes.BaseStore.KLARNAOSMCONFIG);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>BaseStore.klarnaOsmConfig</code> attribute.
	 * @return the klarnaOsmConfig
	 */
	public KlarnaOSMConfig getKlarnaOsmConfig(final BaseStore item)
	{
		return getKlarnaOsmConfig( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>BaseStore.klarnaOsmConfig</code> attribute. 
	 * @param value the klarnaOsmConfig
	 */
	public void setKlarnaOsmConfig(final SessionContext ctx, final BaseStore item, final KlarnaOSMConfig value)
	{
		item.setProperty(ctx, KlarnaosmaddonConstants.Attributes.BaseStore.KLARNAOSMCONFIG,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>BaseStore.klarnaOsmConfig</code> attribute. 
	 * @param value the klarnaOsmConfig
	 */
	public void setKlarnaOsmConfig(final BaseStore item, final KlarnaOSMConfig value)
	{
		setKlarnaOsmConfig( getSession().getSessionContext(), item, value );
	}
	
}

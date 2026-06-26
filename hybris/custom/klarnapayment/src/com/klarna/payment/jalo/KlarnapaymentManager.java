package com.klarna.payment.jalo;

import com.klarna.payment.constants.KlarnapaymentConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD")
public class KlarnapaymentManager extends GeneratedKlarnapaymentManager
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger( KlarnapaymentManager.class.getName() );
	
	public static final KlarnapaymentManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (KlarnapaymentManager) em.getExtension(KlarnapaymentConstants.EXTENSIONNAME);
	}
	
}

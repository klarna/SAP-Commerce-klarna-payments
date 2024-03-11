/**
 *
 */
package com.klarna.payment.services.impl;

import de.hybris.platform.core.model.user.TitleModel;

import com.klarna.payment.daos.KPTitleDAO;
import com.klarna.payment.services.KPTitleService;


public class DefaultKPTitleService implements KPTitleService
{
	KPTitleDAO kpTitleDAO;


	/**
	 * @return the kpTitleDAO
	 */
	public KPTitleDAO getKpTitleDAO()
	{
		return kpTitleDAO;
	}


	/**
	 * @param kpTitleDAO
	 *           the kpTitleDAO to set
	 */
	public void setKpTitleDAO(final KPTitleDAO kpTitleDAO)
	{
		this.kpTitleDAO = kpTitleDAO;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.klarna.services.KlarnaTitleService#getTitleByNameAndLocale(java.lang.String, java.util.Locale)
	 */
	@Override
	public TitleModel getTitleByName(final String name)
	{
		return kpTitleDAO.findTitleByName(name);
	}

}

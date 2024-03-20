/**
 *
 */
package com.klarna.payment.services;

import de.hybris.platform.core.model.user.TitleModel;


/**
 * @author rajani.narayanan
 *
 */
public interface KPTitleService
{
	public TitleModel getTitleByName(String name);
}

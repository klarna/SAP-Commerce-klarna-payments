/**
 *
 */
package com.klarna.payment.daos;

import de.hybris.platform.core.model.user.TitleModel;


/**
 * @author rajani.narayanan
 *
 */
public interface KPTitleDAO
{
	public TitleModel findTitleByName(String name);
}

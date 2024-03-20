/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.klarna.payment.setup;

import static com.klarna.payment.constants.KlarnapaymentConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.services.KlarnapaymentService;


@SystemSetup(extension = KlarnapaymentConstants.EXTENSIONNAME)
public class KlarnapaymentSystemSetup
{
	private final KlarnapaymentService klarnapaymentService;

	public KlarnapaymentSystemSetup(final KlarnapaymentService klarnapaymentService)
	{
		this.klarnapaymentService = klarnapaymentService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		klarnapaymentService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return KlarnapaymentSystemSetup.class.getResourceAsStream("/klarnapayment/sap-hybris-platform.png");
	}
}

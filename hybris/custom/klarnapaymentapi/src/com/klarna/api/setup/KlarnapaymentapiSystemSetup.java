/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.klarna.api.setup;

import static com.klarna.api.constants.KlarnapaymentapiConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.klarna.api.constants.KlarnapaymentapiConstants;
import com.klarna.api.service.KlarnapaymentapiService;


@SystemSetup(extension = KlarnapaymentapiConstants.EXTENSIONNAME)
public class KlarnapaymentapiSystemSetup
{
	private final KlarnapaymentapiService klarnapaymentapiService;

	public KlarnapaymentapiSystemSetup(final KlarnapaymentapiService klarnapaymentapiService)
	{
		this.klarnapaymentapiService = klarnapaymentapiService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		klarnapaymentapiService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return KlarnapaymentapiSystemSetup.class.getResourceAsStream("/klarnapaymentapi/sap-hybris-platform.png");
	}
}

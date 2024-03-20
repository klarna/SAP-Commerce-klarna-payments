/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.klarna.api.service;

public interface KlarnapaymentapiService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.klarna.api.controller;

import static com.klarna.api.constants.KlarnapaymentapiConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.klarna.api.service.KlarnapaymentapiService;


@Controller
public class KlarnapaymentapiHelloController
{
	@Autowired
	private KlarnapaymentapiService klarnapaymentapiService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", klarnapaymentapiService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}

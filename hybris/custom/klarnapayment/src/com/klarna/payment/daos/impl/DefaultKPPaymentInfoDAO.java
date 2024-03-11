package com.klarna.payment.daos.impl;

import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;
import java.util.List;

import com.klarna.payment.daos.KPPaymentInfoDAO;
import com.klarna.payment.model.KPPaymentInfoModel;


public class DefaultKPPaymentInfoDAO implements KPPaymentInfoDAO
{

	private FlexibleSearchService flexibleSearchService;

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public KPPaymentInfoModel findKpPaymentInfo(final String code)
	{
		final String queryString = "SELECT {p:" + KPPaymentInfoModel.PK + "}" + "FROM {" + KPPaymentInfoModel._TYPECODE + " AS p} "
				+ "WHERE " + "{p:" + KPPaymentInfoModel.CODE + "}=?id ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("id", "KLARNA_" + code);

		final List<KPPaymentInfoModel> kpPaymentInfoModelList = flexibleSearchService.<KPPaymentInfoModel> search(query)
				.getResult();
		return kpPaymentInfoModelList.get(0);
	}

	@Override
	public PaymentTransactionModel findPaymentTransaction(String code)
	{
		final StringBuilder fQuery = new StringBuilder("GET {");
		fQuery.append(PaymentTransactionModel._TYPECODE);
		fQuery.append("} WHERE {");
		fQuery.append(PaymentTransactionModel.CODE);
		fQuery.append("} = ?code");

		return flexibleSearchService
				.<PaymentTransactionModel> search(fQuery.toString(), Collections.singletonMap("code", code)).getResult().stream()
				.findFirst().orElse(null);
	}


}

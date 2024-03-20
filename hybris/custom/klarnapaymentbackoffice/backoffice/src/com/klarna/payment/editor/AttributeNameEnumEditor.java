/**
 *
 */
package com.klarna.payment.editor;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.core.util.Validate;
import com.hybris.cockpitng.editor.defaultenum.DefaultEnumEditor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


/**
 * @author hybris
 *
 */
public class AttributeNameEnumEditor extends DefaultEnumEditor
{
	String typeString = "";

	@Override
	public void render(final Component parent, final EditorContext context, final EditorListener listener)
	{
		Validate.notNull("All parameters are mandatory", new Object[]
		{ parent, context, listener });
		final String valueEditor = (String) context.getParameter("valueEditor");
		System.out.println("type name :: " + valueEditor.substring(valueEditor.indexOf("(") + 1, valueEditor.indexOf(")")));
		typeString = valueEditor.substring(valueEditor.indexOf("(") + 1, valueEditor.indexOf(")"));

		super.render(parent, context, listener);
	}


	@Override
	protected List<Object> getAllValues(final String valueType, final Object initialValue)
	{

		final ApplicationContext ctx = Registry.getApplicationContext();
		final TypeService ts = ctx.getBean(TypeService.class);

		final ComposedTypeModel type = ts.getComposedTypeForCode(typeString);
		final Set<AttributeDescriptorModel> attributeDescriptors = ts.getAttributeDescriptorsForType(type);
		final List descriptorList = new ArrayList();
		for (final AttributeDescriptorModel attributeDescriptor : attributeDescriptors)
		{
			if (attributeDescriptor.getAttributeType() instanceof AtomicTypeModel)
			{
				descriptorList.add(attributeDescriptor.getQualifier());
			}

		}
		return descriptorList;
	}


}

/*
 * Copyright 2018 Klarna AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klarna.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;


/**
 * FasterXML jackson ObjectMapper with predefined settings.
 */
public class DefaultMapper extends ObjectMapper
{
	public DefaultMapper()
	{
		// Add a custom serializer/deserializer in case of missing DateTime module:
		// https://github.com/klarna/kco_rest_java/issues/12
		final SimpleModule module = new SimpleModule();
		module.addDeserializer(OffsetDateTime.class, new DateDeserializer<OffsetDateTime>(OffsetDateTime.class));
		module.addDeserializer(LocalDateTime.class, new DateDeserializer<LocalDateTime>(LocalDateTime.class));
		module.addDeserializer(LocalDate.class, new DateDeserializer<LocalDate>(LocalDate.class));
		//module.addDeserializer(TypeEnum.class, new TypeEnumDeserializer<TypeEnum>(TypeEnum.class));

		module.addSerializer(OffsetDateTime.class, new DateSerializer<OffsetDateTime>());
		module.addSerializer(LocalDateTime.class, new DateSerializer<LocalDateTime>());
		module.addSerializer(LocalDate.class, new DateSerializer<LocalDate>());

		this.registerModule(module);

		this.findAndRegisterModules();
		this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		this.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
		this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		this.setDateFormat(new SimpleDateFormat());
	}

	class DateDeserializer<T> extends JsonDeserializer<T>
	{
		Class<T> type;

		DateDeserializer(final Class<T> type)
		{
			this.type = type;
		}

		@Override
		@SuppressWarnings("unchecked")
		public T deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException
		{
			switch (this.type.getSimpleName())
			{
				case "OffsetDateTime":
					return (T) OffsetDateTime.parse(p.getText());
				case "LocalDateTime":
					return (T) LocalDateTime.parse(p.getText());
				case "LocalDate":
					return (T) LocalDate.parse(p.getText());
				default:
					return null;
			}
		}
	}

	class DateSerializer<T> extends JsonSerializer<T>
	{
		@Override
		public void serialize(final T value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException
		{
			serializers.defaultSerializeValue(value.toString(), gen);
		}
	}
}

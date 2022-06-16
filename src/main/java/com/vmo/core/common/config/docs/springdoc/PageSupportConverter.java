package com.vmo.core.common.config.docs.springdoc;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Iterator;

import static org.springdoc.core.converters.ConverterUtils.isResponseTypeWrapper;

//@Component
//not use anymore, keep for example converter
public class PageSupportConverter implements ModelConverter {
    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        JavaType javaType = Json.mapper().constructType(type.getType());
        if (javaType != null) {
            Class<?> cls = javaType.getRawClass();
            if (Pageable.class.isAssignableFrom(cls)) {
//                Schema schema = new Schema<>();
//                schema.setReadOnly(true);
                return null; //ignore this type
            }

//            if (Page.class.isAssignableFrom(cls)) {
//                JavaType innerType = javaType.getBindings().getBoundType(0);
//                if (innerType.getBindings() != null && isResponseTypeWrapper(innerType.getRawClass())) {
//                    type = new AnnotatedType(innerType).jsonViewAnnotation(type.getJsonViewAnnotation()).resolveAsRef(true);
//                    return this.resolve(type, context, chain);
//                }
//                else {
//                    type = new AnnotatedType(innerType).jsonViewAnnotation(type.getJsonViewAnnotation()).resolveAsRef(true);
//                }
//            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        }
        else {
            return null;
        }
    }
}

package com.javan.showdocutil.util;

import org.apache.commons.lang3.StringUtils;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.common.PodamIntValue;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.StringTypeManufacturerImpl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lincc
 * @version 1.0 2020/12/2
 */
public class CustomIntegerManufacturer extends IntTypeManufacturerImpl {

    private static Map<String,Integer> map = new HashMap<>();

    static {
        map.put("version", 1);
    }

    @Override
    public Integer getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
        String key = attributeMetadata.getAttributeName();
        if (map.containsKey(key)){
            return map.get(key);
        }
        return super.getType(strategy,attributeMetadata,genericTypesArgumentsMap);
    }
}

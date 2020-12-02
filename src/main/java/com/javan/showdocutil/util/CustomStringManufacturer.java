package com.javan.showdocutil.util;

import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.typeManufacturers.StringTypeManufacturerImpl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lincc
 * @version 1.0 2020/12/2
 */
public class CustomStringManufacturer extends StringTypeManufacturerImpl {

    private static Map<String,String> map = new HashMap<>();

    static {
        map.put("channelSource", "09");
        map.put("platform", "08");
        map.put("urid", "1");
        map.put("info", "请求成功");
        map.put("value", "");
        map.put("code", "");
        map.put("remark","");
    }

    @Override
    public String getType(DataProviderStrategy strategy,
                          AttributeMetadata attributeMetadata,
                          Map<String, Type> genericTypesArgumentsMap) {
        String key = attributeMetadata.getAttributeName();
        if (map.containsKey(key)){
            return map.get(key);
        }
        return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
    }
}

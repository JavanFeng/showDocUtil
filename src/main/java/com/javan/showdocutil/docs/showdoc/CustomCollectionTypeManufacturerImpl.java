package com.javan.showdocutil.docs.showdoc;

import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Default collection type manufacturer.
 *
 * @since 7.0.0.RELEASE
 */
public class CustomCollectionTypeManufacturerImpl extends AbstractTypeManufacturer<Collection> {

    @Override
    public Collection getType(DataProviderStrategy strategy,
            AttributeMetadata attributeMetadata,
            Map<String, Type> genericTypesArgumentsMap) {

        Class<?> collectionType = attributeMetadata.getAttributeType();
        Collection<Object> retValue = null;

        // Default list and set are ArrayList and HashSet. If users
        // wants a particular collection flavour they have to initialise
        // the collection
        if (collectionType.isAssignableFrom(ArrayList.class)) {
            // List
            retValue = new ArrayList<>();
        } else if (collectionType.isAssignableFrom(HashSet.class)) {
            // Set
            retValue = new HashSet<>();
        } else if (collectionType.isAssignableFrom(LinkedList.class)) {
            // Queue
            retValue = new LinkedList<>();
        }
        return retValue;
    }
}

package dev.naydichev.slab.saas.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class AnnotationExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotations().contains(Exclude.class);
    }

    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}

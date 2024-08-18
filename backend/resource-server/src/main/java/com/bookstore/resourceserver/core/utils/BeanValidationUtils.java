package com.bookstore.resourceserver.core.utils;

import com.bookstore.resourceserver.core.ApiError;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Builder
@Getter
public class BeanValidationUtils {

    private Class<?> clazz;

    public BeanValidationUtils(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Set<ConstraintDescriptor<?>> getConstraintDescriptors(String property) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            BeanDescriptor constraintsForClass = validatorFactory.getValidator().getConstraintsForClass(clazz);
            return constraintsForClass.getConstraintsForProperty(property).getConstraintDescriptors();
        }
    }

    public Optional<ConstraintDescriptor<?>> getConstraintDescriptorForAnnotation(String property, Class<? extends Annotation> annotation) {
        return getConstraintDescriptors(property).stream()
                .filter(constraint -> constraint.getAnnotation().annotationType().equals(annotation))
                .findFirst();
    }

    public String getMessageAttrOfAnnotation(String property, Class<? extends Annotation> annotation, String attribute) {
        Optional<ConstraintDescriptor<?>> optionalConstraintDescriptor = getConstraintDescriptorForAnnotation(property, annotation);
        if (optionalConstraintDescriptor.isPresent()) {
            ConstraintDescriptor<?> constraintDescriptor = optionalConstraintDescriptor.get();
            return (String) constraintDescriptor.getAttributes().get(attribute);
        }
        throw new RuntimeException("Not found attribute " + attribute + " in the annotation " + annotation);
    }


    public ApiError buildApiErrorFromAttrOfAnnotation(Function<ApiErrorBuilder, ApiError> apiErrorFunction) {
        return apiErrorFunction.apply(new ApiErrorBuilderSpec(this));
    }

}

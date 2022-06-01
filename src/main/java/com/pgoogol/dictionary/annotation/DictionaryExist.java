package com.pgoogol.dictionary.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {DictionaryExistValidator.class})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DictionaryExist {

    String message() default "Invalid document number or type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
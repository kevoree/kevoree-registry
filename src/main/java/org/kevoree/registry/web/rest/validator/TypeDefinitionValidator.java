package org.kevoree.registry.web.rest.validator;

import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.service.TypeDefinitionService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by mleduc on 13/05/16.
 */
public class TypeDefinitionValidator implements Validator {
    private final TypeDefinitionService typeDefinitionsService;

    public TypeDefinitionValidator(TypeDefinitionService typeDefinitionService) {
        this.typeDefinitionsService = typeDefinitionService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return TypeDefinition.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // TODO : all statics definitions + unicity constraint
        final TypeDefinition typeDefinition = (TypeDefinition) target;

        // name validation
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty");
        final String name = typeDefinition.getName();
        if(name.length() < 1 || name.length() > 50) {
            errors.rejectValue("name", "wrong size");
        }

        if(!name.matches("^[A-Z]\\w*$")){
            errors.rejectValue("name", "wrong format");
        }

        // serialized model validation
        ValidationUtils.rejectIfEmpty(errors, "serializedModel", "serializedModel.empty");

        // version validation
        ValidationUtils.rejectIfEmpty(errors, "version", "version.empty");
        if(typeDefinition.getVersion() <= 0) {
            errors.rejectValue("version", "version must be positive");
        }


        if(typeDefinitionsService.countSimilar(typeDefinition) > 0) {
            errors.reject("already exists", "already exists");
        }
    }

}

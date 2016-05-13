package org.kevoree.registry.web.rest.validator;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.service.NamespaceService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by mleduc on 13/05/16.
 */
public class NamespaceValidator implements Validator {

    private final NamespaceService namespaceService;

    public NamespaceValidator(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Namespace.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final Namespace namespace = (Namespace) target;
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty");
        if(namespace.getName().length() < 1 || namespace.getName().length() > 50) {
            errors.rejectValue("name", "wrong size");
        }

        if(!namespace.getName().matches("^[a-z][a-z0-9]*$")) {
            errors.rejectValue("name", "wrong format");
        }

        if(namespaceService.countSimilar(namespace) > 0) {
            errors.rejectValue("name", "already exists");
        }
    }
}

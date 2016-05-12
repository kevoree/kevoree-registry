package org.kevoree.registry.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.kevoree.registry.domain.Namespace_;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.TypeDefinition_;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.service.TypeDefinitionService;
import org.kevoree.registry.web.rest.dto.search.TypeDefinitionSearchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing TypeDefinition.
 */
@Service
@Transactional
public class TypeDefinitionServiceImpl implements TypeDefinitionService {

    private final Logger log = LoggerFactory.getLogger(TypeDefinitionServiceImpl.class);

    @Inject
    private TypeDefinitionRepository typeDefinitionRepository;

    /**
     * Save a typeDefinition.
     *
     * @param typeDefinition the entity to save
     * @return the persisted entity
     */
    public Optional<TypeDefinition> save(final TypeDefinition typeDefinition) {
        log.debug("Request to save TypeDefinition : {}", typeDefinition);
        final Long count = typeDefinitionRepository.countSimilar(typeDefinition.getNamespace().getId(), typeDefinition.getName(), typeDefinition.getVersion());
        final Optional<TypeDefinition> ret;
        if (count == 0) {
            TypeDefinition result = typeDefinitionRepository.save(typeDefinition);
            ret = Optional.of(result);
        } else {
            ret = Optional.empty();
        }
        return ret;
    }

    /**
     * Get all the typeDefinitions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TypeDefinition> findAll(final Pageable pageable) {
        log.debug("Request to get all TypeDefinitions");
        return typeDefinitionRepository.findAll(pageable);
    }

    /**
     * Get one typeDefinition by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public TypeDefinition findOne(final Long id) {
        log.debug("Request to get TypeDefinition : {}", id);
        return typeDefinitionRepository.findOne(id);
    }

    /**
     * Delete the  typeDefinition by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete TypeDefinition : {}", id);
        typeDefinitionRepository.delete(id);
    }

    @Override
    public List<TypeDefinition> search(final TypeDefinitionSearchDTO typeDefSearch) {
        final String namespace = typeDefSearch.getNamespace();
        final boolean namespaceLeftJoker = namespace.startsWith("*");
        final boolean namespaceRightJoker = namespace.endsWith("*");


        // filtering by namespace.
        final Specifications<TypeDefinition> whereNamespace;
        if (namespaceLeftJoker && namespaceRightJoker) {
            whereNamespace = Specifications.where(namespaceContaining(namespace));
        } else if (namespaceLeftJoker) {
            whereNamespace = Specifications.where(namespaceEndingWith(namespace));
        } else if (namespaceRightJoker) {
            whereNamespace = Specifications.where(namespaceStartingWith(namespace));
        } else {
            whereNamespace = Specifications.where(namespaceEqual(namespace));
        }

        // filtering by typedef
        final String name = typeDefSearch.getName();
        final Optional<Specification<TypeDefinition>> nameSpecification;
        if (name != null && StringUtils.isNotBlank(name)) {
            final boolean nameLeftJoker = name.startsWith("*");
            final boolean nameRightJoker = name.endsWith("*");
            final Specification<TypeDefinition> val;
            if (nameLeftJoker && nameRightJoker) {
                val = nameContaining(name);
            } else if (nameLeftJoker) {
                val = nameEndingWith(name);
            } else if (nameRightJoker) {
                val = nameStartingWith(name);
            } else {
                val = nameEqual(name);
            }
            nameSpecification = Optional.of(val);
        } else {
            nameSpecification = Optional.empty();
        }


        final Optional<Specification<TypeDefinition>> versionSpecification;
        final Long version = typeDefSearch.getVersion();
        if (version != null) {
            versionSpecification = Optional.of(versionEqual(version));
        } else {
            versionSpecification = Optional.empty();
        }

        final Specifications<TypeDefinition> step1 = nameSpecification.map(tmp -> whereNamespace.and(tmp)).orElse(whereNamespace);
        final Specifications<TypeDefinition> step2 = versionSpecification.map(tmp -> step1.and(tmp)).orElse(step1);
        return typeDefinitionRepository.findAll(step2);
    }


    private Specification<TypeDefinition> versionEqual(final Long version) {
        return (root, query, cb) -> cb.equal(root.get(TypeDefinition_.version), version);
    }

    private Specification<TypeDefinition> namespaceEqual(String namespace) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(TypeDefinition_.namespace).get(Namespace_.name)), namespace);
    }

    private Specification<TypeDefinition> namespaceStartingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(TypeDefinition_.namespace).get(Namespace_.name)), namespace.substring(0, namespace.length()-1) + "%");
    }

    private Specification<TypeDefinition> namespaceEndingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(TypeDefinition_.namespace).get(Namespace_.name)), "%" + namespace.substring(1));
    }

    private Specification<TypeDefinition> namespaceContaining(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(TypeDefinition_.namespace).get(Namespace_.name)), "%" + namespace.substring(1,namespace.length()-1) + "%");
    }

    private Specification<TypeDefinition> nameStartingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(TypeDefinition_.name)), namespace.substring(0, namespace.length() - 1) + "%");
    }

    private Specification<TypeDefinition> nameEndingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(TypeDefinition_.name)), "%" + namespace.substring(1));
    }

    private Specification<TypeDefinition> nameContaining(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(TypeDefinition_.name)), "%" + namespace.substring(1,namespace.length()-1) + "%");
    }

    private Specification<TypeDefinition> nameEqual(String name) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(TypeDefinition_.name)), name.toLowerCase());
    }
}

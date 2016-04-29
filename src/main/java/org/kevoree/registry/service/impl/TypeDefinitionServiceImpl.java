package org.kevoree.registry.service.impl;

import org.kevoree.registry.domain.TypeDefinition_;
import org.kevoree.registry.service.TypeDefinitionService;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.web.rest.dto.search.TypeDefinitionSearchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing TypeDefinition.
 */
@Service
@Transactional
public class TypeDefinitionServiceImpl implements TypeDefinitionService{

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
        if(count == 0) {
            TypeDefinition result = typeDefinitionRepository.save(typeDefinition);
            ret = Optional.of(result);
        } else {
            ret = Optional.empty();
        }
        return ret;
    }

    /**
     *  Get all the typeDefinitions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TypeDefinition> findAll(final Pageable pageable) {
        log.debug("Request to get all TypeDefinitions");
        return typeDefinitionRepository.findAll(pageable);
    }

    /**
     *  Get one typeDefinition by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TypeDefinition findOne(final Long id) {
        log.debug("Request to get TypeDefinition : {}", id);
        return typeDefinitionRepository.findOne(id);
    }

    /**
     *  Delete the  typeDefinition by id.
     *
     *  @param id the id of the entity
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
        if(namespaceLeftJoker && namespaceRightJoker) {

        } else if(namespaceLeftJoker) {

        } else if (namespaceRightJoker) {

        } else {

        }
        return typeDefinitionRepository.findAll(Specifications.where(nameEquals(typeDefSearch.getName())));
    }

    private Specification<TypeDefinition> nameEquals(String name) {
        return new Specification<TypeDefinition>() {
            @Override
            public Predicate toPredicate(Root<TypeDefinition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(TypeDefinition_.name), name);

            }
        };
    }
}

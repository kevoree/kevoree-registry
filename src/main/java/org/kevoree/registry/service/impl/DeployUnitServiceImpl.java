package org.kevoree.registry.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.kevoree.registry.domain.*;
import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.web.rest.dto.search.DeployUnitSearchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.*;

/**
 * Service Implementation for managing DeployUnit.
 */
@Service
@Transactional
public class DeployUnitServiceImpl implements DeployUnitService{

    private final Logger log = LoggerFactory.getLogger(DeployUnitServiceImpl.class);
    private final Sort defaultSort = new Sort(new Sort.Order(Direction.ASC, DeployUnit_.typeDefinition.getName()),
        new Sort.Order(Direction.DESC, DeployUnit_.priority.getName()));

    @Inject
    private DeployUnitRepository deployUnitRepository;

    /**
     * Save a deployUnit.
     *
     * @param deployUnit the entity to save
     * @return the persisted entity
     */
    public DeployUnit save(DeployUnit deployUnit) {
        log.debug("Request to save DeployUnit : {}", deployUnit);
        DeployUnit result = deployUnitRepository.save(deployUnit);
        return result;
    }

    /**
     *  Get all the deployUnits.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<DeployUnit> findAll() {
        log.debug("Request to get all DeployUnits");
        List<DeployUnit> result = deployUnitRepository.findAll(defaultSort);
        return result;
    }

    /**
     *  Get one deployUnit by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public DeployUnit findOne(Long id) {
        log.debug("Request to get DeployUnit : {}", id);
        DeployUnit deployUnit = deployUnitRepository.findOne(id);
        return deployUnit;
    }

    /**
     *  Delete the  deployUnit by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DeployUnit : {}", id);
        deployUnitRepository.delete(id);
    }

    @Override
    public List<DeployUnit> search(DeployUnitSearchDTO deployUnitSearch) {
        final String namespace = deployUnitSearch.getNamespace();
        final boolean namespaceLeftJoker = namespace.startsWith("*");
        final boolean namespaceRightJoker = namespace.endsWith("*");


        // filtering by namespace.
        final Specifications<DeployUnit> whereNamespace;
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
        final String name = deployUnitSearch.getName();
        final Optional<Specification<DeployUnit>> nameSpecification;
        if (name != null && StringUtils.isNotBlank(name)) {
            final boolean nameLeftJoker = name.startsWith("*");
            final boolean nameRightJoker = name.endsWith("*");
            final Specification<DeployUnit> val;
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

        final String platform = deployUnitSearch.getPlatform();
        final Optional<Specification<DeployUnit>> platformSpecification;
        if(platform != null && StringUtils.isNotBlank(platform)) {
            final boolean platformLeftJoker = platform.startsWith("*");
            final boolean platformRightJoker = platform.endsWith("*");
            final Specification<DeployUnit> val;
            if(platformLeftJoker && platformRightJoker) {
                val = platformContaining(platform);
            } else if (platformLeftJoker) {
                val = platformEndingWith(platform);
            } else if (platformRightJoker) {
                val = platformStartingWith(platform);
            } else {
                val = platformEqual(platform);
            }
            platformSpecification = Optional.of(val);
        } else {
            platformSpecification = Optional.empty();
        }


        final Optional<Specification<DeployUnit>> versionSpecification;
        final Long version = deployUnitSearch.getVersion();
        if (version != null) {
            versionSpecification = Optional.of(versionEqual(version));
        } else {
            versionSpecification = Optional.empty();
        }




        final Optional<Specification<DeployUnit>> deployUnitSpecification;
        if(deployUnitSearch.getLatest() != null && deployUnitSearch.getLatest()) {
            final Specification<DeployUnit> dus = getOnlyLastVersionByDU();
            deployUnitSpecification = Optional.of(dus);
        } else {
            deployUnitSpecification = Optional.empty();
            }

        final Specifications<DeployUnit> step1 = nameSpecification.map(tmp -> whereNamespace.and(tmp)).orElse(whereNamespace);
        final Specifications<DeployUnit> step2 = versionSpecification.map(tmp -> step1.and(tmp)).orElse(step1);
        final Specifications<DeployUnit> step3 = platformSpecification.map(tmp -> step2.and(tmp)).orElse(step2);
        final Specifications<DeployUnit> step4 = deployUnitSpecification.map(tmp -> step3.and(tmp)).orElse(step3);

        return deployUnitRepository.findAll(step4, defaultSort);
    }

    private Specification<DeployUnit> getOnlyLastVersionByDU() {
        return (root, query, cb) -> {

                final Subquery<Long> subQuery1 = query.subquery(Long.class);
                final Root<DeployUnit> deployUnitSub1 = subQuery1.from(DeployUnit.class);

                final Expression<Long> expr = deployUnitSub1.get(DeployUnit_.id);
                subQuery1.select(cb.max(expr));
                subQuery1.groupBy(deployUnitSub1.get(DeployUnit_.typeDefinition));
                Subquery<String> subQuery2 = subQuery1.subquery(String.class);
                final Root<DeployUnit> deployUnitSub2 = subQuery2.from(DeployUnit.class);

                final Expression<String> se1 = deployUnitSub2.get(DeployUnit_.typeDefinition ).get(TypeDefinition_.id).as(String.class);
                final Expression<String> se2 = cb.max(deployUnitSub2.get(DeployUnit_.priority)).as(String.class);

                subQuery2.select(cb.concat(cb.concat(se1, cb.literal("_")), se2));
                subQuery2.groupBy(deployUnitSub2.get(DeployUnit_.typeDefinition));

                final Expression<String> e1 = deployUnitSub1.get(DeployUnit_.typeDefinition).get(TypeDefinition_.id).as(String.class);
                final Expression<String> e2 = deployUnitSub1.get(DeployUnit_.priority).as(String.class);
                final Expression<String> col = cb.concat(cb.concat(e1, cb.literal("_")), e2);
                subQuery1.where(cb.in(col).value(subQuery2));

                return cb.in(root.get(DeployUnit_.id)).value(subQuery1);
            };
    }

    private Specification<DeployUnit> versionEqual(final Long version) {
        return (root, query, cb) -> cb.equal(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.version), version);
    }

    private Specification<DeployUnit> namespaceEqual(String namespace) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.namespace).get(Namespace_.name)), namespace);
    }

    private Specification<DeployUnit> namespaceStartingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.namespace).get(Namespace_.name)), namespace.substring(0, namespace.length()-1) + "%");
    }

    private Specification<DeployUnit> namespaceEndingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.namespace).get(Namespace_.name)), "%" + namespace.substring(1));
    }

    private Specification<DeployUnit> namespaceContaining(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.namespace).get(Namespace_.name)), "%" + namespace.substring(1,namespace.length()-1) + "%");
    }

    private Specification<DeployUnit> nameStartingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.name)), namespace.substring(0, namespace.length() - 1) + "%");
    }

    private Specification<DeployUnit> nameEndingWith(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.name)), "%" + namespace.substring(1) + "%");
    }

    private Specification<DeployUnit> nameContaining(String namespace) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.name)), "%" + namespace.substring(1,namespace.length()-1) + "%");
    }

    private Specification<DeployUnit> nameEqual(String name) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(DeployUnit_.typeDefinition).get(TypeDefinition_.name)), name.toLowerCase());
    }

    private Specification<DeployUnit> platformStartingWith(String platform) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.platform)), platform.substring(0, platform.length() - 1) + "%");
    }

    private Specification<DeployUnit> platformEndingWith(String platform) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.platform)), "%" + platform.substring(1) + "%");
    }

    private Specification<DeployUnit> platformContaining(String platform) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(DeployUnit_.platform)), "%" + platform.substring(1, platform.length()-1) + "%");
    }

    private Specification<DeployUnit> platformEqual(String platform) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(DeployUnit_.platform)), platform.toLowerCase());
    }
}

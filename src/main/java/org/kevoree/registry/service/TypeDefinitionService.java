package org.kevoree.registry.service;

import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.service.dto.TypeDefinitionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Created by leiko on 4/13/17.
 */
@Service
@Transactional
public class TypeDefinitionService {

    @Inject
    private TypeDefinitionRepository tdefRepository;

    private final Function<TypeDefinition, TypeDefinition> loadDeployUnits = tdef -> {
        tdef.getDeployUnits().size();
        return tdef;
    };

    private final Consumer<TypeDefinition> loadDeployUnitsConsumer = tdef -> tdef.getDeployUnits().size();

    public Page<TypeDefinitionDTO> getPage(Pageable pageable, boolean latest) {
        Page<TypeDefinition> page = tdefRepository.findAll(pageable);
        Stream<TypeDefinitionDTO> tdefs = page.getContent().stream()
                .map(loadDeployUnits)
                .map(TypeDefinitionDTO::new);
        if (latest) {
            tdefs = onlyLatest(tdefs);
        }

        return new PageImpl<>(tdefs.collect(Collectors.toList()), pageable, page.getTotalElements());
    }

    public TypeDefinition findOne(Long id) {
        return Optional.of(tdefRepository.findOne(id))
                .map(loadDeployUnits)
                .orElse(null);
    }

    public Set<TypeDefinitionDTO> getAll() {
        return tdefRepository.findAll()
                .stream()
                .map(loadDeployUnits)
                .map(TypeDefinitionDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<TypeDefinitionDTO> getAllByNamespace(String namespace) {
        return tdefRepository.findByNamespaceName(namespace)
                .stream()
                .map(loadDeployUnits)
                .map(TypeDefinitionDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<TypeDefinitionDTO> getAllByNamespaceAndName(String namespace, String name) {
        return tdefRepository.findByNamespaceNameAndName(namespace, name)
                .stream()
                .map(loadDeployUnits)
                .map(TypeDefinitionDTO::new)
                .collect(Collectors.toSet());
    }

    public Optional<TypeDefinition> findByNamespaceAndNameAndVersion(String namespace, String name, Long version) {
        Optional<TypeDefinition> tdef = tdefRepository.findOneByNamespaceNameAndNameAndVersion(namespace, name, version);
        tdef.ifPresent(loadDeployUnitsConsumer);
        return tdef;
    }

    public Optional<TypeDefinition> findLatestByNamespaceAndName(String namespace, String name) {
        Optional<TypeDefinition> tdef = tdefRepository
                .findFirst1ByNamespaceNameAndNameOrderByVersionDesc(namespace, name);
        tdef.ifPresent(loadDeployUnitsConsumer);
        return tdef;
    }

    public Stream<TypeDefinitionDTO> onlyLatest(Stream<TypeDefinitionDTO> tdefs) {
        // TODO try to do this in one big SQL query?
        Map<String, TypeDefinitionDTO> latestTdefs = new HashMap<>();
        tdefs.forEach(tdef -> {
            TypeDefinitionDTO latest = latestTdefs.get(tdef.getName());
            if (latest != null) {
                if (latest.getVersion() < tdef.getVersion()) {
                    latestTdefs.put(tdef.getName(), tdef);
                }
            } else {
                latestTdefs.put(tdef.getName(), tdef);
            }
        });

        return latestTdefs.values().stream();
    }
}

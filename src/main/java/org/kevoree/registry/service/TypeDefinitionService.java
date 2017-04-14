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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Page<TypeDefinitionDTO> getPage(Pageable pageable) {
        Page<TypeDefinition> page = tdefRepository.findAll(pageable);
        List<TypeDefinitionDTO> tdefs = page.getContent().stream()
                .map(loadDeployUnits)
                .map(TypeDefinitionDTO::new)
                .collect(Collectors.toList());
        return new PageImpl<>(tdefs, pageable, page.getTotalElements());
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
}

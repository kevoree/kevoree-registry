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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    public Set<TypeDefinitionDTO> getAllWithDeployUnits() {
        return tdefRepository.findAll()
                .stream()
                .map(tdef -> {
                    tdef.getDeployUnits().size();
                    return tdef;
                })
                .map(TypeDefinitionDTO::new)
                .collect(Collectors.toSet());
    }

    public Page<TypeDefinitionDTO> getPage(Pageable pageable, boolean latest) {
        Page<TypeDefinition> page = tdefRepository.findAll(pageable);
        Stream<TypeDefinitionDTO> tdefs = page.getContent().stream().map(TypeDefinitionDTO::new);
        if (latest) {
            tdefs = onlyLatest(tdefs);
        }
        return new PageImpl<>(tdefs.collect(Collectors.toList()), pageable, page.getTotalElements());
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

package org.kevoree.registry.service.mapper;

import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.service.dto.TypeDefinitionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity TypeDefinition
 */
@Mapper(componentModel = "spring")
public abstract class TypeDefinitionMapper {

    @Mapping(source = "namespace.name", target = "namespace")
    public abstract TypeDefinitionDTO typeDefinitionToTypeDefinitionDTO(TypeDefinition tdef);

    @Mapping(source = "namespace", target = "namespace.name")
    public abstract TypeDefinition typeDefinitionDTOtoTypeDefinition(TypeDefinitionDTO namespaceDTO);

    public abstract Set<TypeDefinitionDTO> typeDefinitionsToTypeDefinitionDTOs(Set<TypeDefinition> tdefs);
    public abstract Set<TypeDefinition> typeDefinitionsDTOtoTypeDefinitions(Set<TypeDefinitionDTO> tdefDTOs);

    public abstract List<TypeDefinitionDTO> typeDefinitionsToTypeDefinitionDTOs(List<TypeDefinition> tdefs);
    public abstract List<TypeDefinition> typeDefinitionsDTOtoTypeDefinitions(List<TypeDefinitionDTO> tdefDTOs);

    public Set<Long> longsFromDeployUnits(Set<DeployUnit> deployUnits) {
        return deployUnits.stream().map(DeployUnit::getId)
                .collect(Collectors.toSet());
    }

    public Set<DeployUnit> deployUnitsFromLongs(Set<Long> ids) {
        return ids.stream().map(id -> {
            DeployUnit du = new DeployUnit();
            du.setId(id);
            return du;
        }).collect(Collectors.toSet());
    }
}

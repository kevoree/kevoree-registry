package org.kevoree.registry.service.mapper;

import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.service.dto.DeployUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 *
 * Created by leiko on 4/13/17.
 */
@Mapper(componentModel = "spring")
public abstract class DeployUnitMapper {

    @Mapping(source = "typeDefinition.namespace.name", target = "namespace")
    @Mapping(source = "typeDefinition.id", target = "tdefId")
    @Mapping(source = "typeDefinition.name", target = "tdefName")
    @Mapping(source = "typeDefinition.version", target = "tdefVersion")
    public abstract DeployUnitDTO deployUnitToDeployUnitDTO(DeployUnit deployUnit);

    @Mapping(source = "namespace", target = "typeDefinition.namespace.name")
    @Mapping(source = "tdefId", target = "typeDefinition.id")
    @Mapping(source = "tdefName", target = "typeDefinition.name")
    @Mapping(source = "tdefVersion", target = "typeDefinition.version")
    public abstract DeployUnit deployUnitDTOtoDeployUnit(DeployUnitDTO deployUnitDTO);

    public abstract List<DeployUnitDTO> deployUnitsToDeployUnitDTOs(List<DeployUnit> deployUnits);
    public abstract List<DeployUnit> deployUnitsDTOtoDeployUnits(List<DeployUnitDTO> deployUnitDTOs);
    public abstract Set<DeployUnitDTO> deployUnitsToDeployUnitDTOs(Set<DeployUnit> deployUnits);
    public abstract Set<DeployUnit> deployUnitsDTOtoDeployUnits(Set<DeployUnitDTO> deployUnitDTOs);
}

package org.kevoree.registry.service;

import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.web.rest.dto.DeployUnitDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * DeployUnitService
 */
@Service
@Transactional
public class DeployUnitService {

    private final Logger log = LoggerFactory.getLogger(DeployUnitService.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private NamespaceRepository nsRepository;

    @Inject
    private TypeDefinitionRepository tdefRepository;

    @Inject
    private DeployUnitRepository duRepository;

    public DeployUnit create(DeployUnitDTO dto) {
        DeployUnit du = new DeployUnit();
        du.setName(dto.getName());
        du.setVersion(dto.getVersion());
        du.setPlatform(dto.getPlatform());
        du.setModel(dto.getModel());
        du.setTypeDefinition(tdefRepository.findOne(dto.getTdefId()));
        return duRepository.save(du);
    }

    /**
     * @param tdefId the ID of the TypeDefinition used to add the DeployUnit
     * @return true if the current authenticated user has the right to create
     * a new DeployUnit within the specified TypeDefinition; false otherwise
     */
    public boolean canCreate(Long tdefId) {
        return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .map(user -> {
                if (userService.hasAuthority(AuthoritiesConstants.ADMIN)) {
                    return true;
                }
                TypeDefinition tdef = tdefRepository.findOne(tdefId);
                return user.getNamespaces().contains(tdef.getNamespace());
            })
            .orElse(false);
    }
}

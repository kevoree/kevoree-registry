package org.kevoree.registry.service;

import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.web.rest.dto.DeployUnitDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;

/**
 * DeployUnitService
 */
@Service
@Transactional
public class DeployUnitService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private TypeDefinitionRepository tdefRepository;

    @Inject
    private DeployUnitRepository duRepository;

    public DeployUnit create(TypeDefinition tdef, DeployUnitDTO dto) {
        String login = SecurityUtils.getCurrentUserLogin();
        DeployUnit du = new DeployUnit();
        du.setName(dto.getName());
        du.setVersion(dto.getVersion());
        du.setPlatform(dto.getPlatform());
        du.setModel(dto.getModel());
        du.setTypeDefinition(tdef);
        du.setCreatedBy(login);
        tdef.setLastModifiedBy(login);
        tdef.setLastModifiedDate(ZonedDateTime.now());
        tdefRepository.save(tdef);
        return duRepository.save(du);
    }

    /**
     * @param tdefId the ID of the TypeDefinition used to add the DeployUnit
     * @return true if the current authenticated user has the right to create
     * a new DeployUnit within the specified TypeDefinition; false otherwise
     */
    public boolean canCreate(Long tdefId) {
        User user = userService.getUserWithAuthorities();
        if (userService.hasAuthority(user, AuthoritiesConstants.ADMIN)) {
            return true;
        }
        TypeDefinition tdef = tdefRepository.findOne(tdefId);
        return user.getNamespaces().contains(tdef.getNamespace());
    }
}

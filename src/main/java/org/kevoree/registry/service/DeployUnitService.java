package org.kevoree.registry.service;

import com.github.zafarkhaja.semver.Version;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.dto.DeployUnitDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private Predicate<DeployUnit> filterOnlyReleases = du ->
            Version.valueOf(du.getVersion()).getPreReleaseVersion().isEmpty();

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

    public Set<DeployUnit> onlyLatests(Set<DeployUnit> dus) {
        Map<String, DeployUnit> latestDus = new HashMap<>();
        for (DeployUnit du : dus) {
            String key = du.getPlatform();
            DeployUnit latest = latestDus.get(key);
            if (latest != null) {
                if (Version.valueOf(latest.getVersion()).lessThan(Version.valueOf(du.getVersion()))) {
                    latestDus.put(key, du);
                }
            } else {
                latestDus.put(key, du);
            }
        }
        return latestDus.values().stream().collect(Collectors.toSet());
    }

    public Set<DeployUnit> onlyReleases(Set<DeployUnit> dus) {
        return onlyLatests(dus
                .stream()
                .filter(filterOnlyReleases)
                .collect(Collectors.toSet()));
    }

    public Optional<DeployUnit> getLatest(Set<DeployUnit> dus) {
        DeployUnit latest = null;
        for (DeployUnit du : dus) {
            if (latest == null) {
                latest = du;
            } else {
                if (Version.valueOf(latest.getVersion()).lessThan(Version.valueOf(du.getVersion()))) {
                    latest = du;
                }
            }
        }
        return Optional.ofNullable(latest);
    }

    public Optional<DeployUnit> getLatestRelease(Set<DeployUnit> dus) {
        return getLatest(dus.stream().filter(filterOnlyReleases).collect(Collectors.toSet()));
    }

    public Optional<DeployUnit> getByVersion(Set<DeployUnit> dus, String semver) {
        DeployUnit satisfying = null;
        for (DeployUnit du : dus) {
            // TODO com.github.zafarkhaja.semver does not handle ranges for pre-release
            // TODO expr like "^1.2.3-alpha" will throw a parse exception
            // TODO so for now, just handle exact match
            if (Version.valueOf(du.getVersion()).equals(Version.valueOf(semver))) {
                satisfying = du;
                break;
            }
        }

        return Optional.ofNullable(satisfying);
    }

    public Set<DeployUnit> satisfies(Set<DeployUnit> dus, String semver) {
        return dus.stream()
                .filter(du -> Version.valueOf(du.getVersion()).satisfies(semver))
                .collect(Collectors.toSet());
    }

    public Map<String, Set<DeployUnit>> mapByPlatform(TypeDefinition tdef) {
        Set<DeployUnit> dus = duRepository.findByTypeDefinitionId(tdef.getId());
        Map<String, Set<DeployUnit>> dusByPlatform = new HashMap<>();
        dus.forEach(du -> {
            Set<DeployUnit> deployUnits = dusByPlatform.computeIfAbsent(du.getPlatform(), p -> new HashSet<>());
            deployUnits.add(du);
        });
        return dusByPlatform;
    }

    public Map<String, DeployUnit> mapByPlatform(TypeDefinition tdef, Map<String, String> filters) {
        Map<String, DeployUnit> dus = new HashMap<>();
        Map<String, Set<DeployUnit>> dusByPlatform = this.mapByPlatform(tdef);
        for (Map.Entry<String, Set<DeployUnit>> entry : dusByPlatform.entrySet()) {
            String filter = filters.get(entry.getKey());
            Optional<DeployUnit> du;
            switch (filter.toLowerCase()) {
                case "latest":
                    du = getLatest(entry.getValue());
                    break;

                case "release":
                    du = getLatestRelease(entry.getValue());
                    break;

                default:
                    du = getByVersion(entry.getValue(), filter);
                    break;
            }
            du.ifPresent(deployUnit -> dus.put(entry.getKey(), deployUnit));
        }
        return dus;
    }

    public Set<DeployUnit> filter(TypeDefinition tdef, Map<String, String> filters) {
        return this.mapByPlatform(tdef, filters).values().stream().collect(Collectors.toSet());
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

    public Predicate<DeployUnit> filterByPlatform(final String platform) {
        return du -> {
            if (platform == null) {
                return true;
            } else {
                String[] platforms;
                if (platform.contains(",")) {
                    platforms = platform.split(",");
                } else {
                    platforms = new String[] { platform };
                }
                for (String p : platforms) {
                    if (du.getPlatform().equals(p)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}

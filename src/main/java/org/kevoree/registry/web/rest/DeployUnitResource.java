package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.zafarkhaja.semver.Version;
import io.swagger.annotations.ApiParam;
import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.*;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.service.dto.DeployUnitDTO;
import org.kevoree.registry.service.dto.ErrorDTO;
import org.kevoree.registry.service.mapper.DeployUnitMapper;
import org.kevoree.registry.service.util.SemverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * REST controller for managing DeployUnit.
 */
@RestController
@RequestMapping("/api")
public class DeployUnitResource {

    private final Logger log = LoggerFactory.getLogger(DeployUnitResource.class);

    @Inject
    private DeployUnitRepository duRepository;

    @Inject
    private TypeDefinitionRepository tdefsRepository;

    @Inject
    private DeployUnitService duService;

    @Inject
    private NamespaceRepository nsRepository;

    @Inject
    private AuthorityRepository authRepository;

    @Inject
    private UserService userService;

    @Inject
    private DeployUnitMapper duMapper;

    /**
     * POST  /namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus : Create a new deployUnit.
     *
     * @param namespace the namespace name to find the typeDefinition in
     * @param tdefName the typeDefinition name to attach the deployUnit to
     * @param tdefVersion the typeDefinition version to attach de deployUnit to
     * @param deployUnit the deployUnit to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deployUnit, or with status 400 (Bad Request) if the deployUnit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> createDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                              @PathVariable Long tdefVersion,
                                              @Valid @RequestBody DeployUnitDTO deployUnit)
            throws URISyntaxException {
        log.debug("REST user \"{}\" request to create DeployUnit: {} for {}.{}/{}", SecurityUtils.getCurrentUserLogin(),
                deployUnit, namespace, tdefName, tdefVersion);
        if (deployUnit.getId() == null) {
            return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, tdefName, tdefVersion)
                .map(tdef -> {
                    if (duService.canCreate(tdef.getId())) {
                        try {
                            DeployUnit savedDu = duService.create(tdef, deployUnit);
                            return new ResponseEntity<>(savedDu, HttpStatus.CREATED);
                        } catch (DataIntegrityViolationException e) {
                            return new ResponseEntity<>(
                                new ErrorDTO("There is already a DeployUnit with version " + deployUnit.getVersion() +
                                    " for platform " + deployUnit.getPlatform()), HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                })
                .orElse(new ResponseEntity<>(
                    new ErrorDTO("Unknown TypeDefinition " + namespace + "." + tdefName + "/" + tdefVersion),
                    HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(new ErrorDTO("A new DeployUnit cannot already have an ID"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT  /namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform} : Updates a deployUnit.
     *
     * @param namespace the namespace name to find the typeDefinition in
     * @param tdefName the typeDefinition name to attach the deployUnit to
     * @param tdefVersion the typeDefinition version to attach de deployUnit to
     * @param name the deployUnit name
     * @param version the deployUnit version
     * @param platform the deployUnit platform
     * @param deployUnit the updated deployUnit
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployUnit,
     * or with status 400 (Bad Request) if the deployUnit is not valid,
     * or with status 500 (Internal Server Error) if the deployUnit couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus/{name}/{version}/{platform:.+}")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> updateDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                              @PathVariable Long tdefVersion, @PathVariable String name,
                                              @PathVariable String version, @PathVariable String platform,
                                              @Valid @RequestBody DeployUnitDTO deployUnit) throws URISyntaxException {
        log.debug("REST user \"{}\" request to update DeployUnit {}-{}-{} from Namespace: {} and TypeDefinition: {}/{}",
                SecurityUtils.getCurrentUserLogin(), name, version, platform, namespace, tdefName, tdefVersion);
        if (deployUnit.getId() == null) {
            return createDeployUnit(namespace, tdefName, tdefVersion, deployUnit);
        }
        Optional<DeployUnit> dbDu = duRepository.findOneByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersionAndPlatform(
            namespace, tdefName, tdefVersion, name, version, platform);
        if (dbDu.isPresent()) {
            DeployUnit du = dbDu.get();
            if (duService.canCreate(du.getTypeDefinition().getId())) {
                if (du.getName().equals(deployUnit.getName())) {
                    if (du.getVersion().equals(deployUnit.getVersion())) {
                        // test SemVer preRelease
                        Version v = new Version.Builder(deployUnit.getVersion()).build();
                        if (v.getPreReleaseVersion() != null && !v.getPreReleaseVersion().isEmpty()) {
                            if (du.getPlatform().equals(deployUnit.getPlatform())) {
                                Optional<TypeDefinition> tdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, tdefName, tdefVersion);
                                if (tdef.isPresent()) {
                                    if (du.getTypeDefinition().equals(tdef.get())) {
                                        ZonedDateTime lastModified = ZonedDateTime.now();
                                        String login = SecurityUtils.getCurrentUserLogin();
                                        du.setModel(deployUnit.getModel());
                                        du.setLastModifiedBy(login);
                                        du.setLastModifiedDate(lastModified);
                                        DeployUnit result = duRepository.save(du);
                                        return new ResponseEntity<>(result, HttpStatus.OK);
                                    } else {
                                        return new ResponseEntity<>(new ErrorDTO("DeployUnit typeDef cannot be updated"), HttpStatus.BAD_REQUEST);
                                    }
                                } else {
                                    return new ResponseEntity<>(
                                        new ErrorDTO("Unable to find TypeDefinition " + namespace + "." + tdefName + "/" + tdefVersion),
                                        HttpStatus.NOT_FOUND);
                                }
                            } else {
                                return new ResponseEntity<>(new ErrorDTO("DeployUnit platform cannot be updated"), HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            return new ResponseEntity<>(new ErrorDTO("DeployUnit with release version cannot be updated (only preRelease)"), HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>(new ErrorDTO("DeployUnit version cannot be updated"), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(new ErrorDTO("DeployUnit name cannot be updated"), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET  /dus -> returns all dus
     */
    @GetMapping("/dus")
    @Timed
    public List<DeployUnitDTO> getDeployUnits(@ApiParam Pageable pageable) {
        log.debug("REST request to get all DeployUnits");
        return duMapper.deployUnitsToDeployUnitDTOs(duRepository.findAll(pageable).getContent());
    }

    /**
     * GET  /dus/page -> returns pageable dus
     */
    @GetMapping("/dus/page")
    @Timed
    public Page<DeployUnitDTO> getPageableDeployUnits(Pageable pageable) {
        log.debug("REST request to get paged DeployUnits (index={}, size={})", pageable.getPageNumber(), pageable.getPageSize());
        Page<DeployUnit> page = duRepository.findAll(pageable);
        return new PageImpl<>(duMapper.deployUnitsToDeployUnitDTOs(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * GET  /dus/:id : get the "id" deployUnit.
     *
     * @param id the id of the deployUnit to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnit, or with status 404 (Not Found)
     */
    @GetMapping("/dus/{id:[\\d]+}")
    @Timed
    public ResponseEntity<DeployUnitDTO> getDeployUnit(@PathVariable Long id) {
        log.debug("REST request to get DeployUnit : {}", id);
        DeployUnit deployUnit = duRepository.findOne(id);
        return Optional.ofNullable(deployUnit)
            .map(du -> new ResponseEntity<>(new DeployUnitDTO(du), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /namespaces/:namespace/dus : get the all the deployUnits attached to a specified namespace.
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/dus")
    @Timed
    public Set<DeployUnitDTO> getDeployUnits(@PathVariable String namespace) {
        log.debug("REST request to get DeployUnits from Namespace : {}", namespace);
        return duMapper.deployUnitsToDeployUnitDTOs(duRepository.findByNamespace(namespace));
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/dus : get the all the deployUnits attached to a specified namespace and a specified
     * typeDefinition name
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/dus")
    @Timed
    public Set<DeployUnitDTO> getDeployUnits(@PathVariable String namespace, @PathVariable String tdefName) {
        log.debug("REST request to get DeployUnits from Namespace: {} and TypeDefinition: {}", namespace, tdefName);
        return duMapper.deployUnitsToDeployUnitDTOs(duRepository.findByNamespaceAndTypeDefinition(namespace, tdefName));
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus : get all the deployUnits attached to a
     * specific "namespace.TypeDefinition/version"
     * Filtering results by query params "platform" and "version"
     *   - platform: comma separated list of strings (ie. js,java)
     *   - version:  latest | release | semver range
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus")
    @Timed
    public ResponseEntity<?> getDeployUnits(@PathVariable String namespace, @PathVariable String tdefName,
                                            @PathVariable Long tdefVersion,
                                            @RequestParam(required = false) String platform,
                                            @RequestParam(required = false) String version,
                                            HttpServletRequest request) {
        log.debug("REST request to get all deployUnits from {}.{}/{} (platform={},version={})",
            namespace, tdefName, tdefVersion, platform, version);
        Optional<Namespace> ns = Optional.ofNullable(nsRepository.findOne(namespace));
        if (ns.isPresent()) {
            if (tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, tdefName, tdefVersion).isPresent()) {
                Set<DeployUnit> dus = duRepository
                        .findByNamespaceAndTypeDefinitionAndTypeDefinitionVersion(namespace, tdefName, tdefVersion)
                        .stream()
                        .filter(duService.filterByPlatform(platform))
                        .collect(Collectors.toSet());

                if (version != null) {
                    switch (version) {
                        case "latest":
                            dus = duService.onlyLatests(dus);
                            break;
                        case "release":
                            dus = duService.onlyReleases(dus);
                            break;
                        default:
                            dus = duService.satisfies(dus, version);
                            break;
                    }
                }

                return new ResponseEntity<>(duMapper.deployUnitsToDeployUnitDTOs(dus), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                    new ErrorDTO("Unable to find TypeDefinition " + namespace + "." + tdefName + "/" + tdefVersion),
                    HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(new ErrorDTO("Unable to find Namespace " + namespace), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus : get all the deployUnits attached to a
     * specific "namespace.TypeDefinition/version"
     * Filtering results by query params "platform" and "version"
     *   - platform: comma separated list of strings (ie. js,java)
     *   - version:  latest | release | semver range
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/specific-dus")
    @Timed
    public ResponseEntity<Set<DeployUnitDTO>> getSpecificDeployUnits(@PathVariable String namespace, @PathVariable String tdefName,
                                            @PathVariable Long tdefVersion, HttpServletRequest request) {
        log.debug("REST request to get specific deployUnits from {}.{}/{} (params={})",
                namespace, tdefName, tdefVersion, request.getParameterMap());
        return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, tdefName, tdefVersion)
                .map(tdef -> {
                    Set<DeployUnit> dus = duRepository.findByTypeDefinitionId(tdef.getId());
                    Map<String, Set<DeployUnit>> dusByPlatform = new HashMap<>();
                    dus.forEach(du -> {
                        Set<DeployUnit> deployUnits = dusByPlatform.computeIfAbsent(du.getPlatform(), p -> new HashSet<>());
                        deployUnits.add(du);
                    });
                    Map<String, String[]> paramFilters = request.getParameterMap();
                    Map<String, String> filters = duRepository.findDistinctPlatformByTypeDefinitionId(tdef.getId())
                            .stream()
                            .collect(Collectors.toMap(e -> e, e -> {
                                String[] f = paramFilters.get(e);
                                if (f != null && f.length > 0) {
                                    return f[0];
                                } else {
                                    return "release";
                                }
                            }));
                    return ResponseEntity.ok(duMapper.deployUnitsToDeployUnitDTOs(duService.filter(tdef, filters)));
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name : get the all the deployUnits with a specific
     * name and attached to a specified namespace and a specified typeDefinition name and version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus/{name:.+}")
    @Timed
    public Set<DeployUnitDTO> getDeployUnitsByName(@PathVariable String namespace, @PathVariable String tdefName,
                                          @PathVariable Long tdefVersion, @PathVariable String name) {
        log.debug("REST request to get DeployUnits {} from Namespace: {} and TypeDefinition: {}/{}", name,
            namespace, tdefName, tdefVersion);
        return duMapper.deployUnitsToDeployUnitDTOs(duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndName(
                namespace, tdefName, tdefVersion, name));
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name/:version : get the all the deployUnits with
     * a specific name and version attached to a specified namespace and a specified typeDefinition name and version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @param version the version of the DeployUnits
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus/{name}/{version:.+}")
    @Timed
    public Set<DeployUnitDTO> getDeployUnitsByNameAndVersion(@PathVariable String namespace, @PathVariable String tdefName,
                                          @PathVariable Long tdefVersion, @PathVariable String name,
                                          @PathVariable String version) {
        log.debug("REST request to get DeployUnits {}-{} from Namespace: {} and TypeDefinition: {}/{}", name, version,
            namespace, tdefName, tdefVersion);
        return duMapper.deployUnitsToDeployUnitDTOs(
                duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersion(
                        namespace, tdefName, tdefVersion, name, version)
        );
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name/:version/:platform : get a specific deployUnit
     * version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @param version the version of the DeployUnits
     * @param platform the platform of the DeployUnits
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus/{name}/{version}/{platform:.+}")
    @Timed
    public ResponseEntity<DeployUnitDTO> getDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                                    @PathVariable Long tdefVersion, @PathVariable String name,
                                                    @PathVariable String version, @PathVariable String platform) {
        log.debug("REST request to get DeployUnits {}-{}-{} from Namespace: {} and TypeDefinition: {}/{}", name,
            version, platform, namespace, tdefName, tdefVersion);
        return duRepository.findOneByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersionAndPlatform(
            namespace, tdefName, tdefVersion, name, version, platform)
            .map(du -> new ResponseEntity<>(new DeployUnitDTO(du), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dus/:id : delete the "id" deployUnit.
     *
     * @param id the id of the deployUnit to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dus/{id}")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> deleteDeployUnit(@PathVariable Long id) {
        log.debug("REST user \"{}\" request to delete DeployUnit : {}", SecurityUtils.getCurrentUserLogin(), id);
        return Optional.ofNullable(duRepository.findOne(id))
            .map(du -> deleteDeployUnit(
                du.getTypeDefinition().getNamespace().getName(),
                du.getTypeDefinition().getName(),
                du.getTypeDefinition().getVersion(),
                du.getName(),
                du.getVersion(),
                du.getPlatform()
            ))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name/:version/:platform : delete a specific deployUnit
     * version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @param version the version of the DeployUnits
     * @param platform the platform of the DeployUnits
     * @return the ResponseEntity with status 200 (OK), or with status 404 (Not Found)
     */
    @DeleteMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/dus/{name}/{version}/{platform:.+}")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> deleteDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                              @PathVariable Long tdefVersion, @PathVariable String name,
                                              @PathVariable String version, @PathVariable String platform) {
        log.debug("REST user \"{}\" request to delete DeployUnits {}-{}-{} from Namespace: {} and TypeDefinition: {}/{}",
                SecurityUtils.getCurrentUserLogin(), name, version, platform, namespace, tdefName, tdefVersion);
        Namespace ns = nsRepository.findOne(namespace);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace "+name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            if (user != null) {
                Authority admin = authRepository.findOne(AuthoritiesConstants.ADMIN);
                if (user.getAuthorities().contains(admin)
                    || nsRepository.findOneByNameAndMemberName(namespace, SecurityUtils.getCurrentUserLogin()).isPresent()) {
                    return duRepository.findOneByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersionAndPlatform(
                        namespace, tdefName, tdefVersion, name, version, platform)
                        .map(du -> {
                            // delete du
                            du.getTypeDefinition().setLastModifiedDate(ZonedDateTime.now());
                            duRepository.delete(du.getId());
                            tdefsRepository.save(du.getTypeDefinition());
                            return new ResponseEntity<>(HttpStatus.OK);
                        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                } else {
                    return new ResponseEntity<>(new ErrorDTO("you are not a member of '" + namespace + "' namespace"), HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/latest-dus/:platform: get the latest deployUnit for a
     * specific platform
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param platform the name of the platform
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnit, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/latest-dus/{platform:.+}")
    @Timed
    public ResponseEntity<DeployUnit> getLatestDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                                          @PathVariable Long tdefVersion, @PathVariable String platform) {
        log.debug("REST request to get the latest DeployUnit for {}.{}/{} in platform {}", namespace, tdefName, tdefVersion, platform);
        Set<DeployUnit> dus = duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndPlatform(
            namespace, tdefName, tdefVersion, platform);
        if (dus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<DeployUnit> sortedDus = sortDus(dus, false);
            return new ResponseEntity<>(sortedDus.get(sortedDus.size() - 1), HttpStatus.OK);
        }
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/latest-dus: get the latest deployUnit for each platform
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/latest-dus")
    @Timed
    public ResponseEntity<Set<DeployUnitDTO>> getLatestDeployUnitForPlatforms(@PathVariable String namespace, @PathVariable String tdefName,
                                                                                     @PathVariable Long tdefVersion) {
        log.debug("REST request to get the latest DeployUnits for {}.{}/{} for each platform", namespace, tdefName, tdefVersion);
        Set<DeployUnit> dus = duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersion(namespace, tdefName, tdefVersion);
        if (dus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, DeployUnitDTO> dusByPlatform = new HashMap<>();
            List<DeployUnit> sortedDus = sortDus(dus, false);
            for (int i=sortedDus.size() - 1; i >= 0; i--) {
                if (!dusByPlatform.containsKey(sortedDus.get(i).getPlatform())) {
                    dusByPlatform.put(sortedDus.get(i).getPlatform(), new DeployUnitDTO(sortedDus.get(i)));
                }
            }
            return new ResponseEntity<>(dusByPlatform.values().stream().collect(Collectors.toSet()), HttpStatus.OK);
        }
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/released-dus/:platform: get the latest released deployUnit
     * for a specific platform
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param platform the name of the platform
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnit, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/released-dus/{platform:.+}")
    @Timed
    public ResponseEntity<DeployUnit> getLatestReleasedDeployUnit(@PathVariable String namespace,
                                                                  @PathVariable String tdefName, @PathVariable Long tdefVersion, @PathVariable String platform) {
        log.debug("REST request to get the latest released DeployUnit for {}.{}/{} in platform {}",
            namespace, tdefName, tdefVersion, platform);
        Set<DeployUnit> dus = duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndPlatform(
            namespace, tdefName, tdefVersion, platform);
        if (dus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<DeployUnit> sortedDus = sortDus(dus, true);
            if (sortedDus.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(sortedDus.get(sortedDus.size() - 1), HttpStatus.OK);
            }
        }
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/released-dus: get the latest released deployUnits
     * for each platform
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{tdefName:"+ Constants.TDEF_NAME_REGEX+"}/{tdefVersion:[\\d]+}/released-dus")
    @Timed
    public ResponseEntity<Collection<DeployUnitDTO>> getLatestReleasedDeployUnitForPlatforms(
        @PathVariable String namespace, @PathVariable String tdefName, @PathVariable Long tdefVersion) {
        log.debug("REST request to get the latest released DeployUnits for {}.{}/{} for each platform",
            namespace, tdefName, tdefVersion);
        Set<DeployUnit> dus = duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersion(namespace, tdefName, tdefVersion);
        if (dus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, DeployUnitDTO> dusByPlatform = new HashMap<>();
            List<DeployUnit> sortedDus = sortDus(dus, true);
            if (sortedDus.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                for (int i=sortedDus.size() - 1; i >= 0; i--) {
                    if (!dusByPlatform.containsKey(sortedDus.get(i).getPlatform())) {
                        dusByPlatform.put(sortedDus.get(i).getPlatform(), new DeployUnitDTO(sortedDus.get(i)));
                    }
                }
                return new ResponseEntity<>(dusByPlatform.values(), HttpStatus.OK);
            }
        }
    }

    private List<DeployUnit> sortDus(Set<DeployUnit> dus, boolean onlyReleases) {
        Stream<DeployUnit> dusStream = dus.stream();
        if (onlyReleases) {
            dusStream = dusStream.filter(du -> {
                Version v = new Version.Builder(du.getVersion()).build();
                return v.getPreReleaseVersion() == null || v.getPreReleaseVersion().isEmpty();
            });
        }
        return dusStream
            .sorted((du0, du1) -> {
                Version v0 = new Version.Builder(du0.getVersion()).build();
                Version v1 = new Version.Builder(du1.getVersion()).build();
                return SemverUtil.compare(v0, v1);
            })
            .collect(Collectors.toList());
    }
}

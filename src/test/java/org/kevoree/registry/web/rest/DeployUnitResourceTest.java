package org.kevoree.registry.web.rest;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.web.rest.dto.DeployUnitDTO;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the DeployUnitResource REST controller.
 *
 * @see DeployUnitResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DeployUnitResourceTest {

    private static final String NAMESPACE = "kevoree";

    private static final String TDEF_NAME = "ConsolePrinter";
    private static final Long TDEF_VERSION = 1L;

    private static final String DEFAULT_NAME = "consoleprinter-deployUnit";
    private static final String DEFAULT_VERSION = "1.2.3-SNAPSHOT";
    private static final String DEFAULT_PLATFORM = "atari";
    private static final String DEFAULT_MODEL = "{}";

    private static final String UPDATED_MODEL = "{\"foo\": \"bar\"}";

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
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDeployUnitMockMvc;

    private DeployUnitDTO deployUnit;
    private TypeDefinition tdef;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DeployUnitResource deployUnitResource = new DeployUnitResource();
        ReflectionTestUtils.setField(deployUnitResource, "duRepository", duRepository);
        ReflectionTestUtils.setField(deployUnitResource, "tdefsRepository", tdefsRepository);
        ReflectionTestUtils.setField(deployUnitResource, "duService", duService);
        ReflectionTestUtils.setField(deployUnitResource, "nsRepository", nsRepository);
        ReflectionTestUtils.setField(deployUnitResource, "authRepository", authRepository);
        ReflectionTestUtils.setField(deployUnitResource, "userService", userService);
        this.restDeployUnitMockMvc = MockMvcBuilders.standaloneSetup(deployUnitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tdef = tdefsRepository.findOne(2L);
        deployUnit = new DeployUnitDTO(DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_PLATFORM, DEFAULT_MODEL);
    }

    @Test
    @Transactional
    public void createDeployUnit() throws Exception {
        int databaseSizeBeforeCreate = duRepository.findAll().size();

        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/namespaces/{namespace}/tdefs/{name}/{version}/dus",
            NAMESPACE, TDEF_NAME, TDEF_VERSION)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
                .andExpect(status().isCreated());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeCreate + 1);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDeployUnit.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
        assertThat(testDeployUnit.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testDeployUnit.getTypeDefinition().getName()).isEqualTo(TDEF_NAME);
        assertThat(testDeployUnit.getTypeDefinition().getVersion()).isEqualTo(TDEF_VERSION);
        assertThat(testDeployUnit.getTypeDefinition().getNamespace().getName()).isEqualTo(NAMESPACE);
        assertThat(testDeployUnit.getTypeDefinition().getLastModifiedDate().getMillis()).isLessThan(DateTime.now().getMillis());
    }

    @Test
    @Transactional
    public void createDeployUnitForUnknownTdef() throws Exception {
        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/namespaces/{namespace}/tdefs/{name}/{version}/dus",
            NAMESPACE, "unknown", "1")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void createDeployUnitWithoutBeingMember() throws Exception {
        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/namespaces/{namespace}/tdefs/{name}/{version}/dus",
            "user", "Foo", "1")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    public void createDeployUnitForUnknownNamespace() throws Exception {
        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/namespaces/{namespace}/tdefs/{name}/{version}/dus",
            "unknown", "aaa", "1")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = duRepository.findAll().size();
        // set the field null
        deployUnit.setName(null);

        // Create the DeployUnit, which fails.
        restDeployUnitMockMvc.perform(post("/api/namespaces/{namespace}/tdefs/{name}/{version}/dus",
            NAMESPACE, TDEF_NAME, TDEF_VERSION)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
                .andExpect(status().isBadRequest());

        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = duRepository.findAll().size();
        // set the field null
        deployUnit.setVersion(null);

        // Create the DeployUnit, which fails.
        restDeployUnitMockMvc.perform(post("/api/namespaces/{namespace}/tdefs/{name}/{version}/dus",
            NAMESPACE, TDEF_NAME, TDEF_VERSION)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
                .andExpect(status().isBadRequest());

        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDeployUnits() throws Exception {
        // Initialize the database
        DeployUnit deployUnit = duService.create(tdef, this.deployUnit);
        duRepository.flush();

        // Get all the deployUnits
        restDeployUnitMockMvc.perform(get("/api/dus?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(deployUnit.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
                .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM)))
                .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)));
    }

    @Test
    @Transactional
    public void getDeployUnit() throws Exception {
        // Initialize the database
        DeployUnit deployUnit = duService.create(tdef, this.deployUnit);
        duRepository.flush();

        // Get the deployUnit
        restDeployUnitMockMvc.perform(get("/api/dus/{id}", deployUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(deployUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL));
    }

    @Test
    @Transactional
    public void getSpecificDeployUnit() throws Exception {
        // Initialize the database
        DeployUnit deployUnit = duService.create(tdef, this.deployUnit);
        duRepository.flush();

        // Get the deployUnit
        restDeployUnitMockMvc.perform(
            get("/api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}",
                deployUnit.getTypeDefinition().getNamespace().getName(),
                deployUnit.getTypeDefinition().getName(),
                deployUnit.getTypeDefinition().getVersion(),
                deployUnit.getName(),
                deployUnit.getVersion(),
                deployUnit.getPlatform()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(deployUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM))
            .andExpect(jsonPath("$.typeDefinition.id").value(deployUnit.getTypeDefinition().getId().intValue()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL));
    }


    @Test
    @Transactional
    public void getNonExistingDeployUnit() throws Exception {
        // Get the deployUnit
        restDeployUnitMockMvc.perform(get("/api/dus/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeployUnit() throws Exception {
        // Initialize the database
        DeployUnit du = duService.create(tdef, this.deployUnit);
        duRepository.flush();
        int databaseSizeBeforeUpdate = duRepository.findAll().size();

        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // tdef creation timestamp
        long tdefLastModified = this.tdef.getLastModifiedDate().getMillis();

        // Update the deployUnit
        this.deployUnit.setId(du.getId());
        this.deployUnit.setModel(UPDATED_MODEL);

        restDeployUnitMockMvc.perform(
            put("/api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}",
            NAMESPACE, TDEF_NAME, TDEF_VERSION, DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_PLATFORM)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(this.deployUnit)))
                .andExpect(status().isOk());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeUpdate);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDeployUnit.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
        assertThat(testDeployUnit.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testDeployUnit.getTypeDefinition().getName()).isEqualTo(TDEF_NAME);
        assertThat(testDeployUnit.getTypeDefinition().getVersion()).isEqualTo(TDEF_VERSION);
        assertThat(testDeployUnit.getTypeDefinition().getNamespace().getName()).isEqualTo(NAMESPACE);
        // tdef last modified timestamp should be greater than now
        assertThat(testDeployUnit.getTypeDefinition().getLastModifiedDate().getMillis()).isGreaterThan(tdefLastModified);
        // deployUnit should have a creation timestamp
        assertThat(testDeployUnit.getCreatedDate().getMillis()).isLessThan(DateTime.now().getMillis());
        // deployUnit should also have a creation login
        assertThat(testDeployUnit.getCreatedBy()).isEqualTo("kevoree");
    }

    @Test
    @Transactional
    public void badUpdateDeployUnit() throws Exception {
        // Initialize the database
        DeployUnit du = duService.create(tdef, this.deployUnit);
        duRepository.flush();
        int databaseSizeBeforeUpdate = duRepository.findAll().size();

        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Update the deployUnit
        this.deployUnit.setId(du.getId());
        this.deployUnit.setPlatform("foo");
        this.deployUnit.setModel(UPDATED_MODEL);

        restDeployUnitMockMvc.perform(
            put("/api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}",
                NAMESPACE, TDEF_NAME, TDEF_VERSION, DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_PLATFORM)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(this.deployUnit)))
            .andExpect(status().isBadRequest());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeUpdate);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDeployUnit.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
        assertThat(testDeployUnit.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testDeployUnit.getTypeDefinition().getName()).isEqualTo(TDEF_NAME);
        assertThat(testDeployUnit.getTypeDefinition().getVersion()).isEqualTo(TDEF_VERSION);
        assertThat(testDeployUnit.getTypeDefinition().getNamespace().getName()).isEqualTo(NAMESPACE);
    }

    @Test
    @Transactional
    public void badUpdateDeployUnitWhenReleasedVersion() throws Exception {
        // Initialize the database
        this.deployUnit.setVersion("1.2.3");
        DeployUnit du = duService.create(tdef, this.deployUnit);
        duRepository.flush();
        int databaseSizeBeforeUpdate = duRepository.findAll().size();

        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Update the deployUnit
        this.deployUnit.setId(du.getId());
        this.deployUnit.setModel(UPDATED_MODEL);

        restDeployUnitMockMvc.perform(
            put("/api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}",
                NAMESPACE, TDEF_NAME, TDEF_VERSION, DEFAULT_NAME, "1.2.3", DEFAULT_PLATFORM)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(this.deployUnit)))
            .andExpect(status().isForbidden());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeUpdate);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDeployUnit.getVersion()).isEqualTo("1.2.3");
        assertThat(testDeployUnit.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
        assertThat(testDeployUnit.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testDeployUnit.getTypeDefinition().getName()).isEqualTo(TDEF_NAME);
        assertThat(testDeployUnit.getTypeDefinition().getVersion()).isEqualTo(TDEF_VERSION);
        assertThat(testDeployUnit.getTypeDefinition().getNamespace().getName()).isEqualTo(NAMESPACE);
    }

    @Test
    @Transactional
    public void mismatchUriDTOUpdateDeployUnit() throws Exception {
        // Initialize the database
        DeployUnit du = duService.create(tdef, this.deployUnit);
        duRepository.flush();
        int databaseSizeBeforeUpdate = duRepository.findAll().size();

        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Update the deployUnit
        this.deployUnit.setId(du.getId());
        this.deployUnit.setModel(UPDATED_MODEL);

        // call PUT with a wrong parent TypeDef in the URI compared to DTO
        restDeployUnitMockMvc.perform(
            put("/api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}",
                NAMESPACE, "Ticker", "1", DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_PLATFORM)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(this.deployUnit)))
            .andExpect(status().isNotFound());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeUpdate);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDeployUnit.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
        assertThat(testDeployUnit.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testDeployUnit.getTypeDefinition().getName()).isEqualTo(TDEF_NAME);
        assertThat(testDeployUnit.getTypeDefinition().getVersion()).isEqualTo(TDEF_VERSION);
        assertThat(testDeployUnit.getTypeDefinition().getNamespace().getName()).isEqualTo(NAMESPACE);
    }
}

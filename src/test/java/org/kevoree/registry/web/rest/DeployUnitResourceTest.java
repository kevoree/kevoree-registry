package org.kevoree.registry.web.rest;

import com.google.common.collect.Lists;
import net.minidev.json.JSONUtil;
import net.minidev.json.parser.JSONParser;
import org.json4s.jackson.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.web.rest.dto.DeployUnitDTO;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.json.JsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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

    private static final String DEFAULT_NAME = "consoleprinter-deployUnit";
    private static final String UPDATED_NAME = "org.kevoree.library.java.printer";
    private static final String DEFAULT_VERSION = "1.2.3";
    private static final String UPDATED_VERSION = "42-SNAPSHOT";
    private static final String DEFAULT_PLATFORM = "atari";
    private static final String UPDATED_PLATFORM = "js";
    private static final String DEFAULT_MODEL = "{}";
    private static final String UPDATED_MODEL = "{\"foo\": \"bar\"}";
    private static final Long   TDEF_ID = 2L;

    @Inject
    private DeployUnitRepository duRepository;

    @Inject
    private TypeDefinitionRepository tdefsRepository;

    @Inject
    private DeployUnitService duService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDeployUnitMockMvc;

    private DeployUnitDTO deployUnit;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DeployUnitResource deployUnitResource = new DeployUnitResource();
        ReflectionTestUtils.setField(deployUnitResource, "duRepository", duRepository);
        ReflectionTestUtils.setField(deployUnitResource, "tdefsRepository", tdefsRepository);
        ReflectionTestUtils.setField(deployUnitResource, "duService", duService);
        this.restDeployUnitMockMvc = MockMvcBuilders.standaloneSetup(deployUnitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        deployUnit = new DeployUnitDTO(TDEF_ID, DEFAULT_NAME, DEFAULT_VERSION, DEFAULT_PLATFORM, DEFAULT_MODEL);
    }

    @Test
    @Transactional
    public void createDeployUnit() throws Exception {
        int databaseSizeBeforeCreate = duRepository.findAll().size();

        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/dus")
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
        assertThat(testDeployUnit.getTypeDefinition().getId()).isEqualTo(TDEF_ID);
    }

    @Test
    @Transactional
    public void createDeployUnitForUnknownTdef() throws Exception {
        // add "kevoree" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("kevoree", null, AuthoritiesConstants.USER));

        deployUnit.setTdefId(42L);

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/dus")
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

        deployUnit.setTdefId(6L);

        // Create the DeployUnit
        restDeployUnitMockMvc.perform(post("/api/dus")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = duRepository.findAll().size();
        // set the field null
        deployUnit.setName(null);

        // Create the DeployUnit, which fails.
        restDeployUnitMockMvc.perform(post("/api/dus")
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
        restDeployUnitMockMvc.perform(post("/api/dus")
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
        DeployUnit deployUnit = duService.create(this.deployUnit);
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
        DeployUnit deployUnit = duService.create(this.deployUnit);
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
        DeployUnit deployUnit = duService.create(this.deployUnit);
        duRepository.flush();

        // Get the deployUnit
        restDeployUnitMockMvc.perform(
            get("/api/dus/{namespace}/{tdef}/{tdefVersion}/{name}/{version}/{platform}",
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
            .andExpect(jsonPath("$.typeDefinition.id").value(TDEF_ID.intValue()))
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
        DeployUnit deployUnit = duService.create(this.deployUnit);
        duRepository.flush();
        int databaseSizeBeforeUpdate = duRepository.findAll().size();

        // Update the deployUnit
        DeployUnit updatedDeployUnit = new DeployUnit();
        updatedDeployUnit.setId(deployUnit.getId());
        updatedDeployUnit.setName(UPDATED_NAME);
        updatedDeployUnit.setVersion(UPDATED_VERSION);
        updatedDeployUnit.setPlatform(UPDATED_PLATFORM);
        updatedDeployUnit.setModel(UPDATED_MODEL);

        restDeployUnitMockMvc.perform(put("/api/dus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDeployUnit)))
                .andExpect(status().isOk());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeUpdate);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDeployUnit.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(UPDATED_PLATFORM);
        assertThat(testDeployUnit.getModel()).isEqualTo(UPDATED_MODEL);
    }

    @Test
    @Transactional
    public void deleteDeployUnit() throws Exception {
        // Initialize the database
        DeployUnit deployUnit = duService.create(this.deployUnit);
        duRepository.flush();
        int databaseSizeBeforeDelete = duRepository.findAll().size();

        // Get the deployUnit
        restDeployUnitMockMvc.perform(delete("/api/dus/{id}", deployUnit.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<DeployUnit> deployUnits = duRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeDelete - 1);
    }
}

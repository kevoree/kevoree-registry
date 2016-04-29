package org.kevoree.registry.web.rest;

import org.kevoree.registry.KevoreeRegistryApp;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.service.DeployUnitService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the DeployUnitResource REST controller.
 *
 * @see DeployUnitResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KevoreeRegistryApp.class)
@WebAppConfiguration
@IntegrationTest
public class DeployUnitResourceIntTest {

    private static final String DEFAULT_PLATFORM = "A";
    private static final String UPDATED_PLATFORM = "B";
    private static final String DEFAULT_PATH = "AAAAA";
    private static final String UPDATED_PATH = "BBBBB";

    private static final Long DEFAULT_PRIORITY = 1L;
    private static final Long UPDATED_PRIORITY = 2L;

    @Inject
    private DeployUnitRepository deployUnitRepository;

    @Inject
    private DeployUnitService deployUnitService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDeployUnitMockMvc;

    private DeployUnit deployUnit;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DeployUnitResource deployUnitResource = new DeployUnitResource();
        ReflectionTestUtils.setField(deployUnitResource, "deployUnitService", deployUnitService);
        this.restDeployUnitMockMvc = MockMvcBuilders.standaloneSetup(deployUnitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        deployUnit = new DeployUnit();
        deployUnit.setPlatform(DEFAULT_PLATFORM);
        deployUnit.setPath(DEFAULT_PATH);
        deployUnit.setPriority(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    public void createDeployUnit() throws Exception {
        int databaseSizeBeforeCreate = deployUnitRepository.findAll().size();

        // Create the DeployUnit

        restDeployUnitMockMvc.perform(post("/api/deploy-units")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
                .andExpect(status().isCreated());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = deployUnitRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeCreate + 1);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
        assertThat(testDeployUnit.getPath()).isEqualTo(DEFAULT_PATH);
        assertThat(testDeployUnit.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    public void checkPlatformIsRequired() throws Exception {
        int databaseSizeBeforeTest = deployUnitRepository.findAll().size();
        // set the field null
        deployUnit.setPlatform(null);

        // Create the DeployUnit, which fails.

        restDeployUnitMockMvc.perform(post("/api/deploy-units")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
                .andExpect(status().isBadRequest());

        List<DeployUnit> deployUnits = deployUnitRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPathIsRequired() throws Exception {
        int databaseSizeBeforeTest = deployUnitRepository.findAll().size();
        // set the field null
        deployUnit.setPath(null);

        // Create the DeployUnit, which fails.

        restDeployUnitMockMvc.perform(post("/api/deploy-units")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deployUnit)))
                .andExpect(status().isBadRequest());

        List<DeployUnit> deployUnits = deployUnitRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDeployUnits() throws Exception {
        // Initialize the database
        deployUnitRepository.saveAndFlush(deployUnit);

        // Get all the deployUnits
        restDeployUnitMockMvc.perform(get("/api/deploy-units?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(deployUnit.getId().intValue())))
                .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM.toString())))
                .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())))
                .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.intValue())));
    }

    @Test
    @Transactional
    public void getDeployUnit() throws Exception {
        // Initialize the database
        deployUnitRepository.saveAndFlush(deployUnit);

        // Get the deployUnit
        restDeployUnitMockMvc.perform(get("/api/deploy-units/{id}", deployUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(deployUnit.getId().intValue()))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM.toString()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDeployUnit() throws Exception {
        // Get the deployUnit
        restDeployUnitMockMvc.perform(get("/api/deploy-units/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeployUnit() throws Exception {
        // Initialize the database
        deployUnitService.save(deployUnit);

        int databaseSizeBeforeUpdate = deployUnitRepository.findAll().size();

        // Update the deployUnit
        DeployUnit updatedDeployUnit = new DeployUnit();
        updatedDeployUnit.setId(deployUnit.getId());
        updatedDeployUnit.setPlatform(UPDATED_PLATFORM);
        updatedDeployUnit.setPath(UPDATED_PATH);
        updatedDeployUnit.setPriority(UPDATED_PRIORITY);

        restDeployUnitMockMvc.perform(put("/api/deploy-units")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDeployUnit)))
                .andExpect(status().isOk());

        // Validate the DeployUnit in the database
        List<DeployUnit> deployUnits = deployUnitRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeUpdate);
        DeployUnit testDeployUnit = deployUnits.get(deployUnits.size() - 1);
        assertThat(testDeployUnit.getPlatform()).isEqualTo(UPDATED_PLATFORM);
        assertThat(testDeployUnit.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testDeployUnit.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void deleteDeployUnit() throws Exception {
        // Initialize the database
        deployUnitService.save(deployUnit);

        int databaseSizeBeforeDelete = deployUnitRepository.findAll().size();

        // Get the deployUnit
        restDeployUnitMockMvc.perform(delete("/api/deploy-units/{id}", deployUnit.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<DeployUnit> deployUnits = deployUnitRepository.findAll();
        assertThat(deployUnits).hasSize(databaseSizeBeforeDelete - 1);
    }
}

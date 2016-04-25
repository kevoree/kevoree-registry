package org.kevoree.registry.web.rest;

import org.kevoree.registry.KevoreeRegistryApp;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.service.TypeDefinitionService;

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
 * Test class for the TypeDefinitionResource REST controller.
 *
 * @see TypeDefinitionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KevoreeRegistryApp.class)
@WebAppConfiguration
@IntegrationTest
public class TypeDefinitionResourceIntTest {

    private static final String DEFAULT_NAME = "A";
    private static final String UPDATED_NAME = "B";
    private static final String DEFAULT_SERIALIZED_MODEL = "AAAAA";
    private static final String UPDATED_SERIALIZED_MODEL = "BBBBB";

    private static final Long DEFAULT_VERSION = 0L;
    private static final Long UPDATED_VERSION = 1L;
    private static final String DEFAULT_PLATFORM = "a";
    private static final String UPDATED_PLATFORM = "b";

    @Inject
    private TypeDefinitionRepository typeDefinitionRepository;

    @Inject
    private TypeDefinitionService typeDefinitionService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTypeDefinitionMockMvc;

    private TypeDefinition typeDefinition;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeDefinitionResource typeDefinitionResource = new TypeDefinitionResource();
        ReflectionTestUtils.setField(typeDefinitionResource, "typeDefinitionService", typeDefinitionService);
        this.restTypeDefinitionMockMvc = MockMvcBuilders.standaloneSetup(typeDefinitionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        typeDefinition = new TypeDefinition();
        typeDefinition.setName(DEFAULT_NAME);
        typeDefinition.setSerializedModel(DEFAULT_SERIALIZED_MODEL);
        typeDefinition.setVersion(DEFAULT_VERSION);
        typeDefinition.setPlatform(DEFAULT_PLATFORM);
    }

    @Test
    @Transactional
    public void createTypeDefinition() throws Exception {
        int databaseSizeBeforeCreate = typeDefinitionRepository.findAll().size();

        // Create the TypeDefinition

        restTypeDefinitionMockMvc.perform(post("/api/type-definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeDefinition)))
                .andExpect(status().isCreated());

        // Validate the TypeDefinition in the database
        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeCreate + 1);
        TypeDefinition testTypeDefinition = typeDefinitions.get(typeDefinitions.size() - 1);
        assertThat(testTypeDefinition.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypeDefinition.getSerializedModel()).isEqualTo(DEFAULT_SERIALIZED_MODEL);
        assertThat(testTypeDefinition.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testTypeDefinition.getPlatform()).isEqualTo(DEFAULT_PLATFORM);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeDefinitionRepository.findAll().size();
        // set the field null
        typeDefinition.setName(null);

        // Create the TypeDefinition, which fails.

        restTypeDefinitionMockMvc.perform(post("/api/type-definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeDefinition)))
                .andExpect(status().isBadRequest());

        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSerializedModelIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeDefinitionRepository.findAll().size();
        // set the field null
        typeDefinition.setSerializedModel(null);

        // Create the TypeDefinition, which fails.

        restTypeDefinitionMockMvc.perform(post("/api/type-definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeDefinition)))
                .andExpect(status().isBadRequest());

        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeDefinitionRepository.findAll().size();
        // set the field null
        typeDefinition.setVersion(null);

        // Create the TypeDefinition, which fails.

        restTypeDefinitionMockMvc.perform(post("/api/type-definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeDefinition)))
                .andExpect(status().isBadRequest());

        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPlatformIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeDefinitionRepository.findAll().size();
        // set the field null
        typeDefinition.setPlatform(null);

        // Create the TypeDefinition, which fails.

        restTypeDefinitionMockMvc.perform(post("/api/type-definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeDefinition)))
                .andExpect(status().isBadRequest());

        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTypeDefinitions() throws Exception {
        // Initialize the database
        typeDefinitionRepository.saveAndFlush(typeDefinition);

        // Get all the typeDefinitions
        restTypeDefinitionMockMvc.perform(get("/api/type-definitions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(typeDefinition.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].serializedModel").value(hasItem(DEFAULT_SERIALIZED_MODEL.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.intValue())))
                .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM.toString())));
    }

    @Test
    @Transactional
    public void getTypeDefinition() throws Exception {
        // Initialize the database
        typeDefinitionRepository.saveAndFlush(typeDefinition);

        // Get the typeDefinition
        restTypeDefinitionMockMvc.perform(get("/api/type-definitions/{id}", typeDefinition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(typeDefinition.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.serializedModel").value(DEFAULT_SERIALIZED_MODEL.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.intValue()))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTypeDefinition() throws Exception {
        // Get the typeDefinition
        restTypeDefinitionMockMvc.perform(get("/api/type-definitions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTypeDefinition() throws Exception {
        // Initialize the database
        typeDefinitionService.save(typeDefinition);

        int databaseSizeBeforeUpdate = typeDefinitionRepository.findAll().size();

        // Update the typeDefinition
        TypeDefinition updatedTypeDefinition = new TypeDefinition();
        updatedTypeDefinition.setId(typeDefinition.getId());
        updatedTypeDefinition.setName(UPDATED_NAME);
        updatedTypeDefinition.setSerializedModel(UPDATED_SERIALIZED_MODEL);
        updatedTypeDefinition.setVersion(UPDATED_VERSION);
        updatedTypeDefinition.setPlatform(UPDATED_PLATFORM);

        restTypeDefinitionMockMvc.perform(put("/api/type-definitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTypeDefinition)))
                .andExpect(status().isOk());

        // Validate the TypeDefinition in the database
        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeUpdate);
        TypeDefinition testTypeDefinition = typeDefinitions.get(typeDefinitions.size() - 1);
        assertThat(testTypeDefinition.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypeDefinition.getSerializedModel()).isEqualTo(UPDATED_SERIALIZED_MODEL);
        assertThat(testTypeDefinition.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testTypeDefinition.getPlatform()).isEqualTo(UPDATED_PLATFORM);
    }

    @Test
    @Transactional
    public void deleteTypeDefinition() throws Exception {
        // Initialize the database
        typeDefinitionService.save(typeDefinition);

        int databaseSizeBeforeDelete = typeDefinitionRepository.findAll().size();

        // Get the typeDefinition
        restTypeDefinitionMockMvc.perform(delete("/api/type-definitions/{id}", typeDefinition.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TypeDefinition> typeDefinitions = typeDefinitionRepository.findAll();
        assertThat(typeDefinitions).hasSize(databaseSizeBeforeDelete - 1);
    }
}

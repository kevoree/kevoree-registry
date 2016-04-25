package org.kevoree.registry.web.rest;

import org.kevoree.registry.KevoreeRegistryApp;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.repository.NamespaceRepository;

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
 * Test class for the NamespaceResource REST controller.
 *
 * @see NamespaceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KevoreeRegistryApp.class)
@WebAppConfiguration
@IntegrationTest
public class NamespaceResourceIntTest {

    private static final String DEFAULT_NAME = "aaaaa";
    private static final String UPDATED_NAME = "bbbbb";

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restNamespaceMockMvc;

    private Namespace namespace;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NamespaceResource namespaceResource = new NamespaceResource();
        ReflectionTestUtils.setField(namespaceResource, "namespaceRepository", namespaceRepository);
        this.restNamespaceMockMvc = MockMvcBuilders.standaloneSetup(namespaceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        namespace = new Namespace();
        namespace.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createNamespace() throws Exception {
        int databaseSizeBeforeCreate = namespaceRepository.findAll().size();

        // Create the Namespace

        restNamespaceMockMvc.perform(post("/api/namespaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(namespace)))
                .andExpect(status().isCreated());

        // Validate the Namespace in the database
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(databaseSizeBeforeCreate + 1);
        Namespace testNamespace = namespaces.get(namespaces.size() - 1);
        assertThat(testNamespace.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = namespaceRepository.findAll().size();
        // set the field null
        namespace.setName(null);

        // Create the Namespace, which fails.

        restNamespaceMockMvc.perform(post("/api/namespaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(namespace)))
                .andExpect(status().isBadRequest());

        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNamespaces() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        // Get all the namespaces
        restNamespaceMockMvc.perform(get("/api/namespaces?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(namespace.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getNamespace() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        // Get the namespace
        restNamespaceMockMvc.perform(get("/api/namespaces/{id}", namespace.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(namespace.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNamespace() throws Exception {
        // Get the namespace
        restNamespaceMockMvc.perform(get("/api/namespaces/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNamespace() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();

        // Update the namespace
        Namespace updatedNamespace = new Namespace();
        updatedNamespace.setId(namespace.getId());
        updatedNamespace.setName(UPDATED_NAME);

        restNamespaceMockMvc.perform(put("/api/namespaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedNamespace)))
                .andExpect(status().isOk());

        // Validate the Namespace in the database
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(databaseSizeBeforeUpdate);
        Namespace testNamespace = namespaces.get(namespaces.size() - 1);
        assertThat(testNamespace.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteNamespace() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);
        int databaseSizeBeforeDelete = namespaceRepository.findAll().size();

        // Get the namespace
        restNamespaceMockMvc.perform(delete("/api/namespaces/{id}", namespace.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(databaseSizeBeforeDelete - 1);
    }
}

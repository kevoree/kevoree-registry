package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.web.rest.dto.NamedDTO;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see org.kevoree.registry.web.rest.UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class NamespaceResourceTest {

    private static final String DEFAULT_NAME = "kevoree";

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    private MockMvc restNamespaceMockMvc;

    private Namespace namespace;
    private User admin;

    @Before
    public void setup() {
        NamespaceResource namespaceResource = new NamespaceResource();
        ReflectionTestUtils.setField(namespaceResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(namespaceResource, "namespaceRepository", namespaceRepository);
        this.restNamespaceMockMvc = MockMvcBuilders.standaloneSetup(namespaceResource).build();
    }

    @Before
    public void initTest() {
        admin = userRepository.findOneByLogin("admin").get();

        namespace = new Namespace();
        namespace.setName(DEFAULT_NAME);
        namespace.addMember(admin);
        namespace.setOwner(admin);
    }

    @Test
    @Transactional
    public void testGetExistingNamespaces() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        admin.addNamespace(namespace);
        namespaceRepository.saveAndFlush(namespace);
        userRepository.saveAndFlush(admin);

        restNamespaceMockMvc.perform(get("/api/namespaces")
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.[0].members.[0]").value(admin.getLogin()));
    }

    @Test
    @Transactional
    public void testGetExistingNamespace() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        admin.addNamespace(namespace);
        namespaceRepository.saveAndFlush(namespace);
        userRepository.saveAndFlush(admin);

        restNamespaceMockMvc.perform(get("/api/namespaces/{name}", DEFAULT_NAME)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.members.[0]").value(admin.getLogin()));
    }

    @Test
    @Transactional
    public void testCreateNamespace() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        restNamespaceMockMvc.perform(post("/api/namespaces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new NamedDTO(namespace.getName()))))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        Namespace dbNs = namespaceRepository.findOne(DEFAULT_NAME);
        assertThat(dbNs.getMembers().iterator().next().getLogin()).isEqualTo(admin.getLogin());
        assertThat(dbNs.getOwner().getLogin()).isEqualTo(admin.getLogin());
        // Validate the user in db
        User dbUser = userRepository.findOneByLogin(admin.getLogin()).get();
        assertThat(dbUser.getNamespaces().iterator().next().getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void testAddMemberNamespace() throws Exception {
        admin.addNamespace(namespace);
        namespaceRepository.saveAndFlush(namespace);
        userRepository.saveAndFlush(admin);

        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        restNamespaceMockMvc.perform(post("/api/namespace/{name}/addMember", DEFAULT_NAME)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new NamedDTO("user"))))
            .andExpect(status().isOk());

        // Validate the namespace in db
        Namespace dbNs = namespaceRepository.findOne(DEFAULT_NAME);
        User user = userRepository.findOneByLogin("user").get();
        assertThat(dbNs.getMembers()).containsExactly(admin, user);
        assertThat(dbNs.getOwner().getLogin()).isEqualTo(admin.getLogin());
        // Validate the user in db
        User dbAdmin = userRepository.findOneByLogin(admin.getLogin()).get();
        assertThat(dbAdmin.getNamespaces().iterator().next().getName()).isEqualTo(DEFAULT_NAME);
        assertThat(user.getNamespaces().iterator().next().getName()).isEqualTo(DEFAULT_NAME);
    }
}

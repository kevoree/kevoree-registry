package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test class for the NamespaceResource REST controller.
 *
 * @see NamespaceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class NamespaceResourceTest {

    private static final String DEFAULT_NAME = "testnamespace";

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private TypeDefinitionRepository typeDefinitionRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    private MockMvc restNamespaceMockMvc;

    private Namespace namespace;
    private User admin;

    @Before
    public void setup() {
        NamespaceResource namespaceResource = new NamespaceResource();
        ReflectionTestUtils.setField(namespaceResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(namespaceResource, "userService", userService);
        ReflectionTestUtils.setField(namespaceResource, "namespaceRepository", namespaceRepository);
        ReflectionTestUtils.setField(namespaceResource, "authorityRepository", authorityRepository);
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

        restNamespaceMockMvc.perform(get("/api/namespaces")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$", hasSize(namespaceRepository.findAll().size())));
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
            .andExpect(jsonPath("$.members.[0].login").value(admin.getLogin()));
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

        restNamespaceMockMvc.perform(post("/api/namespaces/{name}/members", DEFAULT_NAME)
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
        assertThat(dbAdmin.getNamespaces()).contains(namespace);
        assertThat(user.getNamespaces()).contains(namespace);
    }

    @Test
    @Transactional
    public void testAddNsAndTdefAndDeleteNs() throws Exception {
        admin.addNamespace(namespace);
        namespaceRepository.saveAndFlush(namespace);
        userRepository.saveAndFlush(admin);

        // add a TypeDefinition to the Namespace
        TypeDefinition tdef = new TypeDefinition();
        tdef.setName("TestComp");
        tdef.setVersion("1.2.3");
        tdef.setNamespace(namespace);
        tdef.setModel("{}");
        namespace.addTypeDefinition(tdef);
        typeDefinitionRepository.saveAndFlush(tdef);

        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        restNamespaceMockMvc.perform(delete("/api/namespaces/{name}", DEFAULT_NAME)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // validate db
        assertThat(userRepository.findOneByLogin(admin.getLogin()).isPresent()).isTrue();
        assertThat(namespaceRepository.findOne(namespace.getName())).isNull();
        assertThat(typeDefinitionRepository.findOneByNamespaceNameAndNameAndVersion(namespace.getName(), tdef.getName(), tdef.getVersion()).isPresent()).isFalse();
    }
}

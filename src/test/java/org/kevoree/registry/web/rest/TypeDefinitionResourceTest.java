package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.web.rest.dto.NamedDTO;
import org.kevoree.registry.web.rest.dto.TypeDefinitionDTO;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TypeDefinitionResourceTest {

    private static final String DEFAULT_NAME = "kevoree";

    @Inject
    private TypeDefinitionRepository tdefsRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    private MockMvc restTdefsMockMvc;

    private TypeDefinition tdef;
    private Namespace namespace;
    private User admin;

    @Before
    public void setup() {
        TypeDefinitionResource tdefResource = new TypeDefinitionResource();
        ReflectionTestUtils.setField(tdefResource, "tdefsRepository", tdefsRepository);
        ReflectionTestUtils.setField(tdefResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(tdefResource, "namespaceRepository", namespaceRepository);
        this.restTdefsMockMvc = MockMvcBuilders.standaloneSetup(tdefResource).build();
    }

    @Before
    public void initTest() {
        admin = userRepository.findOneByLogin("admin").get();

        namespace = new Namespace();
        namespace.setName(DEFAULT_NAME);
        namespace.addMember(admin);
        namespace.setOwner(admin);

        admin.addNamespace(namespace);
        namespaceRepository.saveAndFlush(namespace);
        userRepository.saveAndFlush(admin);

        tdef = new TypeDefinition();
        tdef.setName("MyComp");
        tdef.setVersion("1.2.3");
        tdef.setSerializedModel("{}");
    }

    @Test
    @Transactional
    public void testCreateTypeDefinition() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        restTdefsMockMvc.perform(post("/api/tdefs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TypeDefinitionDTO(
                namespace.getName(),
                tdef.getName(),
                tdef.getVersion(),
                tdef.getSerializedModel()))))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            namespace.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(namespace.getName());
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(namespace.getName(), admin.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);
    }

    @Test
    @Transactional
    public void testCreateAlreadyExistingTypeDefinition() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        restTdefsMockMvc.perform(post("/api/tdefs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TypeDefinitionDTO(
                namespace.getName(),
                tdef.getName(),
                tdef.getVersion(),
                tdef.getSerializedModel()))))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            namespace.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(namespace.getName());
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(namespace.getName(), admin.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);

        restTdefsMockMvc.perform(post("/api/tdefs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TypeDefinitionDTO(
                namespace.getName(),
                tdef.getName(),
                tdef.getVersion(),
                tdef.getSerializedModel()))))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testCreateTypeDefinitionWithExistingNameAndVersionButInDifferentNamespace() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(admin.getLogin(), AuthoritiesConstants.ADMIN));

        // add default tdef to db
        tdef.setNamespace(namespace);
        namespace.addTypeDefinition(tdef);
        tdefsRepository.saveAndFlush(tdef);

        // create a new namespace
        Namespace newNs = new Namespace();
        newNs.setName("braindead");
        newNs.setOwner(admin);
        newNs.addMember(admin);
        namespaceRepository.saveAndFlush(newNs);

        // add admin to newNs members
        admin.addNamespace(newNs);
        userRepository.saveAndFlush(admin);

        // validate db
        Namespace dbNewNs = namespaceRepository.findOne(newNs.getName());
        assertThat(dbNewNs).isNotNull();
        assertThat(namespaceRepository.findAll()).hasSize(2);

        // create a new TypeDefinition with same name and version but using the newNs
        restTdefsMockMvc.perform(post("/api/tdefs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TypeDefinitionDTO(
                newNs.getName(),
                tdef.getName(),
                tdef.getVersion(),
                tdef.getSerializedModel()))))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            newNs.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(newNs.getName());
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(newNs.getName(), admin.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);
    }
}

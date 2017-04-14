package org.kevoree.registry.service.mapper;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.service.dto.NamespaceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity Namespace and its DTO NamespaceDTO
 */
@Mapper(componentModel = "spring")
public abstract class NamespaceMapper {

    @Mapping(source = "owner.login", target = "owner")
    public abstract NamespaceDTO namespaceToNamespaceDTO(Namespace namespace);

    @Mapping(source = "owner", target = "owner.login")
    public abstract Namespace namespaceDTOtoNamespace(NamespaceDTO namespaceDTO);

    public User stringToUser(String owner) {
        User user = new User();
        user.setLogin(owner);
        return user;
    }

    public Set<String> stringsFromMembers(Set<User> members) {
        return members.stream().map(User::getLogin)
            .collect(Collectors.toSet());
    }

    public Set<User> membersFromStrings(Set<String> strings) {
        return strings.stream().map(string -> {
            User member = new User();
            member.setLogin(string);
            return member;
        }).collect(Collectors.toSet());
    }

    public Set<Long> longsFromTypeDefinitions(Set<TypeDefinition> typeDefinitions) {
        return typeDefinitions.stream().map(TypeDefinition::getId)
                .collect(Collectors.toSet());
    }

    public Set<TypeDefinition> typeDefinitionsFromLongs(Set<Long> ids) {
        return ids.stream().map(id -> {
            TypeDefinition tdef = new TypeDefinition();
            tdef.setId(id);
            return tdef;
        }).collect(Collectors.toSet());
    }
}

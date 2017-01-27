package org.kevoree.registry.service.mapper;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.service.dto.NamespaceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity Namespace and its DTO NamespaceDTO
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class NamespaceMapper {

    @Mapping(source = "owner.login", target = "owner")
    public abstract NamespaceDTO namespaceToNamespaceDTO(Namespace namespace);

    public abstract List<NamespaceDTO> namespacesToNamespaceDTOs(List<Namespace> namespaces);

    @Mapping(source = "owner", target = "owner")
    @Mapping(target = "typeDefinitions", ignore = true)
    public abstract Namespace namespaceDTOtoNamespace(NamespaceDTO namespaceDTO);

    public abstract List<Namespace> namespaceDTOsToNamespaces(List<NamespaceDTO> namespaceDTOs);

    public User stringToUser(String owner) {
        User user = new User();
        user.setLogin(owner);
        return user;
    }

    public String userToString(User owner) {
        return owner.getLogin();
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
}

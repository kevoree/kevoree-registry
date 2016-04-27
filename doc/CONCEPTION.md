# Kevoree registry

## Public screens
### Index
Just show a welcome text (is that really necessary ?).
### Namespaces
List the available namespaces, their owner and their members.
### Type Definitions
Show the list of type definitions plus a way to filter them.


## Rights
### Disconnected
A disconnected user can see all the namespace and typedefinition

### A connected user
can see all the namespaces
create new namespaces

### User creation
the username cannot be an existing username nor a an existing namespace

## Workflows
### User creation
Anybody can create an account
When an account is created, a namespace is created with the username. The user is affected as owner and member of the namespace.

### Type definition creation
The user can select any namespace when he is a member.
The name must be in CamelCase.
The version must be a positive integer
The serialized model must be a valid json serialization of a type definition (no validation is done in the current version).

### Typedef/DU update
Nothing published can be updated

### Typedef/DU removal
Nothing publicly available can be deleted.
 * Namespace can be deactivated
 * Could DU be deprecated ?

# Technical rules
creating data domain with services

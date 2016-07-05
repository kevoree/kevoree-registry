'use strict';

angular.module('kevoreeRegistryApp')
    .controller('TypeDefinitionController', function ($rootScope, $scope, $stateParams, TypeDefinitions, Namespaces, User, Principal) {
        $scope.tdefs = [];
        $scope.namespaces = [];
        $scope.isInRole = Principal.isInRole;

        $scope.loadAll = function() {
            TypeDefinitions.query(function(result) {
                // map tdefs so that they also get a fqn for filtering purposes
                $scope.tdefs = result.map(function (tdef) {
                    tdef.fqn = tdef.namespace.name + '.' + tdef.name + '/' + tdef.version;
                    return tdef;
                });
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            User.getNamespaces().then(function (resp) {
                $scope.namespaces = resp.data;
            });
            $('#createTypeDefinitionModal').modal('show');
        };

        $scope.confirmCreate = function () {
            TypeDefinitions.save($scope.tdef,
                function () {
                    $scope.loadAll();
                    $('#createTypeDefinitionModal').modal('hide');
                    $scope.clear();
                }, function (resp) {
                    $scope.createError = resp.data.message;
                });
        };

        $scope.isMember = function (typeDef) {
            return $rootScope.user && typeDef.namespace.members.some(function (member) {
                    return member.login === $rootScope.user.login;
                });
        };

        $scope.delete = function (id, event) {
            event.stopPropagation();
            event.preventDefault();
            $scope.tdef = TypeDefinitions.get({ id: id });
            $('#deleteTypeDefinitionConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            TypeDefinitions.delete({ id: id },
                function () {
                    $scope.loadAll();
                    $('#deleteTypeDefinitionConfirmation').modal('hide');
                    $scope.clear();
                },
                function (resp) {
                    $scope.deleteError = resp.data.message;
                });
        };

        $scope.clear = function () {
            $scope.tdef = null;
            $scope.filterText = null;
        };

        $scope.clearDeleteError = function () {
            $scope.deleteError = null;
        };

        $scope.clearCreateError = function () {
            $scope.createError = null;
        };
    });

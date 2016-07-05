'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitController', function ($rootScope, $scope, $stateParams, DeployUnits, User, Principal) {
        $scope.dus = [];
        $scope.namespaces = [];
        $scope.isInRole = Principal.isInRole;

        $scope.loadAll = function() {
            DeployUnits.query(function(result) {
                $scope.dus = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            User.getNamespaces().then(function (resp) {
                $scope.namespaces = resp.data;
            });
            $('#createModal').modal('show');
        };

        $scope.confirmCreate = function () {
            DeployUnits.save($scope.tdef,
                function () {
                    $scope.loadAll();
                    $('#createModal').modal('hide');
                    $scope.clear();
                }, function (resp) {
                    $scope.createError = resp.data.message;
                });
        };

        $scope.isMember = function (deployUnit) {
            return $rootScope.user && deployUnit.typeDefinition.namespace.members.some(function (member) {
                    return $rootScope.user.login === member.login;
                });
        };

        $scope.delete = function (id, event) {
            event.stopPropagation();
            event.preventDefault();
            $scope.du = DeployUnits.get({ id: id });
            $('#deleteConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            DeployUnits.delete({ id: id },
                function () {
                    $scope.loadAll();
                    $('#deleteConfirmation').modal('hide');
                    $scope.clear();
                },
                function (resp) {
                    $scope.deleteError = resp.data.statusText;
                });
        };

        $scope.clear = function () {
            $scope.du = null;
            $scope.filterText = null;
        };

        $scope.clearDeleteError = function () {
            $scope.deleteError = null;
        };

        $scope.clearCreateError = function () {
            $scope.createError = null;
        };
    });

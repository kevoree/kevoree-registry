'use strict';

angular.module('kevoreeRegistryApp')
    .controller('NamespaceController', function ($rootScope, $scope, Principal, Namespaces) {
        $scope.isInRole = Principal.isInRole;
        $scope.namespaces = [];
        $scope.loadAll = function() {
            Namespaces.query(function(result) {
                $scope.namespaces = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Namespaces.save($scope.namespace,
                function () {
                    $scope.loadAll();
                    $('#saveNamespaceModal').modal('hide');
                    $scope.clear();
                }, function (resp) {
                    $scope.saveError = resp.data.message;
                }
            );
        };

        $scope.update = function (name) {
            $scope.namespace = Namespaces.get({ name: name });
            $('#saveNamespaceModal').modal('show');
        };

        $scope.isOwner = function (namespace) {
            return $rootScope.user.login === namespace.owner.login;
        };

        $scope.delete = function (name, event) {
            event.stopPropagation();
            event.preventDefault();
            $scope.namespace = Namespaces.get({ name: name });
            $('#deleteNamespaceConfirmation').modal('show');
        };

        $scope.confirmDelete = function (name) {
            Namespaces.delete({ name: name },
                function () {
                    $scope.loadAll();
                    $('#deleteNamespaceConfirmation').modal('hide');
                    $scope.clear();
                }, function (resp) {
                    if (resp.status === 401) {
                        $scope.deleteError = resp.statusText;
                    } else {
                        $scope.deleteError = resp.statusText;
                    }
                });
        };

        $scope.clear = function () {
            $scope.namespace = { name: null };
            $scope.filterText = null;
        };

        $scope.clearDeleteError = function () {
            $scope.deleteError = null;
        };

        $scope.clearSaveError = function () {
            $scope.saveError = null;
        };

        angular.element('#saveNamespaceModal').on('shown.bs.modal', function () {
            angular.element('[ng-model="namespace.name"]').focus();
        });
    });

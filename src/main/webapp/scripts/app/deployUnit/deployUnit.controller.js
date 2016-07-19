'use strict';

angular.module('kevoreeRegistryApp')
    .controller('DeployUnitController', function ($rootScope, $scope, $stateParams, TypeDefinitions, DeployUnits, User, Principal) {
        $scope.isInRole = Principal.isInRole;
        $scope.search = {
            name: '',
            version: '',
            typeDefinition: {
                name: '',
                namespace: {
                    name: ''
                }
            }
        };
        $scope.page = {};
        $scope.sizes = [5, 20, 50, 100];
        $scope.selectedSize = $scope.sizes[0];
        $scope.orderColumn = 'name';
        $scope.reverse = false;
        $scope.orderClasses = {
            'glyphicon-chevron-up': $scope.reverse,
            'glyphicon-chevron-down': !$scope.reverse
        };

        $scope.loadAll = function() {
            DeployUnits.get(
                Object.assign({ id: 'page' }, $stateParams),
                function(page) {
                    $scope.page = page;
                    $scope.selectedSize = $scope.sizes[$scope.sizes.indexOf(page.size)];
                });
        };
        $scope.loadAll();

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
            $scope.search = {
                name: '',
                version: '',
                typeDefinition: {
                    name: '',
                    namespace: {
                        name: ''
                    }
                }
            };
        };

        $scope.clearDeleteError = function () {
            $scope.deleteError = null;
        };

        $scope.clearCreateError = function () {
            $scope.createError = null;
        };

        $scope.asArray = function (val) {
            return new Array(val);
        };

        $scope.changeOrderBy = function (prop) {
            if (prop === $scope.orderColumn) {
                $scope.reverse = !$scope.reverse;
                $scope.orderClasses = {
                    'glyphicon-chevron-up': $scope.reverse,
                    'glyphicon-chevron-down': !$scope.reverse
                };
            }
            $scope.orderColumn = prop;
        };
    });

'use strict';

angular.module('kevoreeRegistryApp')
    .controller('SettingsController', function ($scope, Principal, Auth) {
        $scope.account = {};
        $scope.success = null;
        $scope.error = null;

        Principal.identity().then(function(account) {
            $scope.account = account;
        });

        $scope.save = function () {
            Auth.updateAccount($scope.account).then(function() {
                $scope.error = null;
                $scope.success = 'OK';
                Principal.identity().then(function(account) {
                    $scope.account = account;
                });
            }).catch(function() {
                $scope.success = null;
                $scope.error = 'ERROR';
            });
        };
    });

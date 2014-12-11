// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('Namespace', [
        '$scope', 'namespaceFactory', function ($scope, namespaceFactory) {

            namespaceFactory.getNs(retrieveNamespaceFromUrl())
                .success(function (data) {
                    $scope.namespace = data;
                })
                .error(function (res, status) {
                    if (res.error) {
                        $scope.error = res.error;
                    } else {
                        $scope.error = 'Something went wrong (status code '+status+')';
                    }
                });

            $scope.removeMember = function (user) {
                console.log('TODO remove user from namespace', user);
            };

            function retrieveNamespaceFromUrl() {
                var split = window.location.pathname.split("/");
                return split[split.length - 1];
            }
        }]);
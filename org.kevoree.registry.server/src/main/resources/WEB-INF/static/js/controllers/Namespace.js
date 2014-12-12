// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('Namespace', [
        '$scope', 'namespaceFactory', function ($scope, namespaceFactory) {

            function errorHandler(res, status) {
                if (res.error) {
                    $scope.error = res.error;
                } else {
                    $scope.error = 'Something went wrong (status code '+status+')';
                }
            }

            function getNs() {
                namespaceFactory
                    .getNs(retrieveNamespaceFromUrl())
                    .success(function (data) {
                        $scope.namespace = data;
                    }).error(errorHandler);
            }

            getNs();

            $scope.removeMember = function (userId) {
                namespaceFactory
                    .removeMember($scope.namespace.fqn, userId)
                    .success(getNs)
                    .error(errorHandler);
            };

            $scope.addMember = function (userId) {
                namespaceFactory
                    .addMember($scope.namespace.fqn, userId)
                    .success(getNs)
                    .error(errorHandler);
            };

            function retrieveNamespaceFromUrl() {
                var split = window.location.pathname.split("/");
                return split[split.length - 1];
            }
        }]);
'use strict';

angular.module('kevoreeRegistryApp')
    .factory('User', function ($http) {
        return {
            getNamespaces: function () {
                return $http.get('api/user/namespaces');
            },
            getTypeDefinitions: function () {
                return $http.get('api/user/tdefs');
            }
        }
    });

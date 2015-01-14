'use strict';

angular.module('kevoreeRegistryApp')
    .factory('Namespace', function ($resource) {
        return $resource('api/namespaces/:fqn', {}, {
            'query': { method: 'GET', isArray: true },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });

'use strict';

angular.module('kevoreeRegistryApp')
    .factory('TypeDefinition', function ($resource) {
        return $resource('api/tdefs/:name/:version', {}, {
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

'use strict';

angular.module('kevoreeRegistryApp')
    .factory('Namespaces', function Namespaces($resource) {
        return $resource('api/namespaces/:name', {}, {});
    });

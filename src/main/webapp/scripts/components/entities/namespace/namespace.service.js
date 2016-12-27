'use strict';

angular.module('kevoreeRegistryApp')
    .factory('Namespace', function Namespace($resource) {
        return $resource('api/namespaces/:name/members/:member', {}, {});
    });

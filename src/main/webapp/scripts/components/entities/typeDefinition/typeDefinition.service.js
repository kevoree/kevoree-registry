'use strict';

angular.module('kevoreeRegistryApp')
    .factory('TypeDefinition', function ($resource) {
        return $resource('api/tdefs/:namespace/:name/:version', {}, {});
    });

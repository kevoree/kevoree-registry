'use strict';

angular.module('kevoreeRegistryApp')
    .factory('TypeDefinitions', function ($resource) {
        return $resource('api/tdefs/:namespace/:name/:version', {}, {});
    });

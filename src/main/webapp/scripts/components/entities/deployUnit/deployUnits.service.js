'use strict';

angular.module('kevoreeRegistryApp')
    .factory('DeployUnits', function ($resource) {
        return $resource('api/dus/:id', {}, {});
    });

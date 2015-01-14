'use strict';

angular.module('kevoreeRegistryApp')
    .factory('Password', function ($resource) {
        return $resource('api/account/change_password', {}, {
        });
    });

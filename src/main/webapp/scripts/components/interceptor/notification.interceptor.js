 'use strict';

angular.module('kevoreeRegistryApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-kevoreeRegistryApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-kevoreeRegistryApp-params')});
                }
                return response;
            }
        };
    });

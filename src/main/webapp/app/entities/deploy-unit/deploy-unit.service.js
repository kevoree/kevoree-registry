(function() {
    'use strict';
    angular
        .module('kevoreeRegistryApp')
        .factory('DeployUnit', DeployUnit);

    DeployUnit.$inject = ['$resource'];

    function DeployUnit ($resource) {
        var resourceUrl =  'api/deploy-units/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

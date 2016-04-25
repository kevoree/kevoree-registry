(function() {
    'use strict';
    angular
        .module('kevoreeRegistryApp')
        .factory('TypeDefinition', TypeDefinition);

    TypeDefinition.$inject = ['$resource'];

    function TypeDefinition ($resource) {
        var resourceUrl =  'api/type-definitions/:id';

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

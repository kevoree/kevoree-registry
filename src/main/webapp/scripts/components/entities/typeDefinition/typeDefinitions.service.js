'use strict';

angular.module('kevoreeRegistryApp')
    .factory('TypeDefinitions', function ($http) {
        return {
            query: function (params, callback) {
                if (!callback) {
                    callback = params;
                    params = {};
                }
                function process(res) {
                    callback(res.data);
                }
                if (params.namespace) {
                    if (params.name) {
                        if (params.version) {
                            $http.get('api/namespaces/'+params.namespace+'/tdefs/'+params.name+'/'+params.version)
                                .then(process);
                        } else {
                            $http.get('api/namespaces/'+params.namespace+'/tdefs/'+params.name)
                                .then(process);
                        }
                    } else {
                        $http.get('api/namespaces/'+params.namespace+'/tdefs')
                            .then(process);
                    }
                } else {
                    $http.get('api/tdefs')
                        .then(process);
                }
            }
        };
    });

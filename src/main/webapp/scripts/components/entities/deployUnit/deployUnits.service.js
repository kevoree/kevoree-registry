'use strict';

angular.module('kevoreeRegistryApp')
    .factory('DeployUnits', function ($http) {
        return {
            query: function (params, callback) {
                if (!callback) {
                    callback = params;
                    params = {};
                }
                function url(str) {
                    return str.replace('{namespace}', params.namespace)
                        .replace('{tdefName}', params.tdefName)
                        .replace('{tdefVersion}', params.tdefVersion)
                        .replace('{name}', params.name)
                        .replace('{version}', params.version)
                        .replace('{platform}', params.platform);
                }

                function process(res) {
                    callback(res.data);
                }

                if (params.namespace) {
                    if (params.tdefName) {
                        if (params.tdefVersion) {
                            if (params.name) {
                                if (params.version) {
                                    if (params.platform) {
                                        $http.get(url('api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}'))
                                            .then(process);
                                    } else {
                                        $http.get(url('api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}'))
                                            .then(process);
                                    }
                                } else {
                                    $http.get(url('api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}'))
                                        .then(process);
                                }
                            } else {
                                $http.get(url('api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus'))
                                    .then(process);
                            }
                        } else {
                            $http.get(url('api/namespaces/{namespace}/tdefs/{tdefName}/dus'))
                                .then(process);
                        }
                    } else {
                        $http.get(url('api/namespaces/{namespace}/dus'))
                            .then(process);
                    }
                } else {
                    $http.get('api/dus')
                        .then(process);
                }
            }
        };
        // return $resource('api/namespaces/:namespace/tdefs/:tdef/:tdefVersion/dus/:name/:version/:platform', {}, {});
    });

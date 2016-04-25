(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('NamespaceController', NamespaceController);

    NamespaceController.$inject = ['$scope', '$state', 'Namespace'];

    function NamespaceController ($scope, $state, Namespace) {
        var vm = this;
        vm.namespaces = [];
        vm.loadAll = function() {
            Namespace.query(function(result) {
                vm.namespaces = result;
            });
        };

        vm.loadAll();

    }
})();

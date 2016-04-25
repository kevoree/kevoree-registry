(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('NamespaceDeleteController',NamespaceDeleteController);

    NamespaceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Namespace'];

    function NamespaceDeleteController($uibModalInstance, entity, Namespace) {
        var vm = this;
        vm.namespace = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Namespace.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

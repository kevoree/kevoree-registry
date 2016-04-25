(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('TypeDefinitionDeleteController',TypeDefinitionDeleteController);

    TypeDefinitionDeleteController.$inject = ['$uibModalInstance', 'entity', 'TypeDefinition'];

    function TypeDefinitionDeleteController($uibModalInstance, entity, TypeDefinition) {
        var vm = this;
        vm.typeDefinition = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            TypeDefinition.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

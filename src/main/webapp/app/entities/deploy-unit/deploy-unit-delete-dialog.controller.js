(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('DeployUnitDeleteController',DeployUnitDeleteController);

    DeployUnitDeleteController.$inject = ['$uibModalInstance', 'entity', 'DeployUnit'];

    function DeployUnitDeleteController($uibModalInstance, entity, DeployUnit) {
        var vm = this;
        vm.deployUnit = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            DeployUnit.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

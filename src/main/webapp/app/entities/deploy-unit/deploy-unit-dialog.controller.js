(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .controller('DeployUnitDialogController', DeployUnitDialogController);

    DeployUnitDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DeployUnit', 'TypeDefinition'];

    function DeployUnitDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DeployUnit, TypeDefinition) {
        var vm = this;
        vm.deployUnit = entity;
        vm.typedefinitions = TypeDefinition.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('kevoreeRegistryApp:deployUnitUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.deployUnit.id !== null) {
                DeployUnit.update(vm.deployUnit, onSaveSuccess, onSaveError);
            } else {
                DeployUnit.save(vm.deployUnit, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

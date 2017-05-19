'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('TypeDefinitionDetailController', TypeDefinitionDetailController);

TypeDefinitionDetailController.$inject = ['$q', '$state', '$stateParams', 'TypeDefinition'];

function TypeDefinitionDetailController($q, $state, $stateParams, TypeDefinition) {
  var vm = this;
  vm.tdef = null;
  vm.latestDUS = null;
  vm.releaseDUS = null;

  TypeDefinition.getByID($stateParams.id)
		.then(function (tdef) {
  vm.tdef = tdef;

  return $q.all([
    TypeDefinition.getLatestDeployUnits(tdef.namespace, tdef.name, tdef.version),
    TypeDefinition.getReleaseDeployUnits(tdef.namespace, tdef.name, tdef.version)
  ]);
})
		.then(function (results) {
  vm.latestDUS = results[0];
  vm.releaseDUS = results[1];
})
		.catch(function () {
			// TODO better error handling
  $state.go('tdefs');
});
}

angular.module('kevoreeRegistryApp')
	.controller('HomeController', HomeController);

HomeController.$inject = ['$q', 'TypeDefinitions', 'DeployUnits'];

function HomeController($q, TypeDefinitions, DeployUnits) {
	var vm = this;

	$q.all([
		TypeDefinitions.latest({ size: 5, sort: 'lastModifiedDate,asc' }),
		DeployUnits.latest({ size: 5, sort: 'lastModifiedDate,asc' }),
	]).then(function (results) {
		vm.tdefs = results[0].content;
		vm.dus = results[1].content;
	});
}

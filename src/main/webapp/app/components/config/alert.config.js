(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.config(alertConfig);

	alertConfig.$inject = ['AlertServiceProvider'];

	function alertConfig(AlertServiceProvider) {
		AlertServiceProvider.setDefaultTimeout(5000);
	}
})();

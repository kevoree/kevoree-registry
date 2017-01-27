	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.config(function ($localStorageProvider, $sessionStorageProvider) {
			$localStorageProvider.setKeyPrefix('kreg-');
			$sessionStorageProvider.setKeyPrefix('kreg-');
		});

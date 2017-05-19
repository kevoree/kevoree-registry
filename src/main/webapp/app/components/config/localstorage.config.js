(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(localStorageConfig);

  localStorageConfig.$inject = ['$localStorageProvider', '$sessionStorageProvider'];

  function localStorageConfig($localStorageProvider, $sessionStorageProvider) {
    $localStorageProvider.setKeyPrefix('kreg-');
    $sessionStorageProvider.setKeyPrefix('kreg-');
  }
})();

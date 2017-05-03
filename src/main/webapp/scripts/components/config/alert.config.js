'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function (AlertServiceProvider) {
		AlertServiceProvider.setDefaultTimeout(5000);
	});

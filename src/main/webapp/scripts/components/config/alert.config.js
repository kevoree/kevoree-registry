'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function (AlertServiceProvider) {
		// set below to true to make alerts look like toast
		AlertServiceProvider.showAsToast(false);
	});

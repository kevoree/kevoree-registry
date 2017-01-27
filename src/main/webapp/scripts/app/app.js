'use strict';

angular
	.module('kevoreeRegistryApp', [
	'ngStorage',
  'tmh.dynamicLocale',
  'ngResource',
  'ui.router',
	'ui.bootstrap',
  'ngCookies',
  'pascalprecht.translate',
  'ngCacheBuster',
  'hljs'
])
	.run(function (stateHandler, translationHandler) {
		stateHandler.initialize();
		translationHandler.initialize();
	});

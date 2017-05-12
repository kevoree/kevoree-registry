(function () {
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
			'hljs',
			'angularMoment',
		])
		.run(appRun);

	appRun.$inject = ['stateHandler', 'translationHandler'];

	function appRun(stateHandler, translationHandler) {
		stateHandler.initialize();
		translationHandler.initialize();
	}
})();

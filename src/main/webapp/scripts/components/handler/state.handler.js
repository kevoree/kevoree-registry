	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.factory('stateHandler', function ($rootScope, $state, $translate, Language, translationHandler, $window, Auth, Principal) {
			return {
				initialize: initialize
			};

			function initialize() {
				var stateChangeStart = $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams, fromState, fromStateParams) {
					$rootScope.previousState = angular.extend(fromState, { params: fromStateParams });
					$rootScope.nextState = angular.extend(toState, { params: toStateParams });

					// Redirect to a state with an external URL (http://stackoverflow.com/a/30221248/1098564)
					if (toState.external) {
						event.preventDefault();
						$window.open(toState.url, '_self');
					}

					if (Principal.isIdentityResolved()) {
						Auth.authorize();
					}

					// Update the language
					Language.getCurrent().then(function (language) {
						$translate.use(language);
					});
				});

				var stateChangeSuccess = $rootScope.$on('$stateChangeSuccess', function (event, toState/*, toParams, fromState, fromParams*/) {
					var titleKey = 'global.title';

					// Set the page title key to the one configured in state or use default one
					if (toState.data.pageTitle) {
						titleKey = toState.data.pageTitle;
					}
					translationHandler.updateTitle(titleKey);
				});

				$rootScope.$on('$destroy', function () {
					if (angular.isDefined(stateChangeStart) && stateChangeStart !== null) {
						stateChangeStart();
					}
					if (angular.isDefined(stateChangeSuccess) && stateChangeSuccess !== null) {
						stateChangeSuccess();
					}
				});
			}
		});

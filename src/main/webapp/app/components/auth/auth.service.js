(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.factory('Auth', Auth);

	Auth.$inject = ['$rootScope', '$state', '$q', '$sessionStorage', '$translate', 'Principal', 'AuthServerProvider', 'Account', 'Register', 'Activate', 'Password', 'Tracker', 'AlertService'];

	function Auth($rootScope, $state, $q, $sessionStorage, $translate, Principal, AuthServerProvider, Account, Register, Activate, Password, Tracker, AlertService) {
		var service = {
			activateAccount: activateAccount,
			authorize: authorize,
			changePassword: changePassword,
			createAccount: createAccount,
			getPreviousState: getPreviousState,
			login: login,
			logout: logout,
			resetPreviousState: resetPreviousState,
			updateAccount: updateAccount
		};

		return service;

		function activateAccount(key, callback) {
			var cb = callback || angular.noop;

			return Activate.get(key,
				function (response) {
					return cb(response);
				},
				function (err) {
					return cb(err);
				}.bind(this)).$promise;
		}

		function authorize(force) {
			var authReturn = Principal.identity(force).then(authThen);

			return authReturn;

			function authThen() {
				var isAuthenticated = Principal.isAuthenticated();

				// an authenticated user can't access to login and register pages
				if (isAuthenticated && (['login', 'register', 'activate'].indexOf($rootScope.nextState.name) !== -1)) {
					$state.go('home');
				}

				// recover and clear previousState after external login redirect (e.g. oauth2)
				if (isAuthenticated && !$rootScope.previousState.name && getPreviousState()) {
					var previousState = getPreviousState();
					resetPreviousState();
					$state.go(previousState.name, previousState.params);
				}

				if ($rootScope.nextState.data.authorities && $rootScope.nextState.data.authorities.length > 0 && !Principal.hasAnyAuthority($rootScope.nextState.data.authorities)) {
					if (isAuthenticated) {
						// user is signed in but not authorized for desired state
						$state.go('accessdenied');
					} else {
						// user is not authenticated. stow the state they wanted before you
						// send them to the login service, so you can return them when you're done
						$sessionStorage.previousState = $rootScope.nextState;
						// now, send them to the signin state so they can log in
						$state.go('login').then(function () {
							AlertService.warning('You must be logged-in to access <strong>' + $state.href($sessionStorage.previousState.name, $sessionStorage.previousState.params) + '</strong>');
						});
					}
				}
			}
		}

		function changePassword(newPassword, callback) {
			var cb = callback || angular.noop;

			return Password.save(newPassword, function () {
				return cb();
			}, function (err) {
				return cb(err);
			}).$promise;
		}

		function createAccount(account, callback) {
			var cb = callback || angular.noop;

			return Register.save(account,
				function () {
					return cb(account);
				},
				function (err) {
					this.logout();
					return cb(err);
				}.bind(this)).$promise;
		}

		function login(credentials, callback) {
			var cb = callback || angular.noop;
			var deferred = $q.defer();

			AuthServerProvider.login(credentials)
				.then(loginThen)
				.catch(function (err) {
					this.logout();
					deferred.reject(err);
					return cb(err);
				}.bind(this));

			function loginThen(data) {
				Principal.identity(true).then(function (account) {
					// After the login the language will be changed to
					// the language selected by the user during his registration
					if (account !== null) {
						$translate.use(account.langKey).then(function () {
							$translate.refresh();
						});
					}
					Tracker.sendActivity();
					deferred.resolve(data);
				});
				return cb();
			}

			return deferred.promise;
		}


		function logout() {
			Principal.authenticate(null);
			return AuthServerProvider.logout();
		}

		function updateAccount(account, callback) {
			var cb = callback || angular.noop;

			return Account.save(account,
				function () {
					return cb(account);
				},
				function (err) {
					return cb(err);
				}.bind(this)).$promise;
		}

		function getPreviousState() {
			var previousState = $sessionStorage.previousState;
			return previousState;
		}

		function resetPreviousState() {
			delete $sessionStorage.previousState;
		}
	}
})();

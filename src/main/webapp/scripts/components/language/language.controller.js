'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LanguageController', function ($translate, Language) {
		var vm = this;
		vm.changeLanguage = function (languageKey) {
			$translate.use(languageKey);
		};

		Language.getAll().then(function (languages) {
			vm.languages = languages;
		});
	});

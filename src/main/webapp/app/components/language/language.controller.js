'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LanguageController', LanguageController);

LanguageController.$inject = ['$translate', 'Language'];

function LanguageController($translate, Language) {
	var vm = this;
	vm.changeLanguage = function (languageKey) {
		$translate.use(languageKey);
	};

	Language.getAll().then(function (languages) {
		vm.languages = languages;
	});
}

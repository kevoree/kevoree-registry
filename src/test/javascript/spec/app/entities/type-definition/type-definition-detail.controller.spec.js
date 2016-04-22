'use strict';

describe('Controller Tests', function() {

    describe('TypeDefinition Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTypeDefinition, MockNamespace;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTypeDefinition = jasmine.createSpy('MockTypeDefinition');
            MockNamespace = jasmine.createSpy('MockNamespace');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TypeDefinition': MockTypeDefinition,
                'Namespace': MockNamespace
            };
            createController = function() {
                $injector.get('$controller')("TypeDefinitionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'kevoreeRegistryApp:typeDefinitionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

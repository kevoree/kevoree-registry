'use strict';

describe('Controller Tests', function() {

    describe('Namespace Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockNamespace, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockNamespace = jasmine.createSpy('MockNamespace');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Namespace': MockNamespace,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("NamespaceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'kevoreeRegistryApp:namespaceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

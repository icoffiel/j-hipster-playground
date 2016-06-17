'use strict';

describe('Controller Tests', function() {

    describe('RefreshToken Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockRefreshToken, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockRefreshToken = jasmine.createSpy('MockRefreshToken');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'RefreshToken': MockRefreshToken,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("RefreshTokenDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'jHipsterPlaygroundApp:refreshTokenUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

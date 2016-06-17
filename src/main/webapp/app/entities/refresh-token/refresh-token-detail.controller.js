(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .controller('RefreshTokenDetailController', RefreshTokenDetailController);

    RefreshTokenDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'RefreshToken', 'User'];

    function RefreshTokenDetailController($scope, $rootScope, $stateParams, entity, RefreshToken, User) {
        var vm = this;
        vm.refreshToken = entity;
        vm.load = function (id) {
            RefreshToken.get({id: id}, function(result) {
                vm.refreshToken = result;
            });
        };
        var unsubscribe = $rootScope.$on('jHipsterPlaygroundApp:refreshTokenUpdate', function(event, result) {
            vm.refreshToken = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

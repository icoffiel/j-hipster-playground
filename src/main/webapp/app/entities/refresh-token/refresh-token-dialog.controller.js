(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .controller('RefreshTokenDialogController', RefreshTokenDialogController);

    RefreshTokenDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'RefreshToken', 'User'];

    function RefreshTokenDialogController ($scope, $stateParams, $uibModalInstance, entity, RefreshToken, User) {
        var vm = this;
        vm.refreshToken = entity;
        vm.users = User.query();
        vm.load = function(id) {
            RefreshToken.get({id : id}, function(result) {
                vm.refreshToken = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('jHipsterPlaygroundApp:refreshTokenUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.refreshToken.id !== null) {
                RefreshToken.update(vm.refreshToken, onSaveSuccess, onSaveError);
            } else {
                RefreshToken.save(vm.refreshToken, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();

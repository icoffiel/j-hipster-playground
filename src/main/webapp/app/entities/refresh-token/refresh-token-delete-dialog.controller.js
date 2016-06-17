(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .controller('RefreshTokenDeleteController',RefreshTokenDeleteController);

    RefreshTokenDeleteController.$inject = ['$uibModalInstance', 'entity', 'RefreshToken'];

    function RefreshTokenDeleteController($uibModalInstance, entity, RefreshToken) {
        var vm = this;
        vm.refreshToken = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            RefreshToken.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

app.controller('indexController',function ($scope,loginService) {

    $scope.loginName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

});
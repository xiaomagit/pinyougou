app.service('loginService', function ($http) {

    //读取登录用户的用户名
    this.showName = function () {
        return $http.get('../login/name.do');
    }

});
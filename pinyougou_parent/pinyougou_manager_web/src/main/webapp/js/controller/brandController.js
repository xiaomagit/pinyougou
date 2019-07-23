app.controller('brandController', function ($scope, $controller, brandService) {
    /*$scope.findList = function () {
        $http.get("../brand/findAll.do").success(function (data) {
            $scope.brandList = data;
        });
    }*/

    $controller('baseController', {$scope: $scope});

    //分页
    $scope.findPage = function (page, size) {
        brandService.findPage(page, size).success(function (data) {
            $scope.brandList = data.rows;
            $scope.paginationConf.totalItems = data.total;//更新总记录数
        })
    }

    //添加
    $scope.add = function () {
        brandService.add($scope.entity).success(function (response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        })
    }

    //添加，修改
    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }

        object.success(function (response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        })

    }

    //查询详细信息
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    }

    //删除选中
    $scope.del = function () {
        if (confirm("你确定删除吗？")) {
            brandService.del($scope.selectIds).success(function (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectIds = [];
                } else {
                    alert(response.message);
                }
            })
        }
    }

    //定义条件
    $scope.searchEntity = {};

    //条件查询
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(function (response) {
            $scope.brandList = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }

})
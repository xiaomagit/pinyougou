app.controller('itemController', function ($scope) {
    //数量操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }


    $scope.specificationItems = {};  //记录用户选择规格

    //选择规格
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;
        searchSku();//读取sku
    }

    //判断规格是否被选中
    $scope.isSelected = function (name, value) {
        if ($scope.specificationItems[name] == value) {
            return true;
        } else {
            return false;
        }
    }

    //加载默认SKU
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //匹配两个对象
    matchObject = function (map1, map2) {
        for (var key in map1) {
            if (map1[key] != map2[key]) {
                return false;
            }
        }
        for (var key in map2) {
            if (map2[key] != map1[key]) {
                return false;
            }
        }
        return true;
    }

    //查询 SKU
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {id:0,title:'-------', price: 0};
    }

    //添加商品到购物车
    $scope.addToCart = function () {
        alert('skuid:'+$scope.sku.id);
    }

});
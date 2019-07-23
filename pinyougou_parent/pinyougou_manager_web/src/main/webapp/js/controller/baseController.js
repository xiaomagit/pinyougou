app.controller('baseController', function ($scope) {
    //重新加载列表 数据
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.selectIds = [];
    }


    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    }


    //选中id的集合
    $scope.selectIds = [];
    $scope.updateSelection = function ($event, id) {
        //如果checked为true就将id保存在数组中
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            //如果不为true算出id所在的索引，移除
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);
        }
    }

    //获取json字符串中的某一部分
    $scope.jsonToString = function (jsonString,key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }

    //从集合中按照key查询对象
    $scope.searchObjectByKey = function (list, key, keyValue) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key] == keyValue) {
                return list[i];
            }
        }
        return null;
    }

})
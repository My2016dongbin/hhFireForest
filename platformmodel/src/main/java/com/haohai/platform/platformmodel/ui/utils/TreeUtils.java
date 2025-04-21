package com.haohai.platform.platformmodel.ui.utils;

import com.haohai.platform.platformmodel.ui.model.Tree;

import java.util.ArrayList;
import java.util.List;

/**
  * Created by qc
  * on 2023/1/6.
  * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
  */
public class TreeUtils {


    public static List<Tree> parseLevel(List<Tree> treeList){
        List<Tree> list = new ArrayList<>(treeList);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setLevel(list.get(i).getLevel()+1);
        }

        return list;
    }

}

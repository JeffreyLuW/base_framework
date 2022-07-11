package com.meide.demo.service;

import com.github.pagehelper.util.StringUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuHandler {






    /*
     * 组装treeMenu
     * */
    public Object restructureMenuTree(List<MenuVo> vos) {
        ArrayList<MenuVo> resList = new ArrayList<>();
        for(MenuVo vo: vos) {
            if(StringUtil.isEmpty(vo.getPid()))
                resList.add(renderTree(vo, vos));
        }
        // resList.sort((o1, o2) -> o1.getSort()- o2.getSort());
        resList.sort(Comparator.comparingInt(MenuVo::getSort));
        return resList;
    }

    /*
     * 返回只有一个父节点的tree
     * */
    MenuVo renderTree(MenuVo parent, List<MenuVo> vos){
        ArrayList<MenuVo> resList = new ArrayList<>();
        vos.forEach(vo -> {
            if(parent.getId().equals(vo.getPid())){
                resList.add(vo);
                if(ObjectUtils.isEmpty(vo.getChildren()))
                    vo.setChildren(new ArrayList<>());
                vo.getChildren().add(renderTree(vo, vos));
            }
        });
        parent.setChildren(resList);
        return parent;
    }

    @Data
    static class Menu{
        String id;
        String pid;
        String name;
        int sort;
    }

    @Data
    static class MenuVo extends Menu{
        List<MenuVo> children;
    }
    
    
}

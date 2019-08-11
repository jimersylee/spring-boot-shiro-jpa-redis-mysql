package com.example.shiro.controller;

import com.example.shiro.entity.Permission;
import com.example.shiro.repository.PermissionRepository;
import com.example.shiro.vo.PermissionVo;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Resource
    private PermissionRepository permissionRepository;

    @Resource
    private MapperFacade mapperFacade;

    @RequestMapping("/create")
    public void create(String name,String code){
        PermissionVo permissionVo=new PermissionVo();
        permissionVo.setPermName(name);
        permissionVo.setPermCode(code);
        Permission permission = mapperFacade.map(permissionVo, Permission.class);
        permissionRepository.save(permission);
    }
}

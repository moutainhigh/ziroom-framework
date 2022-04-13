package com.ziroom.framework.example.controller;

import com.ziroom.framework.example.service.impl.MerakServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/**
 * @author donghao
 * @version 1.0
 * 2019/7/19.
 */
@RestController
@Slf4j
public class RoleController {
//
//    @Autowired
//    private RoleService roleService;

    @Autowired
    private MerakServiceImpl merakService;

    @Qualifier("merak")
    @Autowired
    DataSource dataSource;

    @Autowired
    BeanFactory beanFactory;

//    @GetMapping("/role")
//    public ResponseEntity<IPage<Role>> roleList() {
//        System.out.println(dataSource);
//        IPage<Role> page = new Page<>(1,2);
//        QueryWrapper<Role> wrapper = new QueryWrapper<>();
//        Role role = new Role();
//        role.setSystem("omega");
//        wrapper.setEntity(role);
//        return ResponseEntity.ok(roleService.page(page,wrapper));
//    }

//    @GetMapping("/merak")
//    public ResponseEntity<Long> merak() {
//        log.info(String.valueOf(dataSource));
//        log.info(String.valueOf(beanFactory.getBean("merakOmega")));
//        return ResponseEntity.ok(merakService.merakData());
//    }
//
    @GetMapping("/merak2")
    public ResponseEntity<Long> merak2() {

        return ResponseEntity.ok(merakService.merakData2());
    }
}

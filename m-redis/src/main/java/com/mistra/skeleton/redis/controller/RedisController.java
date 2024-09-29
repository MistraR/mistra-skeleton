//package com.mistra.skeleton.redis.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.mistra.skeleton.redis.application.ClearAndReloadCache;
//
///**
// * @ author: rui.wang@yamu.com
// * @ description: https://gitee.com/jike11231/redisDemo/blob/master/src/main/java/com/pdh/cache/CacheAspect.java
// * @ date: 2024/7/24
// */
//@RequestMapping("/redis")
//@RestController
//public class RedisController {
//
//    @GetMapping("/get/{id}")
//    @Cache(name = "get method")
//    //@Cacheable(cacheNames = {"get"})
//    public Result get(@PathVariable("id") Integer id){
//        return userService.get(id);
//    }
//
//    @PostMapping("/updateData")
//    @ClearAndReloadCache(name = "get method")
//    public Result updateData(@RequestBody User user){
//        return userService.update(user);
//    }
//}

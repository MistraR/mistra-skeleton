package com.mistra.skeleton.zookeeper;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/7/25
 */
@RestController
public class LockController {

    @Autowired
    private DistributedLock distributedLock;

    @GetMapping("/acquire")
    public String acquireLock() {
        try {
            distributedLock.acquireLock("/my_lock");
            // Perform critical section operations
            return "Lock acquired!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to acquire lock!";
        }
    }

    @GetMapping("/release")
    public String releaseLock() {
        try {
            distributedLock.releaseLock();
            return "Lock released!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to release lock!";
        }
    }

    @GetMapping("/acquire-with-timeout")
    public String acquireLockWithTimeout() {
        try {
            distributedLock.acquireLock("/my_lock", 5, TimeUnit.SECONDS);
            // Perform critical section operations
            return "Lock acquired with timeout!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to acquire lock with timeout!";
        }
    }
}

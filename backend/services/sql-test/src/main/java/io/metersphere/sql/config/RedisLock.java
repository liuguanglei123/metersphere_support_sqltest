package io.metersphere.sql.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
@Slf4j
public class RedisLock {
    private static final String LOCK_PREFIX = "lock:sql_run:";
    private static final int MAX_LOCK_TIME = 1800;
    private static final int EXPIRE_TIME = 600; // 一个SQL执行时间超过600秒则认为是有问题的
    private static final int WAIT_TIME = 2000; // 2秒等待时间

    private final StringRedisTemplate redisTemplate;

    public RedisLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // TODO：超时时间最好也可以自定义配置
    public boolean lock(String reportId, String stepId, int maxStepSize) throws InterruptedException {
        String lockKey = LOCK_PREFIX + reportId;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        while (true) {
            Map<String, String> lockMap = hashOps.entries(lockKey);
            long currentTime = System.currentTimeMillis();

            // 检查是否有超时的 stepId
            for (Map.Entry<String, String> entry : lockMap.entrySet()) {
                long lockTime = Long.parseLong(entry.getValue());
                if ((currentTime - lockTime) / 1000 > MAX_LOCK_TIME) {
                    log.error("Error: StepId " + entry.getKey() + " has been locked for over " + MAX_LOCK_TIME + " seconds!");
                }
            }

            // 如果 map 大小小于 maxStepSize，添加新锁
            if (lockMap.size() < maxStepSize) {
                hashOps.put(lockKey, stepId, String.valueOf(currentTime));
                redisTemplate.expire(lockKey, EXPIRE_TIME, TimeUnit.SECONDS); // 重置过期时间
                return true;
            }

            // 超过最大锁数量，等待后重试
            Thread.sleep(WAIT_TIME);
        }
    }

    public void unlock(String reportId, String stepId) {
        String lockKey = LOCK_PREFIX + reportId;
        redisTemplate.opsForHash().delete(lockKey, stepId);
        redisTemplate.expire(lockKey, EXPIRE_TIME, TimeUnit.SECONDS); // 续期
    }
}

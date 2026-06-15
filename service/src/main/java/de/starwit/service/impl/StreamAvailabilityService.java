package de.starwit.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
public class StreamAvailabilityService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.redis.maxStreamAge:1s}")
    private Duration maxAge;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Set<String> previouslyActiveStreams = new HashSet<>();

    private final Set<String> availableStreams = ConcurrentHashMap.newKeySet();

    /**
     * @return the SAE streams that have received messages recently, as determined by the latest monitoring run.
     */
    public List<String> getAvailableStreams() {
        return availableStreams.stream().toList();
    }

    @Scheduled(fixedRate = 2000)
    public void refreshAvailableStreams() {
        // Get the set of available streams
        Set<String> allKeys = redisTemplate.keys("*");

        Set<String> streamKeys = allKeys.stream()
            .filter(k -> redisTemplate.type(k) == DataType.STREAM)
            .collect(Collectors.toSet());

        // The age calculation can yield negative results, which is fine in this case but smth to keep in mind
        Long serverTime = getServerTime();
        Set<String> recentlyUpdatedStreams = streamKeys.stream()
            .filter(k -> (serverTime - getLastMessageTimestamp(k)) < maxAge.toMillis())
            .collect(Collectors.toSet());

        if (!setsEqual(recentlyUpdatedStreams, previouslyActiveStreams)) {
            this.availableStreams.retainAll(recentlyUpdatedStreams);
            this.availableStreams.addAll(recentlyUpdatedStreams);
            this.previouslyActiveStreams = recentlyUpdatedStreams;
            log.info("Available streams changed: " + Arrays.toString(recentlyUpdatedStreams.toArray()));
        }
    }

    private Long getServerTime() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.serverCommands().time();
            }
        });
    }

    private Long getLastMessageTimestamp(String streamKey) {
        String lastMessageId = redisTemplate.opsForStream().info(streamKey).lastEntryId();
        Long lastMessageTimestamp = Long.parseLong(lastMessageId.split("-")[0]);
        return lastMessageTimestamp;
    }

    private <T> boolean setsEqual(Set<T> set1, Set<T> set2) {
        Set<T> union = new HashSet<>(set1);
        union.addAll(set2);

        return set1.size() == union.size() && set2.size() == union.size();
    }
}

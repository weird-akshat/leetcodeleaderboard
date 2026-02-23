package org.iecse.leetcodeleaderboard.security.service;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitServiceTest {

    @Test
    void resolveBucketShouldReturnSameBucketForSameIp() {
        RateLimitService service = new RateLimitService();

        Bucket first = service.resolveBucket("127.0.0.1");
        Bucket second = service.resolveBucket("127.0.0.1");

        assertThat(first).isSameAs(second);
    }

    @Test
    void resolveBucketShouldReturnDifferentBucketsForDifferentIps() {
        RateLimitService service = new RateLimitService();

        Bucket first = service.resolveBucket("127.0.0.1");
        Bucket second = service.resolveBucket("127.0.0.2");

        assertThat(first).isNotSameAs(second);
    }
}

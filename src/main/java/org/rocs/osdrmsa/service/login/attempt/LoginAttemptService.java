package org.rocs.osdrmsa.service.login.attempt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Tracks failed login attempts per username in an in-memory cache and flags
 * an account for temporary rate-limiting once too many failures pile up.
 *
 * This is deliberately separate from Login.isLocked() (a persistent,
 * OSD-staff-managed lock) - this lockout is self-clearing once the cache
 * window expires and requires no admin action. Same approach/thresholds as
 * the reference implementation: 5 attempts, 15-minute window, 100 tracked
 * usernames at a time.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_WINDOW_MINUTES = 15;
    private static final int MAX_TRACKED_USERNAMES = 100;

    private final LoadingCache<String, Integer> attemptsByUsername;

    public LoginAttemptService() {
        this.attemptsByUsername = CacheBuilder.newBuilder()
                .expireAfterWrite(LOCKOUT_WINDOW_MINUTES, TimeUnit.MINUTES)
                .maximumSize(MAX_TRACKED_USERNAMES)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String username) {
                        return 0;
                    }
                });
    }

    /** Clears the failed-attempt count for a username, e.g. after a successful login. */
    public void evictUserFromLoginAttemptCache(String username) {
        attemptsByUsername.invalidate(username);
    }

    /** Records one more failed attempt for the given username. */
    public void addUserToLoginAttemptCache(String username) {
        try {
            attemptsByUsername.put(username, attemptsByUsername.get(username) + 1);
        } catch (ExecutionException e) {
            attemptsByUsername.put(username, 1);
        }
    }

    /** True once a username has reached MAX_ATTEMPTS failures within the window. */
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return attemptsByUsername.get(username) >= MAX_ATTEMPTS;
        } catch (ExecutionException e) {
            return false;
        }
    }
}

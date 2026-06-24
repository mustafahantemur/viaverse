-- KEYS[1] = bucket key
-- ARGV[1] = window seconds
-- Returns the new counter value after increment.
-- Counts the very first attempt (creates the bucket at value 1 with TTL).
local current = redis.call('INCR', KEYS[1])
if current == 1 then
    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
end
return current

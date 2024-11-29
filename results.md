Data latency (with proxy): ~4ms, based on wireshark logs
Scenario of data loss: 
When turning off the application (manually or in case of some app error), the last batch that is saved in app cache will not be inserted into the database.
Duplicate records: 
There should be no duplicate with the logic of error handling, all the failed records from the batch are returned back to local cache and then added to the next batch. Even if somehow it went through, primary keys are resource_id and timestamp so the database would not allow it.

Aggregation:
Table with resource_id, response_status, cache_status, remote_addr, COUNT() as total requests, SUM(bytes sent), AVG(request_time_mili), updated for example once an hour.
Estimates for clickhouse:
1 row in http_log table is approximately 145B and 1 row in a aggregated table is approximately 60B. Given message rate of 1000 per second, the required space would be 1000 * 60 * 60 * 24 * 205B = 17,71GB/day.
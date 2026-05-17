DROP TABLE IF EXISTS event_similarities CASCADE;

CREATE TABLE IF NOT EXISTS event_similarities (
    event_a_id BIGINT NOT NULL,
    event_b_id BIGINT NOT NULL,
    score DECIMAL(10, 2) NOT NULL,
    timestamp_ms TIMESTAMP NOT NULL,

    PRIMARY KEY (event_a_id, event_b_id)
);

DROP TABLE IF EXISTS user_actions CASCADE;

CREATE TABLE IF NOT EXISTS user_actions (
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    action_type VARCHAR(10) NOT NULL,
    timestamp_ms TIMESTAMP NOT NULL,

    PRIMARY KEY (user_id, event_id)
);
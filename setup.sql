\c web3;
DROP TABLE IF EXISTS session_results CASCADE;

CREATE TABLE session_results (
    id SERIAL,
    jsessionid VARCHAR(255) NOT NULL,
    x NUMERIC(10, 4) NOT NULL,
    y NUMERIC(10, 4) NOT NULL,
    r NUMERIC(10, 4) NOT NULL,
    hit BOOLEAN NOT NULL,
    calculation_time DOUBLE PRECISION NOT NULL,
    released_time TIMESTAMP NOT NULL,
    
    -- Indexes for better performance
    CONSTRAINT session_results_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_session_id ON session_results(jsessionid);

CREATE INDEX idx_released_time ON session_results(released_time DESC);

GRANT ALL PRIVILEGES ON TABLE session_results TO s407868;
GRANT USAGE, SELECT ON SEQUENCE session_results_id_seq TO s407868;

\d session_results;
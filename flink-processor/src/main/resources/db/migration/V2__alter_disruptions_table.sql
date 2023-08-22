ALTER table disruptions
    ADD COLUMN disruption_id varchar NOT NULL,
    ADD COLUMN station_uic_code varchar NOT NULL,
    ADD COLUMN country_code varchar NOT NULL,
    ADD COLUMN direction varchar NOT NULL,

    DROP CONSTRAINT disruptions_pkey,

    ADD CONSTRAINT disruptions_v3_pkey PRIMARY KEY (station_uic_code,disruption_id,start_time, direction),

    ALTER COLUMN station_code DROP NOT NULL,
    ALTER COLUMN end_time DROP NOT NULL;

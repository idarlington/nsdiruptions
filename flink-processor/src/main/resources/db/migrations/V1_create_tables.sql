CREATE TABLE disruptions(
  "station_code" VARCHAR(100) NOT NULL,
  "start_time" VARCHAR(100) NOT NULL,
  "end_time" VARCHAR(100) NOT NULL,
  CONSTRAINT disruptions_pkey PRIMARY KEY (station_code, start_time, end_time)
);

CREATE UNIQUE INDEX disruptions_pkey ON disruptions(station_code text_ops,start_time text_ops,end_time text_ops);
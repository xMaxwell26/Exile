CREATE TABLE IF NOT EXISTS exile_votes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  player_name VARCHAR(12) NOT NULL,
  site VARCHAR(64) NOT NULL,
  external_id VARCHAR(128) NOT NULL,
  payload JSON NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_seen_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_exile_vote (site, external_id),
  KEY ix_exile_votes_player (player_name),
  KEY ix_exile_votes_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

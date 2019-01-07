DROP DATABASE IF EXISTS jship;

CREATE DATABASE jship;

USE jship;

CREATE TABLE users (
	UNo TINYINT(2) UNSIGNED AUTO_INCREMENT PRIMARY KEY, # User ID
	UName VARCHAR(64) UNIQUE,                           # Username
	PassWrd MEDIUMINT                                   # Password Hash
) ENGINE = 'InnoDB';

CREATE TABLE stats (
	UNo TINYINT(2) UNSIGNED REFERENCES users (UNo) ON DELETE CASCADE ON UPDATE CASCADE, # User ID
	Mode ENUM('C', 'S') NOT NULL,                     # Game Mode
	AIDiff ENUM('S', 'R', 'B') NOT NULL,              # AI Difficulty
	GP SMALLINT(3) UNSIGNED NOT NULL DEFAULT 0,       # Games Played
	GW SMALLINT(3) UNSIGNED NOT NULL DEFAULT 0,       # Games Won
	GL SMALLINT(3) UNSIGNED NOT NULL DEFAULT 0,       # Games Lost
	SF MEDIUMINT(6) UNSIGNED NOT NULL DEFAULT 0,      # Shots Fired
	Hits MEDIUMINT(6) UNSIGNED NOT NULL DEFAULT 0,    # Hits landed
	Acc DECIMAL(5, 2) UNSIGNED NOT NULL DEFAULT 0.00, # Accuracy
	TH MEDIUMINT(6) UNSIGNED NOT NULL DEFAULT 0,      # Times Hit
	SS MEDIUMINT(6) UNSIGNED NOT NULL DEFAULT 0,      # Ships Sunk
	SL MEDIUMINT(6) UNSIGNED NOT NULL DEFAULT 0       # Ships Lost
) ENGINE = 'InnoDB';

INSERT INTO users (UName, PassWrd) VALUES ('guest', 0); # Has random statistics.
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'guest'), 'C', 'S', 1, 0, 1, 10, 5, 50.00, 9, 10, 11);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'guest'), 'C', 'R', 1, 0, 1, 11, 6, 54.55, 10, 11, 12);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'guest'), 'C', 'B', 1, 1, 0, 12, 7, 58.33, 11, 12, 13);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'guest'), 'S', 'S', 1, 0, 1, 13, 8, 61.54, 12, 13, 14);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'guest'), 'S', 'R', 1, 0, 1, 14, 9, 64.29, 13, 14, 15);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'guest'), 'S', 'B', 1, 1, 0, 15, 10, 66.67, 14, 15, 16);

INSERT INTO users (UName, PassWrd) VALUES ('admin', 50); # Default password is 'cheats'. Also has random statistics.
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'admin'), 'C', 'S', 2, 1, 1, 21, 15, 55.00, 20, 21, 20);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'admin'), 'C', 'R', 2, 1, 1, 22, 16, 59.45, 21, 22, 31);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'admin'), 'C', 'B', 2, 1, 1, 23, 17, 63.23, 22, 23, 42);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'admin'), 'S', 'S', 2, 1, 1, 24, 18, 66.44, 23, 24, 53);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'admin'), 'S', 'R', 2, 1, 1, 25, 19, 69.19, 24, 25, 64);
INSERT INTO stats VALUES ((SELECT UNo FROM users WHERE UName = 'admin'), 'S', 'B', 2, 1, 1, 26, 20, 71.57, 25, 26, 75);

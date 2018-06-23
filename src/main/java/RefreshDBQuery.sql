DROP TABLE IF EXISTS artifact_associations;
DROP TABLE IF EXISTS artifact_store;
DROP TABLE IF EXISTS group_names;
DROP TABLE IF EXISTS predefined_levels;
DROP TABLE IF EXISTS quest_associations;
DROP TABLE IF EXISTS quest_store;
DROP TABLE IF EXISTS user_artifacts;
DROP TABLE IF EXISTS user_associations;
DROP TABLE IF EXISTS user_privilege_levels;
DROP TABLE IF EXISTS user_experience;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_wallet;
DROP TABLE IF EXISTS users;

CREATE TABLE user_wallet(
  wallet_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER,
  balance INTEGER,
  CONSTRAINT fk_user_id
  FOREIGN KEY (user_id)
  REFERENCES users(user_id)
);

CREATE TABLE users(
  user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  nickname TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  email TEXT
);

CREATE TABLE user_artifacts(
  user_artifact_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER,
  artifact_id INTEGER,
  used TEXT,
  CONSTRAINT fk_user_id
  FOREIGN KEY (user_id)
  REFERENCES users(user_id),
  CONSTRAINT fk_artifact_id
  FOREIGN KEY (artifact_id)
  REFERENCES artifact_store(artifact_id)
);

CREATE TABLE artifact_store(
  artifact_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE,
  descr TEXT,
  price INTEGER
);

CREATE TABLE artifact_associations(
  association_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  artifact_id INTEGER,
  group_id INTEGER,
  CONSTRAINT fk_artifact_id
  FOREIGN KEY (artifact_id)
  REFERENCES artifact_store(artifact_id),
  CONSTRAINT fk_group_id
  FOREIGN KEY (group_id)
  REFERENCES group_names(group_id)
);

CREATE TABLE user_roles(
  role_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER UNIQUE,
  user_privilege_level_id INTEGER,
  CONSTRAINT fk_user_id
  FOREIGN KEY (user_id)
  REFERENCES users(user_id),
  CONSTRAINT fk_user_privilege_level_id
  FOREIGN KEY (user_privilege_level_id)
  REFERENCES user_privilege_levels(privilege_id)
);

CREATE TABLE user_experience(
  experience_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER UNIQUE,
  experience_gained INTEGER,
  CONSTRAINT fk_user_id
  FOREIGN KEY (user_id)
  REFERENCES users(user_id)
);

CREATE TABLE user_associations(
  association_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER,
  group_id INTEGER,
  CONSTRAINT fk_user_id
  FOREIGN KEY (user_id)
  REFERENCES users(user_id),
  CONSTRAINT fk_group_id
  FOREIGN KEY  (group_id)
  REFERENCES group_names(group_id)
);

CREATE TABLE group_names(
  group_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  group_name TEXT UNIQUE
);

CREATE TABLE quest_associations(
  association_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  quest_id INTEGER,
  group_id INTEGER,
  CONSTRAINT fk_quest_id
  FOREIGN KEY (quest_id)
  REFERENCES quest_store(quest_id),
  CONSTRAINT fk_group_id
  FOREIGN KEY (group_id)
  REFERENCES group_names(group_id)
);

CREATE TABLE quest_store(
  quest_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE,
  descr TEXT,
  reward INTEGER
);

CREATE TABLE user_privilege_levels(
  privilege_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  privilege_name TEXT UNIQUE
);

CREATE TABLE predefined_levels(
  threshold INTEGER NOT NULL UNIQUE,
  level_name TEXT UNIQUE,
  PRIMARY KEY(threshold)
);

INSERT INTO artifact_store
(name, descr, price)
VALUES
  ('Sample artifact 1', 'Sample description 1', 1000),
  ('Sample artifact 2', 'Sample description 2', 400),
  ('Sample artifact 3', 'Sample description 3', 10000),
  ('Sample artifact 4', 'Sample description 4', 4000),
  ('Sample artifact 5', 'Sample description 5', 1000);

INSERT INTO artifact_associations
(artifact_id, group_id)
VALUES
  (1, 4),
  (1, 9),
  (2, 4),
  (2, 9),
  (3, 4),
  (3, 9),
  (4, 4),
  (4, 9),
  (5, 4),
  (5, 9);

INSERT INTO group_names
(group_name)
VALUES
  ('quest_basic'),
  ('quest_extra'),
  ('quests'),
  ('artifact_basic'),
  ('codecoolers'),
  ('mentors'),
  ('admins'),
  ('artifact_magic'),
  ('artifacts');



INSERT INTO predefined_levels
VALUES
  (0, 'Level 1'),
  (100, 'Level 2'),
  (200, 'Level 3'),
  (300, 'Level 4'),
  (400, 'Level 5'),
  (500, 'Level 6'),
  (600, 'Level 7');

INSERT INTO quest_associations
(quest_id, group_id)
VALUES
  (1, 1),
  (1, 3),
  (2, 1),
  (2, 3),
  (3, 2),
  (3, 3);

INSERT INTO quest_store
(name, descr, reward)
VALUES
  ('Checkpoint', 'Pass the checkpoint', 500),
  ('SI Project', 'Deliver an SI project', 1000),
  ('TW Project', 'Deliver a TW project', 1500);

INSERT INTO user_artifacts
(user_id, artifact_id, used)
VALUES
  (1, 1, 'false'),
  (2, 2, 'true'),
  (2, 3, 'true'),
  (3, 4, 'true');

INSERT INTO user_associations
(user_id, group_id)
VALUES
  (1, 5),
  (2, 5),
  (3, 5),
  (4, 5),
  (5, 6),
  (6, 6),
  (7, 7);

INSERT INTO user_experience
(user_id, experience_gained)
VALUES
  (1, 0),
  (2, 50),
  (3, 150),
  (4, 250);

INSERT INTO user_privilege_levels
(privilege_name)
VALUES
  ('codecooler'),
  ('mentor'),
  ('admin');

INSERT INTO user_roles
(user_id, user_privilege_level_id)
VALUES
  (1, 1),
  (2, 1),
  (3, 1),
  (4, 1),
  (5, 2),
  (6, 2),
  (7, 3);

INSERT INTO user_wallet
(user_id, balance)
VALUES
  (1, 2500),
  (2, 1238),
  (3, 7249),
  (4, 9050);

INSERT INTO users
(nickname, password, email)
VALUES
  ('Student1', 'password', 'one@email.com'),
  ('Student2', 'password', 'two@email.com'),
  ('Student3', 'password', 'three@email.com'),
  ('Student4', 'password', 'four@email.com'),
  ('Mentor1', 'password', 'five@email.com'),
  ('Mentor2', 'password', 'six@email.com'),
  ('Admin1', 'password', 'seven@email.com');






BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `user_wallet` (
	`wallet_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`user_id`	INTEGER,
	`balance`	INTEGER,
	CONSTRAINT `fk_user_id` FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`)
);
INSERT INTO `user_wallet` VALUES (1,1,2500);
INSERT INTO `user_wallet` VALUES (2,2,1238);
INSERT INTO `user_wallet` VALUES (3,3,7249);
INSERT INTO `user_wallet` VALUES (4,4,9050);
CREATE TABLE IF NOT EXISTS `users` (
	`user_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`nickname`	TEXT NOT NULL UNIQUE,
	`password`	TEXT NOT NULL,
	`email`	TEXT
);
INSERT INTO `users` VALUES (1,'Student1','password','one@email.com');
INSERT INTO `users` VALUES (2,'Student2','password','two@email.com');
INSERT INTO `users` VALUES (3,'Student3','password','three@email.com');
INSERT INTO `users` VALUES (4,'Student4','password','four@email.com');
INSERT INTO `users` VALUES (5,'Mentor1','password','five@email.com');
INSERT INTO `users` VALUES (6,'Mentor2','password','six@email.com');
INSERT INTO `users` VALUES (7,'Admin1','password','seven@email.com');
INSERT INTO `users` VALUES (8,'KrzysztofJarzyna','admin1','krzsztof_jarzyna@zeszczecina.pl');
INSERT INTO `users` VALUES (9,'KrzysztofJarzynaa','admin1','krzsztof_jarzyna@zeszczecina.pl');
CREATE TABLE IF NOT EXISTS `user_artifacts` (
	`user_artifact_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`user_id`	INTEGER,
	`artifact_id`	INTEGER,
	`used`	TEXT,
	CONSTRAINT `fk_artifact_id` FOREIGN KEY(`artifact_id`) REFERENCES `artifact_store`(`artifact_id`),
	CONSTRAINT `fk_user_id` FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`)
);
INSERT INTO `user_artifacts` VALUES (1,1,1,'false');
INSERT INTO `user_artifacts` VALUES (2,2,2,'true');
INSERT INTO `user_artifacts` VALUES (3,2,3,'true');
INSERT INTO `user_artifacts` VALUES (4,3,4,'true');
CREATE TABLE IF NOT EXISTS `artifact_store` (
	`artifact_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`name`	TEXT NOT NULL UNIQUE,
	`descr`	TEXT,
	`price`	INTEGER
);
INSERT INTO `artifact_store` VALUES (1,'Sample artifact 1','Sample description 1',1000);
INSERT INTO `artifact_store` VALUES (2,'Sample artifact 2','Sample description 2',400);
INSERT INTO `artifact_store` VALUES (3,'Sample artifact 3','Sample description 3',10000);
INSERT INTO `artifact_store` VALUES (4,'Sample artifact 4','Sample description 4',4000);
INSERT INTO `artifact_store` VALUES (5,'Sample artifact 5','Sample description 5',1000);
CREATE TABLE IF NOT EXISTS `artifact_associations` (
	`association_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`artifact_id`	INTEGER,
	`group_id`	INTEGER,
	CONSTRAINT `fk_group_id` FOREIGN KEY(`group_id`) REFERENCES `group_names`(`group_id`),
	CONSTRAINT `fk_artifact_id` FOREIGN KEY(`artifact_id`) REFERENCES `artifact_store`(`artifact_id`)
);
INSERT INTO `artifact_associations` VALUES (1,1,4);
INSERT INTO `artifact_associations` VALUES (2,1,9);
INSERT INTO `artifact_associations` VALUES (3,2,4);
INSERT INTO `artifact_associations` VALUES (4,2,9);
INSERT INTO `artifact_associations` VALUES (5,3,4);
INSERT INTO `artifact_associations` VALUES (6,3,9);
INSERT INTO `artifact_associations` VALUES (7,4,4);
INSERT INTO `artifact_associations` VALUES (8,4,9);
INSERT INTO `artifact_associations` VALUES (9,5,4);
INSERT INTO `artifact_associations` VALUES (10,5,9);
CREATE TABLE IF NOT EXISTS `user_roles` (
	`role_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`user_id`	INTEGER UNIQUE,
	`user_privilege_level_id`	INTEGER,
	CONSTRAINT `fk_user_id` FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`),
	CONSTRAINT `fk_user_privilege_level_id` FOREIGN KEY(`user_privilege_level_id`) REFERENCES `user_privilege_levels`(`privilege_id`)
);
INSERT INTO `user_roles` VALUES (1,1,1);
INSERT INTO `user_roles` VALUES (2,2,1);
INSERT INTO `user_roles` VALUES (3,3,1);
INSERT INTO `user_roles` VALUES (4,4,1);
INSERT INTO `user_roles` VALUES (5,5,2);
INSERT INTO `user_roles` VALUES (6,6,2);
INSERT INTO `user_roles` VALUES (7,7,3);
INSERT INTO `user_roles` VALUES (8,8,2);
INSERT INTO `user_roles` VALUES (9,9,2);
CREATE TABLE IF NOT EXISTS `user_experience` (
	`experience_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`user_id`	INTEGER UNIQUE,
	`experience_gained`	INTEGER,
	CONSTRAINT `fk_user_id` FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`)
);
INSERT INTO `user_experience` VALUES (1,1,0);
INSERT INTO `user_experience` VALUES (2,2,50);
INSERT INTO `user_experience` VALUES (3,3,150);
INSERT INTO `user_experience` VALUES (4,4,250);
CREATE TABLE IF NOT EXISTS `user_associations` (
	`association_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`user_id`	INTEGER,
	`group_id`	INTEGER,
	CONSTRAINT `fk_user_id` FOREIGN KEY(`user_id`) REFERENCES `users`(`user_id`),
	CONSTRAINT `fk_group_id` FOREIGN KEY(`group_id`) REFERENCES `group_names`(`group_id`)
);
INSERT INTO `user_associations` VALUES (1,1,5);
INSERT INTO `user_associations` VALUES (2,2,5);
INSERT INTO `user_associations` VALUES (3,3,5);
INSERT INTO `user_associations` VALUES (4,4,5);
INSERT INTO `user_associations` VALUES (5,5,6);
INSERT INTO `user_associations` VALUES (6,6,6);
INSERT INTO `user_associations` VALUES (7,7,7);
INSERT INTO `user_associations` VALUES (8,8,6);
INSERT INTO `user_associations` VALUES (9,9,6);
CREATE TABLE IF NOT EXISTS `group_names` (
	`group_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`group_name`	TEXT UNIQUE
);
INSERT INTO `group_names` VALUES (1,'quest_basic');
INSERT INTO `group_names` VALUES (2,'quest_extra');
INSERT INTO `group_names` VALUES (3,'quests');
INSERT INTO `group_names` VALUES (4,'artifact_basic');
INSERT INTO `group_names` VALUES (5,'codecoolers');
INSERT INTO `group_names` VALUES (6,'mentors');
INSERT INTO `group_names` VALUES (7,'admins');
INSERT INTO `group_names` VALUES (8,'artifact_magic');
INSERT INTO `group_names` VALUES (9,'artifacts');
CREATE TABLE IF NOT EXISTS `quest_associations` (
	`association_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`quest_id`	INTEGER,
	`group_id`	INTEGER,
	CONSTRAINT `fk_quest_id` FOREIGN KEY(`quest_id`) REFERENCES `quest_store`(`quest_id`),
	CONSTRAINT `fk_group_id` FOREIGN KEY(`group_id`) REFERENCES `group_names`(`group_id`)
);
INSERT INTO `quest_associations` VALUES (1,1,1);
INSERT INTO `quest_associations` VALUES (2,1,3);
INSERT INTO `quest_associations` VALUES (3,2,1);
INSERT INTO `quest_associations` VALUES (4,2,3);
INSERT INTO `quest_associations` VALUES (5,3,2);
INSERT INTO `quest_associations` VALUES (6,3,3);
CREATE TABLE IF NOT EXISTS `quest_store` (
	`quest_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`name`	TEXT NOT NULL UNIQUE,
	`descr`	TEXT,
	`reward`	INTEGER
);
INSERT INTO `quest_store` VALUES (1,'Checkpoint','Pass the checkpoint',500);
INSERT INTO `quest_store` VALUES (2,'SI Project','Deliver an SI project',1000);
INSERT INTO `quest_store` VALUES (3,'TW Project','Deliver a TW project',1500);
CREATE TABLE IF NOT EXISTS `user_privilege_levels` (
	`privilege_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`privilege_name`	TEXT UNIQUE
);
INSERT INTO `user_privilege_levels` VALUES (1,'codecooler');
INSERT INTO `user_privilege_levels` VALUES (2,'mentor');
INSERT INTO `user_privilege_levels` VALUES (3,'admin');
CREATE TABLE IF NOT EXISTS `predefined_levels` (
	`threshold`	INTEGER NOT NULL UNIQUE,
	`level_name`	TEXT UNIQUE,
	PRIMARY KEY(`threshold`)
);
INSERT INTO `predefined_levels` VALUES (0,'Level 1');
INSERT INTO `predefined_levels` VALUES (100,'Level 2');
INSERT INTO `predefined_levels` VALUES (200,'Level 3');
INSERT INTO `predefined_levels` VALUES (300,'Level 4');
INSERT INTO `predefined_levels` VALUES (400,'Level 5');
INSERT INTO `predefined_levels` VALUES (500,'Level 6');
INSERT INTO `predefined_levels` VALUES (600,'Level 7');
COMMIT;

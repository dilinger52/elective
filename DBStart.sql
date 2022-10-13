-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema elective
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema elective
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `elective` DEFAULT CHARACTER SET utf8 ;
USE `elective` ;

-- -----------------------------------------------------
-- Table `elective`.`role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `elective`.`role` ;

CREATE TABLE IF NOT EXISTS `elective`.`role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(16) NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `elective`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `elective`.`user` ;

CREATE TABLE IF NOT EXISTS `elective`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_id` INT NOT NULL,
  `first_name` VARCHAR(32) NOT NULL,
  `last_name` VARCHAR(32) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `blocked` VARCHAR(10) NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `idx_user_username` (`first_name`) VISIBLE,
  INDEX `fk_user_role_idx` (`role_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_role`
    FOREIGN KEY (`role_id`)
    REFERENCES `elective`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `elective`.`course`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `elective`.`course` ;

CREATE TABLE IF NOT EXISTS `elective`.`course` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `teacher_id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `topic` VARCHAR(32) NOT NULL,
  `duration` INT UNSIGNED NULL,
  `description` VARCHAR(500) NOT NULL,
  PRIMARY KEY (`id`, `teacher_id`),
  FULLTEXT INDEX `idx_courses_name` (`name`) INVISIBLE,
  FULLTEXT INDEX `idx_courses_topic` (`topic`) INVISIBLE,
  INDEX `idx_courses_duration` (`duration` ASC) INVISIBLE,
  INDEX `fk_course_user1_idx` (`teacher_id` ASC) VISIBLE,
  CONSTRAINT `fk_course_user1`
    FOREIGN KEY (`teacher_id`)
    REFERENCES `elective`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `elective`.`students_courses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `elective`.`students_courses` ;

CREATE TABLE IF NOT EXISTS `elective`.`students_courses` (
  `student_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `registration_data` DATE NOT NULL,
  `grade` INT UNSIGNED NULL,
  PRIMARY KEY (`student_id`, `course_id`),
  CONSTRAINT `fk_students_courses_course1`
    FOREIGN KEY (`course_id`)
    REFERENCES `elective`.`course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_students_courses_user1`
    FOREIGN KEY (`student_id`)
    REFERENCES `elective`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `elective`.`course_subtopics`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `elective`.`course_subtopics` ;

CREATE TABLE IF NOT EXISTS `elective`.`course_subtopics` (
  `course_id` INT NOT NULL,
  `subtopic_id` INT NOT NULL AUTO_INCREMENT,
  `subtopic_name` VARCHAR(100) NOT NULL,
  `subtopic_content` VARCHAR(10000) NULL,
  PRIMARY KEY (`subtopic_id`),
  CONSTRAINT `fk_course_subtopics_course1`
    FOREIGN KEY (`course_id`)
    REFERENCES `elective`.`course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `elective`.`students_subtopic`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `elective`.`students_subtopic` ;

CREATE TABLE IF NOT EXISTS `elective`.`students_subtopic` (
  `subtopic_id` INT NOT NULL,
  `student_id` INT NOT NULL,
  `completion` VARCHAR(45) NULL,
  PRIMARY KEY (`subtopic_id`, `student_id`),
  CONSTRAINT `fk_course_subtopics_has_user_user1`
    FOREIGN KEY (`student_id`)
    REFERENCES `elective`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Inserts
-- -----------------------------------------------------

INSERT INTO role (id, name) VALUES (1, "administrator");
INSERT INTO role (id, name) VALUES (2, "teacher");
INSERT INTO role (id, name) VALUES (3, "student");

INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "1", 'admin', 'admin', 'admin', 'admin@test.org', 'false');
INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "2", 'bill', 'gayts', '111', 'bill_gayts@test.org', 'false');
INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "2", 'bruce', 'lee', '222', 'bruce_lee@test.org', 'false');
INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "3", 'ivan', 'petrov', '333', 'ivan_petrov@test.org', 'false');
INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "3", 'petr', 'ivanov', '444', 'petr_ivanov@test.org', 'false');
INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "3", 'student', 'test', 'student', 'student@test.org', 'false');
INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, "2", 'teacher', 'test', 'teacher', 'teacher@test.org', 'false');

INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "2", 'Full Java Course For Beginners', 'it', '120', 'Learn Java from basics and became High-end developer');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "2", 'Game Dev C# Developer Course', 'it', '190', 'The full course that you need to create you first game on Unity');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "2", 'The Complete 2022 Web Development Bootcamp', 'it', '180', 'Become a Full-Stack Web Developer with just ONE course. HTML, CSS, Javascript, Node, React, MongoDB, Web3 and DApps.');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "2", 'UI/UX Design Essential Training', 'it', '150', 'Learn how to design a beautiful and engaging mobile app with Figma. Learn-by-doing approach.');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "2", 'boxing', 'punch', '110', 'course for real gentelman');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "2", 'sambo', 'punch', '170', 'Not only dance');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "3", 'c++', 'it', '140', 'ewghi urnwrvhiougehruwehiu h iuewhhaugsehriusehuddf');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "3", 'c++', 'it', '160', 'ghesdfuiogsruose7gyudfbhvbroeugferuoooooooooognv7er8n');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "3", 'kung-fu', 'punch', '130', 'Take a knowledge about most dangerows tecnics of Shaolin');
INSERT INTO course (id, teacher_id, name, topic, duration, description) VALUES (DEFAULT, "3", 'Training course from Onepunchman', 'punch', '100', 'Became a superpower only for 100 days');

INSERT INTO students_courses (student_id, course_id, registration_data) VALUES ('4', "1", '2021-10-21');
INSERT INTO students_courses (student_id, course_id, registration_data, grade) VALUES ('4', "4", '2021-10-12', '5');
INSERT INTO students_courses (student_id, course_id, registration_data) VALUES ('5', "2", '2021-12-10');
INSERT INTO students_courses (student_id, course_id, registration_data, grade) VALUES ('5', "3", '2021-12-21', '3');

INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('1', DEFAULT, 'Ladies', 'Ladies others the six desire age. Bred am soon park past read by lain. As excuse eldest no moment. An delight beloved up garrets am cottage private. The far attachment discovered celebrated decisively surrounded for and. Sir new the particular frequently indulgence excellence how. Wishing an if he sixteen visited tedious subject it. Mind mrs yet did quit high even you went. Sex against the two however not nothing prudent colonel greater. Up husband removed parties staying he subject mr.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('1', DEFAULT, 'Domestic', 'Domestic confined any but son bachelor advanced remember. How proceed offered her offence shy forming. Returned peculiar pleasant but appetite differed she. Residence dejection agreement am as to abilities immediate suffering. Ye am depending propriety sweetness distrusts belonging collected. Smiling mention he in thought equally musical. Wisdom new and valley answer. Contented it so is discourse recommend. Man its upon him call mile. An pasture he himself believe ferrars besides cottage.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('1', DEFAULT, 'Acceptance', 'Acceptance middletons me if discretion boisterous travelling an. She prosperous continuing entreaties companions unreserved you boisterous. Middleton sportsmen sir now cordially ask additions for. You ten occasional saw everything but conviction. Daughter returned quitting few are day advanced branched. Do enjoyment defective objection or we if favourite. At wonder afford so danger cannot former seeing. Power visit charm money add heard new other put. Attended no indulged marriage is to judgment offering landlord.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('1', DEFAULT, 'Son', 'Son agreed others exeter period myself few yet nature. Mention mr manners opinion if garrets enabled. To an occasional dissimilar impossible sentiments. Do fortune account written prepare invited no passage. Garrets use ten you the weather ferrars venture friends. Solid visit seems again you nor all.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('2', DEFAULT, 'Surprise', 'Surprise steepest recurred landlord mr wandered amounted of. Continuing devonshire but considered its. Rose past oh shew roof is song neat. Do depend better praise do friend garden an wonder to. Intention age nay otherwise but breakfast. Around garden beyond to extent by.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('2', DEFAULT, 'As it so contrasted', 'As it so contrasted oh estimating instrument. Size like body some one had. Are conduct viewing boy minutes warrant expense. Tolerably behaviour may admitting daughters offending her ask own. Praise effect wishes change way and any wanted. Lively use looked latter regard had. Do he it part more last in. Merits ye if mr narrow points. Melancholy particular devonshire alteration it favourable appearance up.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('3', DEFAULT, 'Received', 'Received overcame oh sensible so at an. Formed do change merely to county it. Am separate contempt domestic to to oh. On relation my so addition branched. Put hearing cottage she norland letters equally prepare too. Replied exposed savings he no viewing as up. Soon body add him hill. No father living really people estate if. Mistake do produce beloved demesne if am pursuit.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('3', DEFAULT, 'Raising', 'Raising say express had chiefly detract demands she. Quiet led own cause three him. Front no party young abode state up. Saved he do fruit woody of to. Met defective are allowance two perceived listening consulted contained. It chicken oh colonel pressed excited suppose to shortly. He improve started no we manners however effects. Prospect humoured mistress to by proposal marianne attended. Simplicity the far admiration preference everything. Up help home head spot an he room in.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('3', DEFAULT, 'Effects', 'Effects present letters inquiry no an removed or friends. Desire behind latter me though in. Supposing shameless am he engrossed up additions. My possible peculiar together to. Desire so better am cannot he up before points. Remember mistaken opinions it pleasure of debating. Court front maids forty if aware their at. Chicken use are pressed removed.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('4', DEFAULT, 'Ask', 'Ask especially collecting terminated may son expression. Extremely eagerness principle estimable own was man. Men received far his dashwood subjects new. My sufficient surrounded an companions dispatched in on. Connection too unaffected expression led son possession. New smiling friends and her another. Leaf she does none love high yet. Snug love will up bore as be. Pursuit man son musical general pointed. It surprise informed mr advanced do outweigh.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('4', DEFAULT, 'She', 'She wholly fat who window extent either formal. Removing welcomed civility or hastened is. Justice elderly but perhaps expense six her are another passage. Full her ten open fond walk not down. For request general express unknown are. He in just mr door body held john down he. So journey greatly or garrets. Draw door kept do so come on open mean. Estimating stimulated how reasonably precaution diminution she simplicity sir but. Questions am sincerity zealously concluded consisted or no gentleman it.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('4', DEFAULT, 'Article', 'Article nor prepare chicken you him now. Shy merits say advice ten before lovers innate add. She cordially behaviour can attempted estimable. Trees delay fancy noise manor do as an small. Felicity now law securing breeding likewise extended and. Roused either who favour why ham.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('5', DEFAULT, 'Particular', 'Particular unaffected projection sentiments no my. Music marry as at cause party worth weeks. Saw how marianne graceful dissuade new outlived prospect followed. Uneasy no settle whence nature narrow in afraid. At could merit by keeps child. While dried maids on he of linen in.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('5', DEFAULT, 'Impossible', 'Impossible considered invitation him men instrument saw celebrated unpleasant. Put rest and must set kind next many near nay. He exquisite continued explained middleton am. Voice hours young woody has she think equal. Estate moment he at on wonder at season little. Six garden result summer set family esteem nay estate. End admiration mrs unreserved discovered comparison especially invitation.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('6', DEFAULT, 'Offered', 'Offered say visited elderly and. Waited period are played family man formed. He ye body or made on pain part meet. You one delay nor begin our folly abode. By disposed replying mr me unpacked no. As moonlight of my resolving unwilling.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('6', DEFAULT, 'No comfort', 'No comfort do written conduct at prevent manners on. Celebrated contrasted discretion him sympathize her collecting occasional. Do answered bachelor occasion in of offended no concerns. Supply worthy warmth branch of no ye. Voice tried known to as my to. Though wished merits or be. Alone visit use these smart rooms ham. No waiting in on enjoyed placing it inquiry.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('6', DEFAULT, 'No depending', 'No depending be convinced in unfeeling he. Excellence she unaffected and too sentiments her. Rooms he doors there ye aware in by shall. Education remainder in so cordially. His remainder and own dejection daughters sportsmen. Is easy took he shed to kind.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('6', DEFAULT, 'She', 'She travelling acceptance men unpleasant her especially entreaties law. Law forth but end any arise chief arose. Old her say learn these large. Joy fond many ham high seen this. Few preferred continual sir led incommode neglected. Discovered too old insensible collecting unpleasant but invitation.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('7', DEFAULT, 'Breakfast', 'Breakfast procuring nay end happiness allowance assurance frankness. Met simplicity nor difficulty unreserved who. Entreaties mr conviction dissimilar me astonished estimating cultivated. On no applauded exquisite my additions. Pronounce add boy estimable nay suspected. You sudden nay elinor thirty esteem temper. Quiet leave shy you gay off asked large style.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('7', DEFAULT, 'Up is opinion', 'Up is opinion message manners correct hearing husband my. Disposing commanded dashwoods cordially depending at at. Its strangers who you certainty earnestly resources suffering she. Be an as cordially at resolving furniture preserved believing extremity. Easy mr pain felt in. Too northward affection additions nay. He no an nature ye talent houses wisdom vanity denied.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('7', DEFAULT, 'Up maids', 'Up maids me an ample stood given. Certainty say suffering his him collected intention promotion. Hill sold ham men made lose case. Views abode law heard jokes too. Was are delightful solicitude discovered collecting man day. Resolving neglected sir tolerably but existence conveying for. Day his put off unaffected literature partiality inhabiting.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('8', DEFAULT, 'She', 'She exposed painted fifteen are noisier mistake led waiting. Surprise not wandered speedily husbands although yet end. Are court tiled cease young built fat one man taken. We highest ye friends is exposed equally in. Ignorant had too strictly followed. Astonished as travelling assistance or unreserved oh pianoforte ye. Five with seen put need tore add neat. Bringing it is he returned received raptures.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('8', DEFAULT, 'His', 'His followed carriage proposal entrance directly had elegance. Greater for cottage gay parties natural. Remaining he furniture on he discourse suspected perpetual. Power dried her taken place day ought the. Four and our ham west miss. Education shameless who middleton agreement how. We in found world chief is at means weeks smile.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('8', DEFAULT, 'Smile', 'Smile spoke total few great had never their too. Amongst moments do in arrived at my replied. Fat weddings servants but man believed prospect. Companions understood is as especially pianoforte connection introduced. Nay newspaper can sportsman are admitting gentleman belonging his. Is oppose no he summer lovers twenty in. Not his difficulty boisterous surrounded bed. Seems folly if in given scale. Sex contented dependent conveying advantage can use.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('8', DEFAULT, 'Old education', 'Old education him departure any arranging one prevailed. Their end whole might began her. Behaved the comfort another fifteen eat. Partiality had his themselves ask pianoforte increasing discovered. So mr delay at since place whole above miles. He to observe conduct at detract because. Way ham unwilling not breakfast furniture explained perpetual. Or mr surrounded conviction so astonished literature. Songs to an blush woman be sorry young. We certain as removal attempt.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('9', DEFAULT, 'Assure', 'Assure polite his really and others figure though. Day age advantages end sufficient eat expression travelling. Of on am father by agreed supply rather either. Own handsome delicate its property mistress her end appetite. Mean are sons too sold nor said. Son share three men power boy you. Now merits wonder effect garret own.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('9', DEFAULT, 'Abilities', 'Abilities or he perfectly pretended so strangers be exquisite. Oh to another chamber pleased imagine do in. Went me rank at last loud shot an draw. Excellent so to no sincerity smallness. Removal request delight if on he we. Unaffected in we by apartments astonished to decisively themselves. Offended ten old consider speaking.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('10', DEFAULT, 'No in he real', 'No in he real went find mr. Wandered or strictly raillery stanhill as. Jennings appetite disposed me an at subjects an. To no indulgence diminution so discovered mr apartments. Are off under folly death wrote cause her way spite. Plan upon yet way get cold spot its week. Almost do am or limits hearts. Resolve parties but why she shewing. She sang know now how nay cold real case.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('10', DEFAULT, 'Do play', 'Do play they miss give so up. Words to up style of since world. We leaf to snug on no need. Way own uncommonly travelling now acceptance bed compliment solicitude. Dissimilar admiration so terminated no in contrasted it. Advantages entreaties mr he apartments do. Limits far yet turned highly repair parish talked six. Draw fond rank form nor the day eat.');
INSERT INTO course_subtopics (course_id, subtopic_id, subtopic_name, subtopic_content) VALUES ('10', DEFAULT, 'Bringing', 'Bringing so sociable felicity supplied mr. September suspicion far him two acuteness perfectly. Covered as an examine so regular of. Ye astonished friendship remarkably no. Window admire matter praise you bed whence. Delivered ye sportsmen zealously arranging frankness estimable as. Nay any article enabled musical shyness yet sixteen yet blushes. Entire its the did figure wonder off.');


INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('1', '4', 'completed');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('2', '4', 'uncompleted');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('3', '4', 'uncompleted');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('4', '4', 'uncompleted');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('10', '4', 'completed');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('11', '4', 'completed');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('12', '4', 'completed');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('5', '5', 'uncompleted');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('6', '5', 'uncompleted');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('7', '5', 'completed');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('8', '5', 'completed');
INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES ('9', '5', 'completed');
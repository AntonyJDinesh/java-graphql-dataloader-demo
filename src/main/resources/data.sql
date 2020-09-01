DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  age INT NOT NULL,
  gender VARCHAR(10) NOT NULL,
  yoj INT NOT NULL,
  dept_id INT NOT NULL
);

INSERT INTO `employee` (name, age, gender, yoj, dept_id) VALUES
('Senkodi', 28, 'F', 2019, 1),
('Muguran', 29, 'M', 2020, 2),
('Muthukumar', 24, 'M', 2019, 1),
('Nalini', 23, 'F', 2020, 2);


DROP TABLE IF EXISTS `dept`;

CREATE TABLE `dept` (
id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL
);

INSERT INTO `dept` (name) VALUES
('MO'),
('FO');
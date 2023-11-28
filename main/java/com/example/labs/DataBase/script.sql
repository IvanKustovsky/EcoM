create schema ekoMon;
use ekoMon;
SET SQL_SAFE_UPDATES = 0;

create table if not exists object(
id INT NOT NULL PRIMARY KEY,
nameObject VARCHAR(45) NOT NULL,
location VARCHAR(255) NOT NULL,
description VARCHAR(255) NOT NULL
);

create table if not exists pollutant(
code INT NOT NULL PRIMARY KEY,
name VARCHAR(45) NOT NULL,
gdk DOUBLE NOT NULL,
mass_consumption DOUBLE NOT NULL,
rfc DOUBLE NOT NULL, -- референтна концентрація
sf DOUBLE NOT NULL, -- фактор нахилу
tax_rate DOUBLE DEFAULT 0, -- ставка податку
danger_class INT NOT NULL  -- клас небезпеки
);

create table if not exists pollution(
id_pollution INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
id_object INT NOT NULL,
code_pollutant INT NOT NULL,
value_pollution DOUBLE NOT NULL,
concentration DOUBLE  NOT NULL,
hq DOUBLE NOT NULL, -- неканцерогенний ризик
cr DOUBLE  NOT NULL, -- канцерогенний ризик
compensation DOUBLE NOT NULL, -- відшкодування
year INT NOT NULL,
FOREIGN KEY(id_object) REFERENCES object(id) ON DELETE CASCADE,
FOREIGN KEY(code_pollutant) REFERENCES pollutant(code) ON DELETE CASCADE);

create table if not exists tax(
pollution_id INT NOT NULL,
rate DOUBLE NOT NULL, -- ставка податку
sum DOUBLE NOT NULL,  -- сума податку
FOREIGN KEY (pollution_id) REFERENCES pollution(id_pollution) ON DELETE CASCADE);

select * from object;
select * from pollution;
select * from pollutant;
select * from tax;

drop database if exists ekoMon;









-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema transfile
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema transfile
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `transfile` DEFAULT CHARACTER SET utf8 ;
USE `transfile` ;

-- -----------------------------------------------------
-- Table `transfile`.`files`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `transfile`.`files` (
  `path` VARCHAR(255) NOT NULL,
  `insctr` DATE NOT NULL,
  `dwnctr` INT(2) NOT NULL,
  `skey` VARCHAR(255) NOT NULL,
  `link` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`link`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `transfile`.`packet`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `transfile`.`packet` (
  `pLocation` VARCHAR(255) NOT NULL,
  `pDate` DATE NOT NULL,
  `pLink` VARCHAR(40) NOT NULL,
  `pDownTimes` INT(2) NOT NULL,
  `pPassKey` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`pLink`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `transfile`.`statinfo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `transfile`.`statinfo` (
  `sCounter` INT(11) NOT NULL,
  `sName` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`sCounter`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `transfile`.`tblads`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `transfile`.`tblads` (
  `adId` INT(3) NOT NULL AUTO_INCREMENT,
  `adLocation` VARCHAR(255) NOT NULL,
  `adDate` DATE NOT NULL,
  `adLink` VARCHAR(255) NOT NULL,
  `adDescription` VARCHAR(255) NOT NULL,
  `adType` ENUM('sidebar', 'background') NOT NULL,
  PRIMARY KEY (`adId`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

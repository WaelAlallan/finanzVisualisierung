
-- DROP TABLE IF EXISTS `finanzen`;
-- DROP TABLE IF EXISTS `unternehmen`;

CREATE TABLE IF NOT EXISTS `unternehmen`  (
     `unternehmen_id` BIGINT AUTO_INCREMENT NOT NULL,
     `unternehmen` VARCHAR(50),
     `gründung` INTEGER,
     `sitz` VARCHAR(50),
     `mitarbeiterzahl` INTEGER,
     `fabrikanzahl` INTEGER,
     `produktionskapazität` INTEGER,
     `logistikpartner` VARCHAR(50),
     `frauenanteil_in_Führung` INTEGER,
     `co2_Bilanz` INTEGER,
     PRIMARY KEY (`unternehmen_id`),
     UNIQUE (unternehmen)
);

-- -------------------------------------

CREATE TABLE IF NOT EXISTS `finanzen`  (
     `unternehmen_id` BIGINT NOT NULL,
     -- `unternehmen` VARCHAR(50),
     `monat` VARCHAR(50) NOT NULL,
     `produktion_Budget` INTEGER,
     `produktion_Kosten` INTEGER,
     `personal_Budget` INTEGER,
     `personal_Kosten` INTEGER,
     `IT_Budget` INTEGER,
     `IT_Kosten` INTEGER,
     `marketing_Budget` INTEGER,
     `marketing_Kosten` INTEGER,
     `logistik_Budget` INTEGER,
     `logistik_Kosten` INTEGER,
     `sonstiges_Budget` INTEGER,
     `sonstiges_Kosten` INTEGER,
     `budget_Gesamt` INTEGER,
     `ausgaben_Gesamt` INTEGER,
     `umsatz` INTEGER,
     `gewinn` INTEGER,
     PRIMARY KEY (`unternehmen_id`, `monat`),
     CONSTRAINT `fk_unternehmen` FOREIGN KEY (`unternehmen_id`)
         REFERENCES `unternehmen`(`unternehmen_id`)
         ON DELETE CASCADE
         ON UPDATE CASCADE
);




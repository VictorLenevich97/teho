DELETE FROM workhours_distribution_interval;
DELETE FROM equipment_labor_input_per_type;
DELETE FROM restoration_type;
DELETE FROM repair_type;
DELETE FROM equipment_per_base;
DELETE FROM equipment;
DELETE FROM equipment_sub_type;
DELETE FROM equipment_type;
DELETE FROM repair_division;
DELETE FROM repair_station_type;
DELETE FROM base;

INSERT INTO base VALUES (1, 'ГрА', 'ГрА');
INSERT INTO base VALUES (2, 'омб', 'омб');

INSERT INTO repair_division_unit_type VALUES (1, 'вто', 3, 5);
INSERT INTO repair_division_unit_type VALUES (2, 'ремр', 8, 10);
INSERT INTO repair_division_unit_type VALUES (3, 'орвб', 8, 10);
INSERT INTO repair_division_unit_type VALUES (4, 'рвп', 10, 12);

INSERT INTO repair_station_type VALUES (1, 'МТО-80');
INSERT INTO repair_station_type VALUES (2, 'МТО-АТ-М1');

INSERT INTO repair_division VALUES (1, '1 омб');
INSERT INTO repair_division VALUES (2, '2 омб');

INSERT INTO repair_division_unit VALUES (1, 'грр вто 1 омб', 1, 1,1, 1);
INSERT INTO repair_division_unit VALUES (2, 'ото вто 1 омб', 1, 1,1, 1);
INSERT INTO repair_division_unit VALUES (3, 'ото ремр 2 омб', 2, 2,2, 2);

INSERT INTO equipment_type VALUES (1, 'РАВ', 'РАВ');
INSERT INTO equipment_type VALUES (2, 'БТВТ', 'БТВТ');
INSERT INTO equipment_type VALUES (3, 'АТ', 'ГМ');
INSERT INTO equipment_sub_type VALUES (1, 'РА', 'РА', 1);
INSERT INTO equipment_sub_type VALUES (2, 'НА', 'НА', 1);
INSERT INTO equipment_sub_type VALUES (3, 'ЗА, ЗРК', 'ЗА, ЗРК', 1);
INSERT INTO equipment_sub_type VALUES (4, 'ПТРК', 'ПТРК', 1);
INSERT INTO equipment_sub_type VALUES (5, 'Средства разведки', 'Средства разведки', 1);
INSERT INTO equipment_sub_type VALUES (6, 'Средства управления', 'Средства управления', 1);
INSERT INTO equipment_sub_type VALUES (7, 'СО', 'СО', 1);
INSERT INTO equipment_sub_type VALUES (8, 'ОП', 'ОП', 1);
INSERT INTO equipment_sub_type VALUES (9, 'ЭОП', 'ЭОП', 1);
INSERT INTO equipment_sub_type VALUES (10, 'Танки', 'Танки', 2);
INSERT INTO equipment_sub_type VALUES (11, 'БМП', 'БМП', 2);
INSERT INTO equipment_sub_type VALUES (12, 'БТР, БРДМ', 'БТР, БРДМ', 2);
INSERT INTO equipment_sub_type VALUES (13, 'КМ', 'КМ', 3);
INSERT INTO equipment_sub_type VALUES (14, 'ГМ', 'ГМ', 3);
INSERT INTO equipment_sub_type VALUES (15, 'ТЫЛ', 'ТЫЛ', null);

INSERT INTO equipment VALUES (1, 'equipment1', 1);
INSERT INTO equipment VALUES (2, '125 мм', 2);
INSERT INTO equipment VALUES (3, 'equipment3', 1);
INSERT INTO equipment VALUES (4, 'equipment4', 3);

INSERT INTO equipment_per_base values (1, 1, 3);
INSERT INTO equipment_per_base values (2, 2, 14);
INSERT INTO equipment_per_base values (1, 3, 7);
INSERT INTO equipment_per_base values (2, 4, 22);
--
-- INSERT INTO repair_station_equipment_staff VALUES (1, 1, 5, 10);
-- INSERT INTO repair_station_equipment_staff VALUES (2, 1, 3, 5);
-- INSERT INTO repair_station_equipment_staff VALUES (3, 1, 12, 15);
-- INSERT INTO repair_station_equipment_staff VALUES (1, 2, 7, 11);
-- INSERT INTO repair_station_equipment_staff VALUES (2, 2, 8, 12);
-- INSERT INTO repair_station_equipment_staff VALUES (3, 2, 9, 13);

INSERT INTO repair_type VALUES (1, true, 'Текущий', 'ТР');
INSERT INTO repair_type VALUES (2, true, 'Средний', 'СР');
INSERT INTO repair_type VALUES (3, false, 'Капитальный', 'КР');
INSERT INTO repair_type VALUES (4, false, 'Безвозвратные потери', 'БП');

INSERT INTO restoration_type VALUES (1, 'Тактический');
INSERT INTO restoration_type VALUES (2, 'Оперативный');
INSERT INTO restoration_type VALUES (3, 'Стратегический');

INSERT INTO equipment_labor_input_per_type VALUES (1, 1, 140);
INSERT INTO equipment_labor_input_per_type VALUES (2, 1, 220);
INSERT INTO equipment_labor_input_per_type VALUES (3, 1, 123);
INSERT INTO equipment_labor_input_per_type VALUES (4, 1, 412);
INSERT INTO equipment_labor_input_per_type VALUES (1, 2, 240);
INSERT INTO equipment_labor_input_per_type VALUES (2, 2, 320);
INSERT INTO equipment_labor_input_per_type VALUES (3, 2, 350);
INSERT INTO equipment_labor_input_per_type VALUES (4, 2, 420);

INSERT INTO workhours_distribution_interval VALUES (1, null, 10, 1);
INSERT INTO workhours_distribution_interval VALUES (2, 10, 20, 1);
INSERT INTO workhours_distribution_interval VALUES (3, 20, 50, 2);
INSERT INTO workhours_distribution_interval VALUES (4, 50, 100, 2);
INSERT INTO workhours_distribution_interval VALUES (5, 100, 200, 2);
INSERT INTO workhours_distribution_interval VALUES (6, 200, 400, 3);

INSERT INTO stage VALUES (1,1);
INSERT INTO stage VALUES (2,2);
INSERT INTO stage VALUES (3,3);
INSERT INTO stage VALUES (4,4);

alter sequence base_id_seq restart with 3;
alter sequence repair_station_type_id_seq restart with 3;
alter sequence repair_division_unit_id_seq restart with 4;
alter sequence equipment_type_id_seq restart with 4;
alter sequence equipment_sub_type_id_seq restart with 16;
alter sequence equipment_id_seq restart with 5;
alter sequence repair_type_id_seq restart with 5;
alter sequence restoration_type_id_seq restart with 4;
alter sequence workhours_distribution_interval_id_seq restart with 7;
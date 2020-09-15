INSERT INTO base VALUES (1, 'б 1', 'б1');
INSERT INTO base VALUES (2, 'б 2', 'б2');

INSERT INTO repair_station_type VALUES (1, 'вто', 5, 3);
INSERT INTO repair_station_type VALUES (2, 'ремр', 10, 8);

INSERT INTO repair_station VALUES (1, 'вто 1', 1, 1, 1);
INSERT INTO repair_station VALUES (2, 'ремр 1', 1, 1, 2);

INSERT INTO equipment_type VALUES (1, 'РАВ', 'РАВ');
INSERT INTO equipment_type VALUES (2, 'test', 'test');
INSERT INTO equipment_sub_type VALUES (1, 'РА', 'РА', 1);
INSERT INTO equipment_sub_type VALUES (2, 'НА', 'НА', 1);
INSERT INTO equipment_sub_type VALUES (3, 'tester', 'tester', 2);

INSERT INTO equipment VALUES (1, 'equipment1', 1);
INSERT INTO equipment VALUES (2, 'equipment2', 2);
INSERT INTO equipment VALUES (3, 'equipment3', 1);
INSERT INTO equipment VALUES (4, 'equipment4', 3);

INSERT INTO equipment_per_base values (1, 1, 20, 3);
INSERT INTO equipment_per_base values (2, 2, 3, 14);
INSERT INTO equipment_per_base values (1, 3, 9, 7);
INSERT INTO equipment_per_base values (2, 4, 12, 22);

INSERT INTO repair_station_equipment_staff VALUES (1, 1, 5, 10);
INSERT INTO repair_station_equipment_staff VALUES (2, 1, 3, 5);

INSERT INTO repair_type VALUES (1, 'Текущий', true);
INSERT INTO repair_type VALUES (2, 'Средний', true);
INSERT INTO repair_type VALUES (3, 'Капитальный', true);
INSERT INTO repair_type VALUES (4, 'Безвозвратные потери', false);

INSERT INTO restoration_type VALUES (1, 'Тактический');
INSERT INTO restoration_type VALUES (2, 'Оперативный');
INSERT INTO restoration_type VALUES (3, 'Стратегический');

INSERT INTO equipment_labor_input_per_type VALUES (1, 1, 140);
INSERT INTO equipment_labor_input_per_type VALUES (2, 1, 220);
INSERT INTO equipment_labor_input_per_type VALUES (3, 1, 123);
INSERT INTO equipment_labor_input_per_type VALUES (4, 1, 412);
INSERT INTO equipment_labor_input_per_type VALUES (1, 2, 240);
INSERT INTO equipment_labor_input_per_type VALUES (2, 2, 320);

INSERT INTO workhours_distribution_interval VALUES (1, 0, 10, 1);
INSERT INTO workhours_distribution_interval VALUES (2, 10, 20, 1);
INSERT INTO workhours_distribution_interval VALUES (3, 20, 50, 2);
INSERT INTO workhours_distribution_interval VALUES (4, 50, 100, 2);
INSERT INTO workhours_distribution_interval VALUES (5, 100, 200, 2);
INSERT INTO workhours_distribution_interval VALUES (6, 200, 400, 3);
INSERT INTO workhours_distribution_interval VALUES (7, 400, 1000, 3);

alter sequence base_id_seq restart with 3;
alter sequence repair_station_type_id_seq restart with 3;
alter sequence repair_station_id_seq restart with 3;
alter sequence equipment_type_id_seq restart with 3;
alter sequence equipment_sub_type_id_seq restart with 4;
alter sequence equipment_id_seq restart with 5;
alter sequence repair_type_id_seq restart with 5;
alter sequence restoration_type_id_seq restart with 4;
alter sequence workhours_distribution_interval_id_seq restart with 8;
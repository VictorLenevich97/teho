insert into teho.equipment_type values (1, 'Танк', 'Танк');
insert into teho.equipment_type values (2, 'БМП', 'Боевая машина пехоты');
insert into teho.equipment_type values (3, 'БТР', 'Бронетранспортёр');
insert into teho.equipment_type values (4, 'КМ', 'Колёсная машина');
insert into teho.equipment_type values (5, 'ГМ', 'Гусеничная машина');

insert into teho.repair_type values (1, 'Текущий');
insert into teho.repair_type values (2, 'Средний');
insert into teho.repair_type values (3, 'Капитальный');

insert into teho.repair_station_type values (1, 'МТО-80', 1, 20);
insert into teho.repair_station_type values (2, 'МТО-АТ-М1', 1, 20);

insert into teho.workhours_distribution_intervals values (1, 0, 10);
insert into teho.workhours_distribution_intervals values (2, 10, 20);
insert into teho.workhours_distribution_intervals values (3, 20, 50);
insert into teho.workhours_distribution_intervals values (4, 50, 100);
insert into teho.workhours_distribution_intervals values (5, 100, 200);
insert into teho.workhours_distribution_intervals values (6, 200, 400);


insert into teho.base values (1, 'test short name', 'test full name');

insert into teho.repair_station values (1, 'грр вто 1 омб', 1, 1, 0);
insert into teho.repair_station values (2, 'ото вто 1 омб', 2, 1, 0);
insert into teho.repair_station values (3, 'рэо вто 2 омб', 1, 1, 0);
insert into teho.repair_station values (4, 'ото вто 2 омб', 2, 1, 0);
insert into teho.repair_station values (5, 'рэо вто 3 омб', 1, 1, 0);
insert into teho.repair_station values (6, 'ото вто 3 омб', 2, 1, 0);
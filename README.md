# teho-backend
[![Quality gate](https://sonarqube.rit/api/project_badges/quality_gate?project=by.varb.teho%3Ateho)](https://sonarqube.rit/dashboard?id=by.varb.teho%3Ateho)

Поддержка принятия решений по техническому обеспечению

В данном репозитории находится backend-часть проекта.

Проект в Redmine - https://redmine.rit/redmine/projects/teho

Ипользуемая база данных - Postgres
Список команд для установки Postgres 12 в ОС Centos7:

```
yum install https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm

yum install postgresql12-server

/usr/pgsql-12/bin/postgresql-12-setup initdb

systemctl enable postgresql-12

systemctl start postgresql-12
```

Схема БД находится в разделе "Файлы" в Redmine - https://redmine.rit/redmine/projects/teho/files

В проекте используется Java 8.
 

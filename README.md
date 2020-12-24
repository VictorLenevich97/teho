# teho-backend
[![Quality gate](https://sonarqube.rit/api/project_badges/quality_gate?project=va.rit.teho%3Ateho)](https://sonarqube.rit/dashboard?id=va.rit.teho%3Ateho)

Поддержка принятия решений по техническому обеспечению

В данном репозитории находится backend-часть проекта.

Проект в Redmine - https://redmine.rit/redmine/projects/teho

Ипользуемая база данных - Postgres

Список команд для установки Postgres 9.2 в ОС Centos7:

```
sudo yum install postgresql-server

sudo su - postgres

postgresql-setup initdb

exit

sudo systemctl enable postgresql.service

sudo systemctl start postgresql.service

sudo su - postgres

psql

\password postgres

...

\q

exit

sudo vim /var/lib/pgsql/data/pg_hba.conf (изменить "ident" на "md5" (на средней строчке))

sudo systemctl restart postgresql.service
```

В проекте используется Java 8.
 

FROM mariadb:latest
USER root
COPY ./platform.sql /docker-entrypoint-initdb.d
RUN chown -R mysql:mysql /docker-entrypoint-initdb.d/

FROM postgres:9.6.20-alpine

RUN apk add --no-cache openjdk8-jre

COPY target/teho-1.0-RELEASE.jar /teho-1.0-RELEASE.jar
COPY startup.sh /startup.sh

ENTRYPOINT ["/bin/sh"]

CMD ["startup.sh"]

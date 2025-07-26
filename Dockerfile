FROM openjdk:8

RUN rm -rf /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

VOLUME /etc/zhishuyun/conf
VOLUME /var/lib/zhishuyun

ARG ADMIN_PASSWORD='admin123'
ARG ACTIVE_ENV='docker'
ARG LOG_LEVEL='info'

COPY ./pytorch-yun-backend/pytorch-yun-main/build/libs/zhishuyun.jar /opt/zhishuyun/zhishuyun.jar
COPY ./pytorch-yun-backend/pytorch-yun-main/src/main/resources/application-docker.yml /etc/zhishuyun/conf/

EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}
ENV ACTIVE_ENV=${ACTIVE_ENV}
ENV LOG_LEVEL=${LOG_LEVEL}
ENV PARAMS=""
ENV JVMOPTIONS=""

ENTRYPOINT ["sh","-c","java $JVMOPTIONS -jar /opt/zhishuyun/zhishuyun.jar --logging.level.root=${LOG_LEVEL} --spring.profiles.active=${ACTIVE_ENV} --isx-app.admin-passwd=${ADMIN_PASSWORD} --spring.config.additional-location=/etc/zhishuyun/conf/ $PARAMS"]
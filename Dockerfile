FROM azul/zulu-openjdk:8u412-8.78

RUN rm -rf /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

VOLUME /etc/zhishuyun/conf
VOLUME /var/lib/zhishuyun

ARG ADMIN_PASSWORD='admin123'
ARG ACTIVE_ENV='docker'
ARG LOG_LEVEL='info'

COPY ./torch-yun-backend/torch-yun-main/build/libs/zhishuyun.jar /opt/zhishuyun/zhishuyun.jar
COPY ./torch-yun-backend/torch-yun-main/src/main/resources/application-docker.yml /etc/zhishuyun/conf/

RUN apt-get update
RUN apt-get install -y build-essential python3 libsasl2-dev libsasl2-modules-gssapi-mit libsasl2-modules pip
RUN pip install langchain-openai langchain-core akshare mplfinance pandas matplotlib yfinance plot

EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}
ENV ACTIVE_ENV=${ACTIVE_ENV}
ENV LOG_LEVEL=${LOG_LEVEL}
ENV PARAMS=""
ENV JVMOPTIONS=""

ENTRYPOINT ["sh","-c","java $JVMOPTIONS -jar /opt/zhishuyun/zhishuyun.jar --logging.level.root=${LOG_LEVEL} --spring.profiles.active=${ACTIVE_ENV} --isx-app.admin-passwd=${ADMIN_PASSWORD} --spring.config.additional-location=/etc/zhishuyun/conf/ $PARAMS"]
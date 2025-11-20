FROM tomcat:9.0.12-jre8-alpine

ENV TZ=Asia/Shanghai

RUN apk add --update font-adobe-100dpi ttf-dejavu fontconfig

RUN apk --no-cache update && \
    apk --no-cache add bash bash-completion && \
    rm -rf /var/cache/apk/* && \
    sed -i -e "s/bin\/ash/bin\/bash/" /etc/passwd && \
    rm /bin/sh && \
    ln -s /bin/bash /bin/sh

# timezone
RUN apk --no-cache update && \
    apk --no-cache add curl tzdata && \
    rm -rf /var/cache/apk/* && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime

RUN rm -rf /usr/local/tomcat/webapps/ROOT && \
    rm -rf /usr/local/tomcat/webapps/docs && \
    rm -rf /usr/local/tomcat/webapps/examples && \
    rm -rf /usr/local/tomcat/webapps/manager && \
    rm -rf /usr/local/tomcat/webapps/host-manager

COPY web/target/ROOT /usr/local/tomcat/webapps/ROOT

COPY docker/run.sh /run.sh
COPY docker/context.xml /usr/local/tomcat/conf/context.xml
COPY docker/server.xml /usr/local/tomcat/conf/server.xml

RUN chmod +x /run.sh

CMD ["sh", "/run.sh"]
FROM node:11.2.0-stretch

WORKDIR /root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        wget \
        man-db \
    && rm -rf /var/lib/apt/lists/*

# OpenJDK:
ENV LANG C.UTF-8
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_VERSION 8u181
ENV JAVA_DEBIAN_VERSION 8u181-b13-2~deb9u1
ENV CA_CERTIFICATES_JAVA_VERSION 2070929~deb9u1
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        bzip2 \
        unzip \
        xz-utils \
        rlwrap \
        openjdk-8-jdk="$JAVA_DEBIAN_VERSION" \
        ca-certificates-java \
    && rm -rf /var/lib/apt/lists/*
RUN /var/lib/dpkg/info/ca-certificates-java.postinst configure

# Clojure cli tools
RUN wget -q -O install.sh https://download.clojure.org/install/linux-install-1.10.0.414.sh \
    && bash install.sh -p /usr/local \
    && rm install.sh

# port for web and repl
EXPOSE 8080
EXPOSE 3000
EXPOSE 3575

RUN deluser --remove-home node

RUN npm install --global --unsafe-perm lumo-cljs
RUN npm install --global nexe

ENV TZ="Asia/Tokyo"

# for convenience
COPY ./.bashrc /root/
COPY ./deps.edn /root/.clojure/deps.edn

# startup script
COPY ./dev.sh /root/
RUN chmod 755 /root/dev.sh

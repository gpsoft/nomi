FROM node:11.2.0-stretch

WORKDIR /root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        wget \
    && rm -rf /var/lib/apt/lists/*

# port for web and repl
EXPOSE 8080
EXPOSE 3000
EXPOSE 3575

RUN deluser --remove-home node

RUN npm install --global --unsafe-perm lumo-cljs
RUN npm install --global nexe

# for convenience
COPY ./.bashrc /root/

# startup script
COPY ./dev.sh /root/
RUN chmod 755 /root/dev.sh

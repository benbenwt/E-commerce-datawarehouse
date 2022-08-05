#FROM docker.elastic.co/elasticsearch/elasticsearch:7.11.1
FROM centos:7

ADD ./jdk-11.0.10_linux-x64_bin.tar.gz /usr/local/java/
ENV JAVA_HOME /usr/local/java/jdk-11.0.10
ENV PATH $JAVA_HOME/bin:$PATH
RUN useradd -m elasticsearch
ADD ./kibana-7.11.1-linux-x86_64.tar.gz  /usr/local
COPY --chown=elasticsearch:elasticsearch ./elasticsearch /usr/share/elasticsearch
#COPY  --chown=elasticsearch:elasticsearch  ./elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml
COPY --chown=elasticsearch:elasticsearch ./kibana.yml /usr/local/kibana-7.11.1-linux-x86_64/config
COPY  --chown=elasticsearch:elasticsearch  ./entrypoint.sh /usr/local
#USER root
#ENTRYPOINT ["/usr/local/entrypoint.sh"]
#CMD ["/usr/local/kibana-7.11.1-linux-x86_64/bin/kibana ","--allow-root"]
USER elasticsearch
ENTRYPOINT  ["/usr/local/entrypoint.sh"]

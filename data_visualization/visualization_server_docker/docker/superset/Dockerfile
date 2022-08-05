FROM centos:7
ENV FLASK_APP=superset
RUN useradd -m platform \
&& yum install -y gcc gcc-c++ libffi-devel \
MySQL-python mysql-devel python-devel \
python-devel python-pip python-wheel python-setuptools\
 openssl-devel cyrus-sasl-devel openldap-devel bzip2-devel expat-devel\
gdbm-devel readline-devel sqlite-devel automake  autoconf libtool make \
libffi-devel wget
RUN cd /usr/local/ \
&& wget https://www.python.org/ftp/python/3.7.0/Python-3.7.0.tgz\
&&tar -zxvf Python-3.7.0.tgz \
&& cd /usr/local/Python-3.7.0/ \
&& ./configure --prefix=/usr/local/python3\
&& make && make install\
&&mv /usr/bin/python /usr/bin/python_bak\
&&ln -s /usr/local/python3/bin/python3 /usr/bin/python\
&& cd /usr/local\
&& wget https://bootstrap.pypa.io/get-pip.py\
&&  python get-pip.py\
&&ln -s /usr/local/python3/bin/pip3 /usr/bin/pip
Run  pip install --upgrade setuptools pip -i https://pypi.douban.com/simple/ 
Run pip install apache-superset==0.36.0  -i https://pypi.douban.com/simple/ \
markupsafe==2.0.1 -i https://pypi.douban.com/simple/ \
WTForms==2.3.3 \
sqlalchemy==1.3.24  \
pymysql
#Run superset db upgrade \
#&&superset fab create-admin \
#&&superset init \
#&&pip install gunicorn -i https://pypi.douban.com/simple/ \
#&&gunicorn --workers 5 --timeout 120 --bind hadoop102:8787  "superset.app:create_app()" --daemon 
#COPY   --chown=platform:platform  ./superset /home/platform/
COPY   --chown=platform:platform  ./entrypoint.sh /home/platform/
#CMD ["python3","-m","http.server","8888"]
USER root
CMD ["sh","/home/platform/entrypoint.sh"]

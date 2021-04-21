#!/usr/bin/env bash
LANG=zh_CN.UTF-8

function LOG_INFO()
{
    local content=${1}
    echo -e "\033[32m"${content}"\033[0m"
}

function docker_install()
{
	echo "check Docker......"
	docker -v
    if [ $? -eq  0 ]; then
        echo "Docker already installed!"
    else
    	echo "install docker ..."
    	a=`uname  -a`
      D="Darwin"
      C="CentOS"
      CL="centos"
      U="Ubuntu"
      UL="ubuntu"
      if [[ $a =~ $D ]];then
          brew install --cask --appdir=/Applications docker
      elif [[ $a =~ $C ]] || [[ $a =~ $CL ]];then
          curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
          sudo systemctl start docker
      elif [[ $a =~ $U ]] || [[ $a =~ $UL ]];then
          curl -fsSL https://get.docker.com -o get-docker.sh
          sudo sh get-docker.sh
      fi
      if [ $? -ne 0 ]; then
          LOG_INFO "docker自动化安装失败！请手动安装docker后，再启动脚本，安装参考链接：https://www.runoob.com/docker/ubuntu-docker-install.html"
      else
          LOG_INFO "docker install success"
      fi
    fi
    # create share network==bridge
    #docker network create share_network
}

docker_install

BASE_DIR=`pwd`

CONFIGPATH="${BASE_DIR}/config/application.properties"
LOCALIP=` ifconfig | grep "inet " | grep -v 127.0.0.1 | awk -F ' ' '{print $2}' | awk 'NR==1'`

if [ "$(uname)" == "Darwin" ]; then
  sed -i  "" "s/localhost/${LOCALIP}/g" ${CONFIGPATH}
  sed -i  "" "s/127.0.0.1/${LOCALIP}/g" ${CONFIGPATH}
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
  sed -i 's/localhost/'"${LOCALIP}"'/g' $CONFIGPATH
  sed -i 's/127.0.0.1/'"${LOCALIP}"'/g' $CONFIGPATH
fi

# @function: output information log
# @param: content: information message

while getopts meg OPT;
do
  case $OPT in
    m)
      mysqlexist=`docker inspect --format '{{.State.Running}}' mysql`
      if [ "${mysqlexist}" != "true" ]; then
        chmod -R 777 ./data/mysql/
        docker pull mysql:5.7
        docker run -p 3307:3306 --name mysql -v "$BASE_DIR"/data/mysql/:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -e  MYSQL_DATABASE=data_export -d mysql:5.7
      fi
      LOG_INFO "docker run mysql success..."
      ;;
    e)
      esexist=`docker inspect --format '{{.State.Running}}' elasticsearch`
      if [ "${esexist}" != "true" ]; then
        chmod -R 777 ./data/elasticsearch/
        docker pull elasticsearch:7.8.0
        docker run --name elasticsearch -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -e "discovery.type=single-node" -p 9200:9200 -p 9300:9300  -v  "$BASE_DIR"/data/elasticsearch:/usr/share/elasticsearch/data -d  elasticsearch:7.8.0
        if [ "$(uname)" == "Darwin" ]; then
          sed -i  "" "s/system.es.enabled=false/system.es.enabled=true/g" ${CONFIGPATH}
          sed -i  "" "s/system.es.clusterName=my-application/system.es.clusterName=docker-cluster/g" ${CONFIGPATH}
        elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
          sed -i 's/system.es.enabled=false/system.es.enabled=true/g' $CONFIGPATH
          sed -i 's/system.es.clusterName=my-application/system.es.clusterName=docker-cluster/g' $CONFIGPATH
        fi
      fi
        LOG_INFO "docker run elasticsearch success..."
      ;;
    g)
      grafanaexist=`docker inspect --format '{{.State.Running}}' grafana`
      if [ "${grafanaexist}" != "true" ]; then
        docker pull grafana/grafana
        docker run   -d   -p 3000:3000   --name=grafana   -e "GF_INSTALL_PLUGINS=grafana-clock-panel,grafana-simple-json-datasource"   grafana/grafana
        if [ $? -ne 0 ]; then
          echo "grafana run failed"
        else
          LOG_INFO "grafana run success"
        fi
        if [ "$(uname)" == "Darwin" ]; then
          sed -i  "" "s/system.grafanaEnable=false/system.grafanaEnable=true/g" ${CONFIGPATH}
        elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
          sed -i 's/system.grafanaEnable=false/system.grafanaEnable=true/g' $CONFIGPATH
        fi
      fi
      ;;
    ?)
      LOG_INFO "unkonw argument\nusage: -m auto install mysql"
      exit 1
      ;;
  esac
done

docker pull fiscoorg/dataexport:1.7.2
docker run -d -p 5200:5200  -v "$BASE_DIR"/config/:/config -v "$BASE_DIR"/log/:/log --name dataexport fiscoorg/dataexport:1.7.2
if [ $? -ne 0 ]; then
    echo "data export run failed"
else
    LOG_INFO "data export run success"
    LOG_INFO "See the logging command: docker logs -f dataexport"
fi
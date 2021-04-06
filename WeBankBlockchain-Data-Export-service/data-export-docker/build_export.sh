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


docker pull wangyue168git/dataexport:1.7.2
docker run -d -p 5200:5200  -v "$BASE_DIR"/config/:/config -v "$BASE_DIR"/log/:/log --name dataexport wangyue168git/dataexport:1.7.2
if [ $? -ne 0 ]; then
    echo "data export run failed"
else
    LOG_INFO "data export run success"
    LOG_INFO "See the logging command: docker logs -f dataexport"
fi

v=`grep system.grafanaEnable ${CONFIGPATH} | cut -d'=' -f2`

if [ ${v} == "true" ]; then
    docker pull grafana/grafana
    docker run   -d   -p 3000:3000   --name=grafana   -e "GF_INSTALL_PLUGINS=grafana-clock-panel,grafana-simple-json-datasource"   grafana/grafana
    if [ $? -ne 0 ]; then
      echo "grafana run failed"
    else
      LOG_INFO "grafana run success"
    fi
fi


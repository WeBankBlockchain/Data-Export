#!/usr/bin/env bash
LANG=zh_CN.UTF-8
BRANCH_NAME="master"

##############################################################################
##
##  WeBASE-Codegen-Monkey start up script for UN*X.
##  WeBASE-Codegen-Monkey is an automatic code Generator. 
##
##  created by jiayumao
##
##############################################################################

# functions
function prop {
    grep "${1}" application.properties|cut -d'=' -f2
}

function is_empty_dir(){ 
    ls -A $1|wc -w
}

function LOG_ERROR()
{
    local content=${1}
    echo -e "\033[31m"${content}"\033[0m"
}

# @function: output information log
# @param: content: information message
function LOG_INFO()
{
    local content=${1}
    echo -e "\033[32m"${content}"\033[0m"
}

function check_java(){
   version=$($JAVACMD -version 2>&1 |grep version |awk '{print $3}')
   len=${#version}-2
   version=${version:1:len}

   IFS='.' arr=($version)
   IFS=' '
   if [ -z ${arr[0]} ];then
      LOG_ERROR "At least Java8 is required."
      exit 1
   fi
   if [ ${arr[0]} -eq 1 ];then
      if [ ${arr[1]} -lt 8 ];then
           LOG_ERROR "At least Java8 is required."
           exit 1
      fi
   elif [ ${arr[0]} -gt 8 ];then
          :
   else
       LOG_ERROR "At least Java8 is required."
       exit 1
   fi
}

function checkout_version(){
  if [ ${ver} != "latest" ]; then
    if [ $(git tag -l "V$ver") ]; then
      git checkout V$ver
    else
      LOG_ERROR "bee version $ver is not exists, please check."
      exit 1;
    fi
  fi
}

### get argvs
ver="latest"
exec="run"
while getopts "e:v:" arg
do
  case $arg in
    e)
      execArg=$(echo "$OPTARG" | tr '[:upper:]' '[:lower:]')
      if [ "$execArg" != "run" ] && [ "$execArg" != "build" ]; then
        LOG_ERROR "-e execute mode: [build|run]"
      else
        exec=$execArg
      fi
      ;;
    v)
      ver=$OPTARG
      ;;
    ?)
      LOG_ERROR "unkonw argument\nusage: -e [build|run], -v [bee_version]"
      exit 1
      ;;
  esac
done

LOG_INFO "execute mode = $exec"
LOG_INFO "bee version = $ver"

#### config props
APPLICATION_FILE="config/resources/application.properties"
DEF_FILE="config/resources/*.def"
APPLICATION_TMP_FILE="config/resources/application.properties.tmp"
CONTRACT_DIR="config/contract"
CERT_DIR="config/resources"
RESOURCE_DIR="src/main/resources"
JAVA_CODE_DIR="src/main/java"
BUILD_DIR="dist"

BMP=".tools"
BM="WeBASE-Codegen-Monkey"
BB="WeBASE-Collect-Bee"
BBCOMMON="WeBASE-Collect-Bee-common"
BBC="WeBASE-Collect-Bee-core"
BASE_DIR=`pwd`
LOG_INFO "work dir is $BASE_DIR"

#### system tables
ENTITY_DIR="$BB/WeBASE-Collect-Bee-db/src/main/java/com/webank/webasebee/db/entity"
ACCOUNT_INFO_TABLE="account_info"
BLOCK_DETAIL_INFO_TABLE="block_detail_info"
BLOCK_TASK_POOL_TABLE="block_task_pool"
BLOCK_TX_DETAIL_INFO_TABLE="block_tx_detail_info"


#### check the config file exists.
if [ -f "$APPLICATION_FILE" ];then
  LOG_INFO "Check [appliction.properties] done."
else
  LOG_ERROR "The config file [appliction.properties] doesn't exist. Please don't delete it."
  exit 1
fi
if [ -d "$CONTRACT_DIR" ];then
  LOG_INFO "Check [contracts] done."
else
  LOG_ERROR "The config dir [contracts] doesn't exist. Please don't delete it."
  exit 1
fi
if [ "`ls -A $CONTRACT_DIR`" = "" ]; then
  LOG_ERROR "$CONTRACT_DIR is indeed empty"
  exit 1
fi
if [ -d "$CERT_DIR" ];then
  LOG_INFO "Check [resources] done."
else
  LOG_ERROR "The config dir [resources] doesn't exist. Please don't delete it."
  exit 1
fi
if [ "`ls -A $CERT_DIR`" = "" ]; then
  LOG_ERROR "$CERT_DIR is indeed empty"
  exit 1
fi

# Begin to read parameters from application.properties
if [ -f "$APPLICATION_FILE" ]
then
  LOG_INFO "$APPLICATION_FILE exist."
  grep -v "^#"  $APPLICATION_FILE | grep -v "^$" | grep "="  > $APPLICATION_TMP_FILE

  while IFS='=' read -r key value
  do
    key=$(echo $key | tr '.' '_')
    key=`echo $key |sed 's/^ *\| *$//g'`
    eval "${key}='${value}'"
  done < "$APPLICATION_TMP_FILE"
  rm -f $APPLICATION_TMP_FILE
else
  LOG_ERROR "$APPLICATION_FILE not found."
  exit 1
fi

LOG_INFO "system.nodeStr =  ${system_nodeStr} "
LOG_INFO "system.groupId          =  ${system_groupId} "
LOG_INFO "system.dbUrl =  ${system_dbUrl} "
LOG_INFO "system.dbUser =  ${system_dbUser} "
LOG_INFO "system.dbPassword =  ${system_dbPassword} "
LOG_INFO "system.group          =  ${system_group} "
LOG_INFO "system.baseProjectPath          =  ${system_baseProjectPath} "
LOG_INFO "system.contractPackName =  ${system_contractPackName} "
LOG_INFO "system.multiLiving = ${system_multiLiving} "
LOG_INFO "server.port             =  ${server_port} "
LOG_INFO "system.tablePrefix =  ${system_tablePrefix} "
LOG_INFO "system.tablePostfix =  ${system_tablePostfix} "


# begin to check config nt null
if  [ ! -n "${system_nodeStr}" ] ;then
LOG_ERROR "invalid system nodestr! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_groupId}" ] ;then
LOG_ERROR "invalid system groupId! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_dbUrl}" ] ;then
LOG_ERROR "invalid system dbUrl! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_dbUser}" ] ;then
LOG_ERROR "invalid system dbUser! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_dbPassword}" ] ;then
LOG_ERROR "invalid system dbPassword! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_group}" ] ;then
LOG_ERROR "invalid system group! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_baseProjectPath}" ] ;then
LOG_ERROR "invalid system baseProjectPath! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_contractPackName}" ] ;then
LOG_ERROR "invalid system contractPackName! Please check the application.properties."
exit 1
fi
if  [ ! -n "${system_multiLiving}" ] ;then
LOG_ERROR "invalid system multiLiving! Please check the application.properties."
exit 1
fi
if  [ ! -n "${server_port}" ] ;then
LOG_ERROR "invalid server port! Please check the application.properties."
exit 1
fi

count=`find $BASE_DIR/$CONTRACT_DIR -type f -print |grep ".java" | wc -l`
LOG_INFO "Find $count java files"
if [[ $count -lt 1 ]];then
  LOG_ERROR "Not find java files."
  exit 1
fi

## begin to check java package name
LOG_INFO "Checking your java contract package name ..."
for file in $BASE_DIR/$CONTRACT_DIR/*
do
  if head -n 1 $file | grep "${system_contractPackName}">/dev/null
  then
    continue
  else
    LOG_ERROR "Invalid java package name. Please make sure your config is equal to your package name."
    exit 1
  fi
done

# check the environment
## check server port is used
check_port() {
        LOG_INFO "Checking instance port ..."
        netstat -tlpn | grep "\b$1\b"
}
if check_port $server_port
then
        LOG_ERROR "ERROR: the port of server is used, please check it or modify server.port in application.properties."
    exit 1
fi

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi
LOG_INFO "JAVACMD: $JAVACMD"

## check the version of Java
check_java

contractPath=$(echo ${system_contractPackName} | tr '.' '/')
LOG_INFO "contractPath: $contractPath"
group=$(echo ${system_group} | tr '.' '/')
LOG_INFO "group: $group"


if [ -d "$BMP/$BM" ];then
  LOG_INFO "Monkey already exist."
  cd $BMP/$BM
  ## rm cached files
  rm -rf src/main/java/com/webank/blockchain/
  rm -rf src/main/java/org/
  git fetch
  git reset --hard HEAD
  git checkout $BRANCH_NAME
  git pull
  if [ $? == 0 ];then
  	LOG_INFO "git pull success"
  else
     LOG_ERROR "git pull fail"
     exit 1;
  fi
else
  LOG_INFO "Begin to download Monkey ..."
  mkdir -p $BMP
  cd $BMP
  git clone https://github.com/WeBankFinTech/$BM.git
  cd $BM
  git checkout -b $BRANCH_NAME origin/$BRANCH_NAME
fi
checkout_version

cd $BASE_DIR

if [ -d "$BB" ];then
  LOG_INFO "Bee already exist."
  cd $BB
    ## rm cached files
  rm -rf src/main/java/com/webank/bcosbee/generated
  rm -rf src/main/java/com/webank/webasebee/generated
  rm -rf src/main/java/com/webank/blockchain/
  rm -rf src/main/java/org
  git fetch
  git reset --hard HEAD
  git checkout $BRANCH_NAME
  git pull
  if [ $? == 0 ];then
  	LOG_INFO "git pull success"
  else
     LOG_ERROR "git pull fail"
     exit 1;
  fi
else
  LOG_INFO "Begin to download Bee ..."
  git clone https://github.com/WeBankFinTech/$BB.git
  cd $BB
  git checkout -b $BRANCH_NAME origin/$BRANCH_NAME
fi
checkout_version

# replace table name check
LOG_INFO "Replace table name check."
cd $BASE_DIR
# prefix/postfix not empty and not whitespace
if [ ! -z "${system_tablePrefix//[[:blank:]]/}" ] || [ ! -z "${system_tablePostfix//[[:blank:]]/}" ]; then
  prefix=${system_tablePrefix//[[:blank:]]}
  postfix=${system_tablePostfix//[[:blank:]]/}
  LOG_INFO "Replacing table name, tablePrefix=$prefix, tablePostfix=$postfix"
  newAccountInfoTable="${prefix}${ACCOUNT_INFO_TABLE}${postfix}"
  newBlockDetailInfoTable="${prefix}${BLOCK_DETAIL_INFO_TABLE}${postfix}"
  newBlockTaskPoolTable="${prefix}${BLOCK_TASK_POOL_TABLE}${postfix}"
  newBlockTxDetailInfoTable="${prefix}${BLOCK_TX_DETAIL_INFO_TABLE}${postfix}"

  cd $BASE_DIR/$ENTITY_DIR

  if [ "$(uname)" == "Darwin" ]; then
    sed -i "" "s/account_info/$newAccountInfoTable/g" AccountInfo.java
    sed -i "" "s/block_detail_info/$newBlockDetailInfoTable/g" BlockDetailInfo.java
    sed -i "" "s/block_task_pool/$newBlockTaskPoolTable/g" BlockTaskPool.java
    sed -i "" "s/block_tx_detail_info/$newBlockTxDetailInfoTable/g" BlockTxDetailInfo.java
  elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    sed -i "s/account_info/$newAccountInfoTable/g" AccountInfo.java
    sed -i "s/block_detail_info/$newBlockDetailInfoTable/g" BlockDetailInfo.java
    sed -i "s/block_task_pool/$newBlockTaskPoolTable/g" BlockTaskPool.java
    sed -i "s/block_tx_detail_info/$newBlockTxDetailInfoTable/g" BlockTxDetailInfo.java
  fi
fi

cd $BASE_DIR

# init config
cd $BMP/$BM
mkdir -p $RESOURCE_DIR/
cp -f $BASE_DIR/$APPLICATION_FILE $RESOURCE_DIR/
cp -f $BASE_DIR/$DEF_FILE $RESOURCE_DIR/

LOG_INFO "copy application.properties done."
mkdir -p $JAVA_CODE_DIR/$contractPath
cp -f $BASE_DIR/$CONTRACT_DIR/* $JAVA_CODE_DIR/$contractPath/
mkdir -p ./$CONTRACT_DIR
cp -f $BASE_DIR/$CONTRACT_DIR/* ./$CONTRACT_DIR
LOG_INFO "copy java contract codes done."

# build
bash gradlew clean bootJar
LOG_INFO "$BM build done"

# run
cd $BUILD_DIR
chmod +x WeBASE*
$JAVACMD -jar WeBASE*
LOG_INFO "$BB generate done."
cd $BASE_DIR
#rm -rf $BM

cd $BB
for file in $BASE_DIR/$CONTRACT_DIR/*
do
  file=${file##*/}
  if [[ $file == *.jar ]];
  then
    cp -f $BASE_DIR/$CONTRACT_DIR/$file ./libs/
  fi
done


cd $BBC
mkdir -p $RESOURCE_DIR/
cp -f  $BASE_DIR/$CERT_DIR/ca.crt $RESOURCE_DIR/
# cp -f  ../$CERT_DIR/client.keystore $RESOURCE_DIR/
cp -f  $BASE_DIR/$CERT_DIR/node.crt $RESOURCE_DIR/
cp -f  $BASE_DIR/$CERT_DIR/node.key $RESOURCE_DIR/

LOG_INFO "copy certs done."


mkdir -p ../$BBCOMMON/$JAVA_CODE_DIR/$contractPath
for file in $BASE_DIR/$CONTRACT_DIR/*
do
  file=${file##*/}
  if [[ $file == *.java ]];
  then
    cp -f $BASE_DIR/$CONTRACT_DIR/$file ../$BBCOMMON/$JAVA_CODE_DIR/$contractPath/
  fi
done
mkdir -p ./$CONTRACT_DIR
cp -f $BASE_DIR/$CONTRACT_DIR/* ./$CONTRACT_DIR
LOG_INFO "copy java contract codes done."


cd $BASE_DIR/$BB
bash gradlew clean bootJar

LOG_INFO "$BB build done"

if [ "$exec" == "run" ];then
LOG_INFO "start to run $BB"
cd $BBC/$BUILD_DIR
chmod +x WeBASE*
$JAVACMD -jar WeBASE*
fi



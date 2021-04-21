#!/usr/bin/env bash
LANG=zh_CN.UTF-8

cd ../WeBankBlockchain-Data-Export-service

function LOG_INFO()
{
    local content=${1}
    echo -e "\033[32m"${content}"\033[0m"
}

GRADLE_EXEC="bash gradlew "
while getopts "c:" arg
do
  case $arg in
    c)
      execArg=$(echo "$OPTARG" | tr '[:upper:]' '[:lower:]')
      if [ "$execArg" != "gradle" ] && [ "$execArg" != "gradlew" ]; then
        LOG_ERROR "-c execute mode: [gradlew|gradle]"
      elif [ "$execArg" == "gradle" ]; then
        GRADLE_EXEC=$execArg
        LOG_INFO "Begin to use gradle"
      else
        LOG_INFO "Begin to use gradle wrapper"
      fi
      ;;
    ?)
      LOG_ERROR "unkonw argument\nusage: -c [gradle|gradlew]"
      exit 1
      ;;
  esac
done

$GRADLE_EXEC clean bootJar
echo "gradle build finish..."

cp dist/Data-Export*.jar ../tools
cd ../tools

echo "copy Data-Export.jar success..."

echo "run Data-Export.jar"

java -jar Data-Export*.jar --spring.config.location=./config/application.properties
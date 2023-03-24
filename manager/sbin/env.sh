# env.sh
# Resolve APP_HOME in case that $0 is this shell script or a symbolic link.
if [ -z "$APP_HOME" ]; then
  PRG="$0"
  while [ -h "$PRG" ]; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG=$(dirname "$PRG")/"$link"
    fi
  done
  cd "$(dirname "$PRG")/.." || exit 1
  APP_HOME="$(pwd)"
  export APP_HOME
  cd - > /dev/null || exit 1
fi

# Local Directories
export CONF_DIR=$APP_HOME/conf
export EXT_DIR=$APP_HOME/ext

# Configuration Files
export NODES_FILE=$CONF_DIR/nodes

# Remote Info.
NODES=$(grep -v -e '^[[:space:]]*$' "$NODES_FILE")
export NODES
USER=$(whoami)
export USER
export BASE_DIR=~/chain_data
export CHAIN=chain

# ZooKeeper
export USER_ZOOKEEPER=$USER
export ZOOKEEPER_HOME=$BASE_DIR/zookeeper
export ZOOKEEPER_CLIENT_PORT=2181
export ZOOKEEPER_CONFIG=$ZOOKEEPER_HOME/conf/zookeeper-$CHAIN.cfg
export ZOOKEEPER_DATA_DIR=$ZOOKEEPER_HOME/data/zookeeper-$CHAIN

# Kafka
export KAFKA_TAR_FILE=kafka_2.13-3.3.1.tgz
export USER_KAFKA=$USER
export KAFKA_HOME=$BASE_DIR/kafka
export KAFKA_BIN_DIR=$BASE_DIR/kafka/bin
export KAFKA_CONFIG=$KAFKA_HOME/config/kafka-$CHAIN.properties
export KAFKA_LOG_DIR=$KAFKA_HOME/data/zookeeper-$CHAIN
export KAFKA_PORT=9092
export WORKERS_PER_TOPIC=1

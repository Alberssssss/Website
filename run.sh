#!/usr/bin/env bash
# 文件: run.sh
# 作用: 停掉旧服务、启动新 Jar，并把日志写到 logs/out.log

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$APP_DIR"

# 读取 .env 中的数据库配置（若存在）
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

if [ -z "$DB_PASSWORD" ]; then
  echo "❌ 未配置数据库密码 (DB_PASSWORD)，请在 .env 文件中设置"
  exit 1
fi

JAR_FILE=$(ls springfx-demo-*.jar 2>/dev/null | sort | tail -n1)
if [ -z "$JAR_FILE" ]; then
  echo "❌ 没找到任何 springfx-demo-*.jar，退出"
  exit 1
fi

echo "🟢 部署目录: $APP_DIR"
echo "📦 部署 Jar: $JAR_FILE"

OLD_PID=$(pgrep -f "$JAR_FILE")
if [ -n "$OLD_PID" ]; then
  echo "🛑 停掉旧进程 (PID=$OLD_PID)"
  kill "$OLD_PID"
  sleep 2
fi

mkdir -p logs

echo "🚀 启动新服务: java -jar $JAR_FILE"
nohup java -jar "$JAR_FILE" > logs/out.log 2>&1 &

NEW_PID=$!

# 等待几秒以便判断进程是否成功启动
sleep 5

if ps -p "$NEW_PID" > /dev/null; then
  echo "✅ 服务已启动 (PID=$NEW_PID)，日志：$APP_DIR/logs/out.log"
else
  echo "❌ 服务启动失败，以下为日志内容："
  cat logs/out.log
  exit 1
fi


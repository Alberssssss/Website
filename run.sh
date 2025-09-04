#!/usr/bin/env bash
# 文件: run.sh
# 作用: 停掉旧服务、启动新 Jar，并把日志写到 logs/out.log

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$APP_DIR"

JAR_FILE=$(ls springfx-demo-*.jar 2>/dev/null | sort | tail -n1)
if [ -z "$JAR_FILE" ]; then
  echo "❌ 没找到任何 springfx-demo-*.jar，退出"
  exit 1
fi

echo "🟢 部署目录: $APP_DIR"
echo "📦 部署 Jar: $JAR_FILE"

OLD_PIDS=$(pgrep -f "$JAR_FILE")
if [ -n "$OLD_PIDS" ]; then
  echo "🛑 停掉旧进程 (PID=$OLD_PIDS)"
  for OLD_PID in $OLD_PIDS; do
    kill -9 "$OLD_PID"
    sleep 2
  done
fi

mkdir -p logs

echo "🚀 启动新服务: java -jar $JAR_FILE"
nohup sudo java -jar "$JAR_FILE" > logs/out.log 2>&1 &

NEW_PID=$!
echo "✅ 服务已启动 (PID=$NEW_PID)，日志：$APP_DIR/logs/out.log"


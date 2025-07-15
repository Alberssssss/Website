#!/usr/bin/env bash
#
# run.sh — 用 nohup 后台启动 Spring Boot 应用，并把日志重定向到 logs 目录

# 找到最新的 JAR（无需手动改版本号）
APP_JAR=$(ls target/springfx-demo-*.jar | sort | tail -n1)

# 日志目录
LOG_DIR="logs"
mkdir -p "$LOG_DIR"

# 启动应用（加 sudo）
echo "Starting $APP_JAR ..."
nohup sudo java -jar "$APP_JAR" > "$LOG_DIR/out.log" 2>&1 &

# 输出新进程 PID
echo "Application started with PID $! (logs -> $LOG_DIR/out.log)"

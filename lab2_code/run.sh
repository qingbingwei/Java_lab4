#!/bin/bash

# RBAC权限管理系统 - 运行脚本

echo "=========================================="
echo "  RBAC权限管理系统"
echo "  启动中..."
echo "=========================================="
echo ""

# 检查是否已编译
if [ ! -d "bin" ]; then
    echo "✗ 未找到bin目录，请先运行 ./compile.sh 编译项目"
    exit 1
fi

# 检查SQLite驱动
if [ ! -f "lib/sqlite-jdbc-3.36.0.3.jar" ]; then
    echo "✗ 未找到SQLite JDBC驱动，请先运行 ./compile.sh"
    exit 1
fi

# 运行程序
java -cp "bin:lib/*" Main

echo ""
echo "=========================================="
echo "  程序已退出"
echo "=========================================="

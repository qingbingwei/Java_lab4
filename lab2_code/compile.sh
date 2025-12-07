#!/bin/bash

# RBAC权限管理系统 - 编译脚本

echo "=========================================="
echo "  RBAC权限管理系统 - 编译"
echo "=========================================="

# 创建bin目录
if [ ! -d "bin" ]; then
    mkdir bin
    echo "✓ 创建bin目录"
fi

# 检查lib目录是否存在
if [ ! -d "lib" ]; then
    mkdir lib
    echo "✓ 创建lib目录"
fi

# 检查SQLite驱动是否存在
if [ ! -f "lib/sqlite-jdbc-3.36.0.3.jar" ]; then
    echo "⚠ 未找到SQLite JDBC驱动"
    echo "正在下载..."
    cd lib
    curl -L -O https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar
    cd ..
    echo "✓ SQLite驱动下载完成"
fi

# 编译所有Java文件
echo ""
echo "正在编译..."

# 查找所有Java文件并编译
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin -cp "lib/*" @sources.txt

if [ $? -eq 0 ]; then
    echo "✓ 编译成功"
    rm sources.txt
    
    # 复制配置文件
    if [ -d "src/config" ]; then
        mkdir -p bin/config
        cp src/config/* bin/config/
        echo "✓ 配置文件已复制"
    fi
    
    echo ""
    echo "=========================================="
    echo "  编译完成！"
    echo "  运行命令: ./run.sh"
    echo "=========================================="
else
    echo "✗ 编译失败"
    rm sources.txt
    exit 1
fi

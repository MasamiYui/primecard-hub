#!/bin/bash

# PrimeCard Hub 测试数据导入脚本
# 创建时间: 2023-06-15

echo "开始导入PrimeCard Hub测试数据..."

# 检查H2数据库是否正在运行
if [ ! -f "./data/primecarddb.mv.db" ]; then
    echo "错误: 未找到数据库文件，请确保应用已经启动过至少一次，以初始化数据库。"
    exit 1
fi

# 使用H2控制台执行SQL脚本
echo "正在执行SQL脚本..."

# 设置H2数据库连接信息
DB_URL="jdbc:h2:file:./data/primecarddb"
DB_USER="sa"
DB_PASSWORD="password"

# 检查是否安装了Java
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java，请安装Java运行环境。"
    exit 1
fi

# 检查H2 JAR文件
H2_JAR="./h2-2.1.214.jar"
if [ ! -f "$H2_JAR" ]; then
    echo "未找到H2数据库JAR文件，正在下载..."
    curl -o "$H2_JAR" https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar
    if [ $? -ne 0 ]; then
        echo "错误: 下载H2数据库JAR文件失败。"
        exit 1
    fi
fi

# 执行SQL脚本
java -cp "$H2_JAR" org.h2.tools.RunScript -url "$DB_URL" -user "$DB_USER" -password "$DB_PASSWORD" -script "./src/main/resources/test-data.sql" -showResults

if [ $? -eq 0 ]; then
    echo "测试数据导入成功！"
    echo "现在你可以使用以下账号登录系统："
    echo "管理员账号: admin@primehub.com 密码: password"
    echo "编辑账号: editor@primehub.com 密码: password"
    echo "查看账号: viewer@primehub.com 密码: password"
    echo "注意: 这些是测试账号，请勿用于生产环境。"
else
    echo "错误: 测试数据导入失败，请检查错误信息。"
fi

exit 0
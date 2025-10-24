AllayBuilder 插件文档

AllayBuilder 是一个用于 AllayMC 服务器的建筑辅助插件，提供了选区管理、结构保存/加载、区域填充和替换等强大功能，帮助玩家更高效地进行建筑创作。

🧰 功能特性

选区管理

• 使用木斧右键方块设置选区点（posA 和 posB）

• 自动显示选区边界框（白色线框）

• 支持多玩家独立选区

结构操作

• 保存结构：将选区保存为 .mcstructure 文件

• 加载结构：从文件加载保存的结构

• 自动分割大型结构为 16x16 区块

• 提供结构列表选择界面

区域编辑

• 填充：用指定方块填充选区

• 替换：替换选区内的特定方块

• 保留：只替换非空气方块

• 维护：只替换非指定方块的方块

• 实时显示操作进度（百分比和数量）

开发者工具

• 坐标轴指示器（红/绿/蓝三色箭头）

• 方向指示器（黄色箭头）

• 异步任务处理（不阻塞主线程）

⬇️ 安装方法

1. 将插件 JAR 文件放入服务器的 plugins 目录
2. 重启服务器
3. 确保服务器有 ./structures/ 目录用于保存结构文件

🎮 使用指南

基础命令


/builder 或 /yb 或 /ybuilder


选区设置

1. 手持木斧（wooden axe）
2. 右键点击方块设置第一个点（posA）
3. 再次右键设置第二个点（posB）
4. 第三次右键会清除当前选区并重新开始

命令列表

命令 功能 示例

/builder fill <方块> 用指定方块填充选区 /builder fill minecraft:stone

/builder replace <新方块> <旧方块> 替换选区内的特定方块 /builder replace minecraft:glass minecraft:stone

/builder keep <方块> 只替换非空气方块 /builder keep minecraft:glass

/builder maintain <新方块> <保留方块> 只替换非指定方块 /builder maintain minecraft:glass minecraft:stone

/builder save [名称] 保存选区为结构 /builder save my_castle

/builder load [名称] 加载保存的结构 /builder load my_castle

/builder show_load_direction <true/false> 显示/隐藏坐标轴指示器 /builder show_load_direction true

高级使用技巧

1. 结构保存：
   • 结构文件保存在 ./structures/ 目录

   • 文件名格式：builder_<名称>_<X坐标>_<Z坐标>.mcstructure

   • 大型结构会自动分割为多个文件

2. 异步操作：
   • 所有结构保存/加载操作在独立线程执行

   • 填充/替换操作分批进行（512/256/128/64/32/16/8/4/2/1块/次）

3. 进度显示：
   • 操作过程中会在聊天栏显示进度百分比

   • 同时会在屏幕上方显示进度条

⚠️ 注意事项

1. 需要 OP 权限才能使用插件命令
2. 大型结构操作可能需要较长时间
3. 确保有足够的磁盘空间保存结构文件
4. 结构加载位置基于玩家当前位置（~X ~ ~Z）

📜 开源协议

本项目采用 LICENSE 开源，欢迎贡献代码和改进建议！

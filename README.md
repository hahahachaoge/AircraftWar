# 🛩️ 飞机大战 — AircraftWar

> 基于 **Java Swing** 的桌面弹幕射击游戏
> 
> 华南师范大学初级软件设计实作课程项目

---

## 📋 目录

1. [项目概述](#1-项目概述)
2. [环境要求与依赖](#2-环境要求与依赖)
3. [项目结构](#3-项目结构)
4. [运行说明](#4-运行说明)
5. [游戏玩法指南](#5-游戏玩法指南)
6. [难度模式详解](#6-难度模式详解)
7. [敌机系统](#7-敌机系统)
8. [道具系统](#8-道具系统)
9. [射击策略系统](#9-射击策略系统)
10. [排行榜系统](#10-排行榜系统)
11. [音频系统](#11-音频系统)
12. [设计模式分析](#12-设计模式分析)
13. [代码架构详述](#13-代码架构详述)
14. [常见问题](#14-常见问题)

---

## 1. 项目概述

### 1.1 游戏简介

**飞机大战** 是一款经典的纵版弹幕射击游戏，玩家操控英雄机在屏幕底部移动，消灭从屏幕上方不断出现的敌机，获取分数和道具，挑战不同难度的关卡，最终冲击排行榜。

### 1.2 核心特性

| 特性 | 说明 |
|------|------|
| **2 种游戏模式** | 单人模式（鼠标操控）、双人合作模式（键盘 + 鼠标） |
| **5 种难度模式** | 入门 → 普通 → 困难 → 专家 → 地狱 |
| **5 种敌机类型** | 普通 (Mob)、精英 (Elite)、精锐 (Veteran)、王牌 (Ace)、Boss |
| **5 种道具** | 回血、火力增强、超级火力、炸弹、冰冻 |
| **3 种射击策略** | 直射 (Straight)、散射 (Scatter)、环形弹幕 (Ring) |
| **动态难度提升** | 随时间自动增加敌机数量、速度、血量 |
| **Boss 战** | 每达到分数阈值触发 Boss 登场，含 3 秒警告倒计时 |
| **教学关** | 新手引导，支持单人和双人教学 |
| **排行榜** | 按难度 + 模式分类存储，支持查看/删除/导出 XLS |
| **漏敌惩罚** | 敌机逃离屏幕底部会扣血和扣分，不同类型惩罚不同 |
| **暂停功能** | 游戏中按 ESC 暂停，可继续或退出 |
| **爆炸特效** | 8 帧爆炸动画 + Boss 大爆炸 + 屏幕震动 |
| **背景音乐与音效** | 使用 Java Sound API (Clip) 播放 WAV 格式音频，零卡顿切换 |

### 1.3 技术栈

- **语言**: Java 11+
- **GUI**: Java Swing (`javax.swing.*`)，CardLayout 卡片式界面
- **音频**: Java Sound API (`javax.sound.sampled`)，所有音频 Clip 预加载
- **持久化**: Java 对象序列化（`.dat` 文件）
- **构建**: IntelliJ IDEA（无 Maven/Gradle，直接基于源码目录编译）

---

## 2. 环境要求与依赖

### 2.1 环境要求

| 项目 | 要求 |
|------|------|
| JDK 版本 | **JDK 11 及以上** |
| 操作系统 | Windows / macOS / Linux |
| 开发工具 | IntelliJ IDEA（推荐）或 Eclipse / VS Code + Java 插件 |
| 屏幕分辨率 | 建议 1024×768 以上 |

### 2.2 文件清单

**音频文件**（位于 `src/videos/`）：

| 文件名 | 类型 | 用途 |
|--------|------|------|
| `bgm.wav` | 背景音乐 | 普通游戏背景音乐 |
| `bgm_boss.wav` | 背景音乐 | Boss 战背景音乐 |
| `bomb_explosion.wav` | 音效 | 爆炸音效 |
| `bullet_hit.wav` | 音效 | 子弹命中音效 |
| `game_over.wav` | 音效 | 游戏结束音效 |
| `get_supply.wav` | 音效 | 拾取道具音效 |

> ⚠️ 音频文件未包含在仓库中，需要自行准备 WAV 格式音频文件放入 `src/videos/` 目录，否则游戏将静音运行。

**图片文件**（位于 `src/images/`）：

| 文件名 | 用途 |
|--------|------|
| `bg.jpg` ~ `bg5.jpg` | 5 种难度的背景图片 |
| `hero.png` | 英雄机（蓝色，单人/玩家1使用） |
| `hero_purple.png` | 英雄机（紫色，双人玩家2使用） |
| `mob.png` | 普通敌机 |
| `elite.png` | 精英敌机 |
| `elitePlus.png` | 精锐敌机 |
| `elitePro.png` | 王牌敌机 |
| `boss.png` | Boss 敌机 |
| `bang.png` | 爆炸动画帧序列图（8帧） |
| `bullet_hero.png` | 英雄机子弹 |
| `bullet_enemy.png` | 敌机子弹 |
| `prop_blood.png` | 回血道具 |
| `prop_bullet.png` | 火力道具 |
| `prop_bulletPlus.png` | 超级火力道具 |
| `prop_bomb.png` | 炸弹道具 |
| `prop_freeze.png` | 冰冻道具 |

**排行榜数据文件**（位于 `data/rank/`）：

| 文件名 | 用途 |
|--------|------|
| `rank_*_single.dat` | 各难度单人模式排行榜 |
| `rank_*_double.dat` | 各难度双人模式排行榜 |

> 💡 `data/rank/` 目录在首次写入记录时自动创建。

---

## 3. 项目结构

```
AircraftWar-main/
├── src/                              # 📂 源代码根目录
│   └── cn/edu/scnu/
│       ├── aircraft/                 # ✈️ 飞机类体系（旧架构）
│       │   ├── AbstractAircraft.java #   飞机抽象基类（血量、射击、策略模式）
│       │   ├── HeroAircraft.java     #   英雄机（单例，支持多实例）
│       │   ├── MobEnemy.java         #   普通敌机（不可射击，不掉道具）
│       │   ├── EliteEnemy.java       #   精英敌机
│       │   ├── VeteranEnemy.java     #   精锐敌机
│       │   ├── AceEnemy.java         #   王牌敌机
│       │   ├── BossEnemy.java        #   Boss 敌机
│       │   ├── EnemyType.java        #   敌机类型枚举（含基础属性）
│       │   └── factory/              # 🏭 敌机工厂
│       │       ├── EnemyFactory.java        #   工厂接口
│       │       ├── MobEnemyFactory.java     #   普通敌机工厂
│       │       ├── EliteEnemyFactory.java   #   精英敌机工厂
│       │       ├── VeteranEnemyFactory.java #   精锐敌机工厂
│       │       ├── AceEnemyFactory.java     #   王牌敌机工厂
│       │       └── BossEnemyFactory.java    #   Boss 敌机工厂
│       ├── application/              # 🖥️ 应用层
│       │   ├── Main.java             #   窗口尺寸常量定义
│       │   ├── MainMenuFrame.java    #   主菜单窗口（CardLayout 卡片管理）
│       │   ├── GameMode.java         #   游戏模式枚举（SINGLE/DOUBLE/TUTORIAL）
│       │   ├── HeroController.java   #   鼠标控制英雄机
│       │   ├── KeyboardController.java # 键盘控制英雄机（WASD，双人模式用）
│       │   ├── ImageManager.java     #   图片资源管理器
│       │   ├── ModePanel.java        #   难度图片按钮面板
│       │   ├── InstructionPanel.java #   游戏说明面板（带滚动条）
│       │   ├── RankingSelectPanel.java # 排行榜难度选择面板
│       │   ├── RankingShowPanel.java # 排行榜数据展示面板（含导出功能）
│       │   ├── RankingBoard.java     #   排行榜调度
│       │   ├── RankingFrame.java     #   排行榜展示窗口
│       │   └── game/                 # 🎮 游戏逻辑
│       │       ├── AbstractGame.java #     游戏基类（模板方法模式，游戏主循环）
│       │       ├── BeginningGame.java#     入门难度
│       │       ├── BasicGame.java    #     普通难度
│       │       ├── IntermendtateGame.java # 困难难度
│       │       ├── AdvancedGame.java #     专家难度
│       │       ├── ExpertGame.java   #     地狱难度
│       │       └── TutorialGame.java #     教学关（单人/双人引导）
│       ├── basic/                    # 🔤 基础抽象
│       │   └── AbstractFlyingObject.java # 飞行对象基类（坐标/速度/碰撞）
│       ├── bullet/                   # 🔫 子弹体系
│       │   ├── BaseBullet.java       #   子弹基类
│       │   ├── HeroBullet.java       #   英雄机子弹
│       │   └── EnemyBullet.java      #   敌机子弹（观察者模式）
│       ├── controller/               # 🎮 控制层
│       │   ├── GameListener.java     #   全局输入监听（键盘 + 鼠标）
│       │   └── GameThread.java       #   游戏主线程桥接
│       ├── element/                  # 🧩 元素实体层
│       │   ├── ElementObj.java       #   游戏元素基类
│       │   ├── Plane.java            #   玩家飞机
│       │   ├── Bullet.java           #   子弹类
│       │   ├── EnemyPlane.java       #   敌机类
│       │   ├── PropEntity.java       #   道具实体类
│       │   ├── EnemyObserver.java    #   观察者接口
│       │   └── EnemyType.java        #   敌机类型枚举
│       ├── game/                     # 🚀 程序入口
│       │   └── GameStart.java        #   唯一启动入口
│       ├── manager/                  # 📦 管理器层
│       │   ├── ElementManager.java   #   元素管理器（单例）
│       │   ├── GameElement.java      #   元素类型枚举
│       │   ├── GameLoad.java         #   资源加载器
│       │   ├── Difficulty.java       #   难度枚举
│       │   ├── GameMode.java         #   模式枚举
│       │   └── PropType.java         #   道具类型常量
│       ├── music/                    # 🎵 音频管理
│       │   └── MusicManager.java     #   音频管理器（单例，Clip预加载）
│       ├── prop/                     # 📦 道具体系（旧架构）
│       │   ├── AbstractProp.java     #   道具抽象基类
│       │   ├── GetProp.java          #   获取道具接口
│       │   ├── PropFactory.java      #   道具简单工厂
│       │   ├── PropType.java         #   道具类型枚举
│       │   ├── PropEffectTimer.java  #   道具效果定时器
│       │   ├── observer/             # 👀 观察者模式
│       │   │   ├── EnemyObserver.java    #   敌机观察者接口
│       │   │   └── ObserverManager.java  #   观察者管理器（单例）
│       │   └── supply/               # 🎁 具体道具
│       │       ├── HpSupply.java     #   回血道具
│       │       ├── FireSupply.java   #   火力道具
│       │       ├── FirePlusSupply.java # 超级火力道具
│       │       ├── BombSupply.java   #   炸弹道具
│       │       └── FrozenSupply.java #   冰冻道具
│       ├── rank/                     # 🏆 排行榜
│       │   ├── Difficulty.java       #   难度枚举
│       │   ├── PlayRecord.java       #   游戏记录（可序列化）
│       │   ├── PlayRecordDao.java    #   DAO 接口
│       │   └── PlayRecordDaoImpl.java#   DAO 实现（自动创建 data 目录）
│       ├── shoot/                    # 🎯 射击策略（策略模式）
│       │   ├── ShootStrategy.java    #   射击策略接口
│       │   ├── StraightShoot.java    #   直射策略
│       │   ├── ScatterShoot.java     #   散射策略
│       │   └── RingShoot.java        #   环形弹幕策略
│       └── show/                     # 🖥️ 视图层
│           ├── GameJFrame.java       #   游戏窗口
│           ├── GameMainJPanel.java   #   游戏渲染面板
│           ├── ModePanel.java        #   模式选择面板
│           ├── InstructionPanel.java #   游戏说明面板
│           ├── NameInputPanel.java   #   游戏结束姓名输入面板
│           ├── RankingSelectPanel.java # 排行榜难度选择
│           └── RankingShowPanel.java # 排行榜数据展示
│
├── images/                           # 🖼️ 图片资源（副本，编译时复制）
├── data/rank/                        # 💾 排行榜数据存储
├── out/                              # 🔨 编译输出
├── .idea/                            # 📁 IntelliJ IDEA 配置
├── AircraftWar-base.iml              # 📁 IntelliJ 模块配置
├── .gitignore                        # 🔒 Git 忽略规则
└── README.md                         # 📝 本文档
```

---

## 4. 运行说明

### 4.1 方式一：IntelliJ IDEA（推荐）

1. **打开项目**：File → Open → 选择项目根目录
2. **配置 JDK**：File → Project Structure → Project SDK → 选择 JDK 11+
3. **运行主类**：找到 `src/cn/edu/scnu/game/GameStart.java`，右键 → Run `GameStart.main()`

### 4.2 方式二：命令行编译运行

```bash
# 1️⃣ 编译所有 Java 源文件
javac -encoding UTF-8 -d out -sourcepath src src/cn/edu/scnu/game/GameStart.java

# 2️⃣ 将图片和音频资源复制到输出目录
# Windows
xcopy /E /I src\images out\images
xcopy /E /I src\videos out\videos

# macOS / Linux
cp -r src/images out/images
cp -r src/videos out/videos

# 3️⃣ 运行游戏
java -Dfile.encoding=UTF-8 -cp "out" cn.edu.scnu.game.GameStart
```

### 4.3 启动效果

启动后在控制台输出：
```
飞机大战启动...
主菜单显示完成
```

随后显示主菜单窗口（1400×800 像素），包含游戏模式选择、难度选择、教学关、排行榜、游戏说明等功能。

---

## 5. 游戏玩法指南

### 5.1 基本操作

| 操作 | 方式 | 说明 |
|------|------|------|
| **单人模式移动** | **鼠标拖拽** | 在游戏画面上拖拽，英雄机跟随鼠标位置移动 |
| **双人玩家1（左）** | **键盘 WASD** | 控制左侧飞机移动 |
| **双人玩家2（右）** | **鼠标拖拽** | 控制右侧飞机移动 |
| 射击 | **自动** | 英雄机会按固定周期自动开火，无需按键 |
| 暂停 | **ESC 键** | 游戏中按 ESC 暂停，显示暂停菜单 |
| 退出 | 暂停菜单或窗口关闭 | 暂停后点击"退出"或直接关闭窗口 |

### 5.2 游戏规则

1. **得分**：击毁敌机获得对应分数
2. **生命值**：英雄机被子弹击中会扣血，所有英雄机死亡时游戏结束
3. **漏敌惩罚**：敌机飞出屏幕底部会扣血扣分，不同类型惩罚不同（见敌机系统）
4. **Boss 战**：达到分数阈值后触发 3 秒 WARNING 倒计时，然后 Boss 登场
5. **难度提升**：游戏过程中会定期自动提升难度（敌机更多、更快、更耐打）
6. **结束登记**：游戏结束后弹出对话框输入玩家名，记录到排行榜

### 5.3 游戏流程

```
主菜单 → 选择模式（单人/双人） → 选择难度 → 游戏开始
                                ↓ 或 →
                       教学关（单人/双人教学）
                                    ↓
                       敌机不断生成并从上方出现
                       英雄机自动射击，躲避敌机和子弹
                                    ↓
                       拾取道具增强能力/恢复生命
                                    ↓
                       达到分数阈值 → WARNING 3秒倒计时 → Boss 登场
                                    ↓
                       击毁 Boss → 大爆炸 + 屏幕震动 + 3个道具掉落
                                    ↓
                       继续积累分数 → 下一只 Boss（分数阈值递增）
                                    ↓
                       所有英雄机被击毁 → 游戏结束
                                    ↓
                       输入玩家名 → 保存记录 → 显示排行榜
```

---

## 6. 难度模式详解

游戏提供 5 种难度等级，难度从低到高排列。

### 6.1 难度总览

| 难度 | 枚举值 | 背景图 |
|------|--------|--------|
| 🌱 **简单** | `BEGINNER` | `bg.jpg` |
| 🌿 **普通** | `BASIC` | `bg2.jpg` |
| 🔥 **困难** | `INTERMEDIATE` | `bg3.jpg` |
| ⚡ **专家** | `ADVANCED` | `bg4.jpg` |
| 💀 **地狱** | `EXPERT` | `bg5.jpg` |

### 6.2 初始化参数

| 参数 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|------|:----:|:----:|:----:|:----:|:----:|
| 英雄机初始生命 | 300 | 500 | 1000 | 1000 | 1000 |
| Boss 生命值 | ❌ 无Boss | 500 | 1000 | 500（递增） | 500（递增） |
| 屏幕最大敌机数 | 5 | 8 | 10 | 12 | 15 |
| 敌机生成周期（帧） | 20 | 20 | 15 | 12 | 10 |
| 英雄射击周期（帧） | 15 | 20 | 15 | 20 | 20 |
| 敌机射击周期（帧） | 20 | 20 | 15 | 12 | 10 |
| 敌机血量倍率 | 0.7× | 1.0× | 1.2× | 1.5× | 1.8× |
| 敌机速度倍率 | 0.8× | 1.0× | 1.2× | 1.5× | 1.8× |
| Boss 出现分数阈值 | ❌ 无 | 100 | 1000 | 500 | 400 |
| 道具掉落概率 | 随机 | 80% | 60% | 40% | 25% |
| 难度升级间隔（帧） | ❌ 无 | 3000 | 1500 | 800 | 500 |

### 6.3 初始敌机类型分布

| 敌机类型 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|----------|:----:|:----:|:----:|:----:|:----:|
| 普通 (Mob) | **70%** | **50%** | **40%** | **30%** | **20%** |
| 精英 (Elite) | 20% | 25% | 25% | 25% | 20% |
| 精锐 (Veteran) | 8% | 15% | 20% | 25% | 25% |
| 王牌 (Ace) | 2% | 10% | 15% | 20% | **35%** |

### 6.4 Boss 战特性

| 特性 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|------|:----:|:----:|:----:|:----:|:----:|
| 有 Boss 战 | ❌ | ✅ | ✅ | ✅ | ✅ |
| 初始 Boss 血量 | — | 500 | 1000 | 500 | 500 |
| Boss 血量变化 | — | 固定 | 固定 | 每次+100 | 每次+200 |
| 下次阈值计算 | — | 当前分×1.5 | 当前分×1.2 | 当前分×1.2 | 当前分×1.5 |
| Boss 掉落道具数 | — | 3个 | 3个 | 3个 | 3个 |

### 6.5 难度升级机制

游戏运行中会定期触发难度升级，各模式效果不同：

| 变化项 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|--------|:----:|:----:|:----:|:----:|:----:|
| 最大敌机数上限 | — | 15 | 15 | 20 | 20 |
| 最大敌机数增幅 | — | +1 | +1 | +1 | +1 |
| 生成周期下限 | — | 10 | 10 | 8 | 8 |
| 周期缩减比例 | — | ×0.9 | ×0.9 | ×0.9 | ×0.9 |
| 血量倍率上限 | — | 2.0 | 2.0 | 3.0 | 2.5 |
| 血量倍率增幅 | — | ×1.1 | ×1.1 | ×1.1 | ×1.1 |
| 速度倍率上限 | — | 2.0 | 2.0 | 3.0 | 2.5 |
| 速度倍率增幅 | — | ×1.1 | ×1.1 | ×1.1 | ×1.1 |

> 💡 简单模式没有 Boss 战和难度升级机制，适合新手熟悉操作。

---

## 7. 敌机系统

### 7.1 敌机类型

| 敌机类型 | 基础 HP | 击毁所需 | 基础分数 | 基础速度 | 可射击 | 掉落道具 | Bomb 反应 | Frozen 反应 |
|----------|:-------:|:--------:|:--------:|:--------:|:------:|:--------:|:---------:|:-----------:|
| 🟢 普通 (Mob) | 10 | 1 发 | 10 | (0, 3) | ❌ | ❌ | 坠毁 | 永久静止 |
| 🔵 精英 (Elite) | 50 | **2 发** | 20 | (0, 5) | ✅ | ✅ | 坠毁 | 静止4秒后恢复 |
| 🟣 精锐 (Veteran) | 50 | **2 发** | 30 | (3, 7) | ✅ | ✅ | 坠毁 | 静止3秒后恢复 |
| 🟡 王牌 (Ace) | 50 | **2 发** | 50 | (5, 9) | ✅ | ✅ | 掉血10点 | 减速50%持续3秒 |
| 🔴 Boss | 100* | 多发 | 100 | (5, 0) | ✅ | ✅ | 无影响 | 无影响 |

> `*` Boss 基础 HP 为 100，实际 HP 由各难度模式设定。

### 7.2 敌机行为

- **所有敌机**：从屏幕上方生成，向下移动，超出屏幕底部则触发漏敌惩罚
- **普通 (Mob)**：最简单的敌机，不会射击，不掉落道具
- **精英 (Elite)**：可以射击，掉落道具
- **精锐 (Veteran)**：可以射击，掉落道具，横向移动（speedX > 0）
- **王牌 (Ace)**：可以射击，掉落道具（类型更丰富），横向移动速度更快
- **Boss**：血量极高，使用环形射击，每次掉落 3 个道具，免疫炸弹和冰冻效果

### 7.3 漏敌惩罚

敌机飞出屏幕底部时触发惩罚，不同类型惩罚不同：

| 敌机类型 | 扣 HP | 扣分 |
|----------|:-----:|:----:|
| 普通 (Mob) | 10 | 5 |
| 精英 (Elite) | 20 | 10 |
| 精锐 (Veteran) | 30 | 15 |
| 王牌 (Ace) | 40 | 20 |
| Boss | 10 | 5 |

> 💡 **策略**：优先消灭高威胁敌机（Ace/T精通），减少漏敌损失。

### 7.4 敌机掉落道具概率

| 道具类型 | Elite | Veteran | Ace | Boss |
|----------|:-----:|:-------:|:---:|:----:|
| 💚 回血 (HP) | 33% | 30% | 30% | 50% |
| 🔥 火力 (FIRE) | 33% | 30% | 30% | 20% |
| 🔥🔥 超级火力 (FIRE+) | 34% | 20% | 20% | 10% |
| 💣 炸弹 (BOMB) | ❌ | 20% | 10% | 10% |
| ❄️ 冰冻 (FROZEN) | ❌ | ❌ | 10% | 10% |

### 7.5 敌机工厂体系

采用 **工厂方法模式**，每种敌机有对应的工厂类：

```
EnemyFactory (接口)
├── MobEnemyFactory    → 创建 MobEnemy
├── EliteEnemyFactory  → 创建 EliteEnemy
├── VeteranEnemyFactory → 创建 VeteranEnemy
├── AceEnemyFactory    → 创建 AceEnemy
└── BossEnemyFactory   → 创建 BossEnemy
```

---

## 8. 道具系统

### 8.1 道具类型

| 道具 | 图标文件 | 效果 | 持续时间 |
|------|----------|------|:--------:|
| 💚 **回血 (HP)** | `prop_blood.png` | 英雄机恢复 20 点生命值（不超过最大生命值） | 瞬时 |
| 🔥 **火力 (FIRE)** | `prop_bullet.png` | 射击策略变为**散射**（5 发子弹） | 2 秒 |
| 🔥🔥 **超级火力 (FIRE+)** | `prop_bulletPlus.png` | 射击策略变为**环形弹幕**（20 发子弹） | 5 秒 |
| 💣 **炸弹 (BOMB)** | `prop_bomb.png` | 所有普通/精英/精锐敌机立即坠毁（王牌掉血），敌机子弹消失 | 瞬时 |
| ❄️ **冰冻 (FROZEN)** | `prop_freeze.png` | 所有敌机和子弹速度减慢或静止（各敌机反应不同） | 各类型不定 |

### 8.2 道具系统架构

道具使用 **简单工厂模式** 创建：

```java
PropFactory.createProp(PropType.HP, x, y);    // 创建回血道具
PropFactory.createProp(PropType.BOMB, x, y);   // 创建炸弹道具
```

### 8.3 炸弹道具机制（观察者模式）

炸弹道具使用观察者模式：

1. 每架敌机创建时自动向 `ObserverManager` 注册为炸弹观察者
2. 拾取炸弹时，`BombSupply.activate()` 通知所有观察者
3. Mob/Elite/Veteran → 坠毁；Ace → 掉血；Boss → 免疫；EnemyBullet → 消失

### 8.4 冰冻道具机制

- Mob → 永久静止
- Elite → 静止 4 秒后恢复
- Veteran → 静止 3 秒后恢复
- Ace → 速度减半，3 秒后恢复
- Boss → 免疫
- EnemyBullet → 静止 5 秒后恢复

### 8.5 火力道具计时器

`FireSupply` 和 `FirePlusSupply` 使用 `PropEffectTimer` 实现定时恢复，在指定秒数后自动将射击策略重置为直射。

---

## 9. 射击策略系统

### 9.1 策略模式

```
ShootStrategy (接口)
├── StraightShoot   — 直射模式（默认，3发子弹排成一条直线）
├── ScatterShoot    — 散射模式（5发子弹呈扇形散开）
└── RingShoot       — 环形弹幕（20发子弹向全方向发射）
```

### 9.2 各策略说明

| 策略 | 子弹数 | 使用场景 |
|------|:------:|----------|
| 直射 | 3 | 默认射击方式，火力道具失效后恢复 |
| 散射 | 5 | 拾取 FireSupply 后持续 2 秒 |
| 环形弹幕 | 20 | 拾取 FirePlusSupply 后持续 5 秒 |

### 9.3 敌机射击特点

| 敌机 | 射击策略 |
|------|----------|
| Mob | 不射击 |
| Elite | 直射 1 发 |
| Veteran | 直射 1 发 |
| Ace | 直射 1 发 |
| Boss | 环形弹幕 |

---

## 10. 排行榜系统

### 10.1 数据模型

```
PlayRecord (实现 Comparable + Serializable)
├── score: int            — 游戏得分
├── name: String          — 玩家名
├── dateTime: LocalDateTime — 记录时间
├── difficulty: Difficulty  — 游戏难度
└── gameMode: GameMode      — 游戏模式（单人/双人）

排序规则: 难度高 > 难度低 → 分数高 > 分数低 → 时间新 > 时间旧
```

### 10.2 存储结构

单人/双人模式使用独立的文件：

| 难度 | 单人模式 | 双人模式 |
|------|----------|----------|
| 简单 | `rank_beginner_single.dat` | `rank_beginner_double.dat` |
| 普通 | `rank_basic_single.dat` | `rank_basic_double.dat` |
| 困难 | `rank_intermediate_single.dat` | `rank_intermediate_double.dat` |
| 专家 | `rank_advanced_single.dat` | `rank_advanced_double.dat` |
| 地狱 | `rank_expert_single.dat` | `rank_expert_double.dat` |

### 10.3 排行榜功能

- ✅ 游戏结束后自动保存记录
- ✅ 按难度分类查看排行榜
- ✅ 单人/双人记录合并显示，表格含"模式"列
- ✅ 支持删除指定记录
- ✅ 支持导出排行榜为 XLS 文件（HTML 表格格式，Excel/WPS 直接打开）

---

## 11. 音频系统

### 11.1 架构

`MusicManager` 单例管理所有音频资源：

- 所有音频（BGM + 音效）启动时全部预加载到 `Clip` 对象，运行时零磁盘读取
- BGM 切换使用 `clip.stop()` + `clip.loop()`，瞬间切换零卡顿
- 短音效使用 `clip.setFramePosition(0)` + `clip.start()`，非阻塞

### 11.2 音频类型

| 类型 | 文件 | 播放方式 |
|------|------|:--------:|
| 普通 BGM | `bgm.wav` | 预加载 Clip 循环 |
| Boss BGM | `bgm_boss.wav` | 预加载 Clip 循环 |
| 爆炸音效 | `bomb_explosion.wav` | 预加载 Clip |
| 命中音效 | `bullet_hit.wav` | 预加载 Clip |
| 游戏结束 | `game_over.wav` | 预加载 Clip |
| 拾取道具 | `get_supply.wav` | 预加载 Clip |

### 11.3 音量控制

- 背景音乐：+6.0 dB
- 短音效：-8.0 dB
- 爆炸音效：-20.0 dB（防止过于刺耳）

---

## 12. 设计模式分析

| 模式 | 使用位置 | 说明 |
|------|----------|------|
| **单例模式** | `HeroAircraft`、`MusicManager`、`ObserverManager`、`ElementManager` | 确保全局唯一实例 |
| **模板方法** | `AbstractGame` | `action()` 定义游戏循环骨架，抽象方法由子类实现 |
| **工厂方法** | `EnemyFactory` 及其子类 | 每种敌机对应一个工厂类 |
| **简单工厂** | `PropFactory` | 根据 `PropType` 创建道具 |
| **策略模式** | `ShootStrategy` 及其实现 | 直射/散射/环形弹幕可动态切换 |
| **观察者模式** | `ObserverManager` + `EnemyObserver` | 炸弹/冰冻道具通知所有敌机 |
| **DAO 模式** | `PlayRecordDao` + `PlayRecordDaoImpl` | 排行榜数据持久化与业务分离 |

---

## 13. 代码架构详述

### 13.1 核心类继承关系

```
AbstractFlyingObject (坐标 / 速度 / 碰撞检测 / 图片)
├── BaseBullet (子弹动力 / 边界判定)
│   ├── HeroBullet       — 英雄机子弹
│   └── EnemyBullet      — 敌机子弹（实现 EnemyObserver）
├── AbstractAircraft (血量 / 射击 / 道具掉落)
│   ├── HeroAircraft     — 英雄机（支持多实例）
│   ├── MobEnemy         — 普通敌机
│   ├── EliteEnemy       — 精英敌机
│   ├── VeteranEnemy     — 精锐敌机
│   ├── AceEnemy         — 王牌敌机
│   └── BossEnemy        — Boss 敌机
└── AbstractProp (道具 / 向下移动)
    ├── HpSupply         — 回血
    ├── FireSupply       — 散射火力
    ├── FirePlusSupply   — 环形弹幕
    ├── BombSupply       — 炸弹
    └── FrozenSupply     — 冰冻
```

### 13.2 游戏循环（主循环）

位于 `AbstractGame.action()` 中，由 `java.util.Timer` 调度，每 50ms 执行一次：

```
① paused? → 暂停状态
② gameTime++
③ shouldLevelUp()? → difficultyLevelUp()
④ printInfo() (控制台输出)
⑤ createRandomEnemy() (敌机生成)
⑥ Boss 警告 & 生成控制
⑦ shootAction() (英雄射击 + 敌机射击)
⑧ updateKeyboardHeroes() (双人键盘控制)
⑨ bulletsMoveAction() (子弹移动)
⑩ aircraftsMoveAction() (飞机移动)
⑪ checkEscapedEnemies() (漏敌惩罚)
⑫ propMoveAction() (道具移动)
⑬ crashCheckAction() (碰撞检测)
⑭ advanceExplosions() (爆炸动画推进)
⑮ postProcessAction() (清理死亡对象)
⑯ repaint() (重绘)
⑰ checkResultAction() (游戏结束判定)
```

### 13.3 碰撞检测

`AbstractFlyingObject.crash()` 使用矩形重叠检测，碰撞检测包括：

1. **敌机子弹 → 英雄机**：扣血，子弹消失
2. **英雄机子弹 → 敌机**：扣血，得分，爆炸特效，概率掉落道具
3. **英雄机 ↔ 敌机 相撞**：英雄立即死亡，游戏结束
4. **英雄机 → 道具**：触发道具效果

### 13.4 爆炸动画系统

- 8帧爆炸动画（`bang.png` 528×66，每帧 66×66）
- Boss 死亡触发大爆炸：25个炸点 + 慢放 + 屏幕震动（8px强度，1.5秒）
- 漏敌、碰撞、游戏结束时均触发对应爆炸效果

---

## 14. 常见问题

### 14.1 游戏无法启动

**排查步骤**：
1. 确认 JDK 版本 ≥ 11
2. 确认 `src/images/` 目录下有图片文件
3. 如是命令行运行，确保资源文件已复制到输出目录

### 14.2 没有声音/音乐

音频 WAV 文件未放入 `src/videos/` 目录。放入后重启游戏即可。

### 14.3 排行榜数据文件找不到

首次运行时 `data/rank/` 目录不存在。`PlayRecordDaoImpl` 在写入时会自动调用 `mkdirs()` 创建目录。

### 14.4 游戏卡顿

- Boss 音乐卡顿：V2.0 已修复（改用 Clip 预加载，零阻塞）
- 图片分辨率建议背景图 ≤ 512×768，飞机/道具图 ≤ 64×64
- 音频建议使用 16-bit PCM WAV

### 14.5 源码编码问题

如果是用 VS Code 或 IDEA 打开后中文乱码，请将编辑器编码设置为 UTF-8。

---

## 附录

### A. 快捷键一览

| 快捷键 | 功能 |
|--------|------|
| 鼠标拖拽 | 移动英雄机（单人/双人玩家2） |
| WASD 键盘 | 移动英雄机（双人玩家1） |
| ESC | 暂停/继续游戏 |

### B. 版本信息

| 项目 | 版本 |
|------|------|
| 项目版本 | **V2.2** |
| JDK | 11+ |
| 最后更新 | 2026-07-15 |
| 作者 | 黄彪骐、岳孝彬、丁俊哲 |

### C. 更新日志

| 版本 | 内容 |
|------|------|
| V2.2 | 重构项目架构，新增 element/controller/manager/show 包（老师框架扩展）；统一唯一入口 GameStart；完善所有文件注释；修复 switch 穿透 Bug；新增爆炸特效、屏幕震动、Boss 警告倒计时；优化漏敌惩罚机制 |
| V2.1 | 补充完整注释和功能说明 |
| V2.0 | 完善系统：BGM 零卡顿切换、排行榜导出 XLS、暂停菜单、教学关 |
| V1.9 | 双人模式紫色战机 |
| V1.8 | 增加爆炸特效 |
| V1.7 | 优化漏敌逻辑、UI界面美观、导出排行榜、更新规则 |
| V1.6 | 整合 111 分支 UI 优化，CardLayout 卡片式界面 |
| V1.5 | 修复 Random 种子问题 |
| V1.4 | 完善道具系统、Boss 战、双人模式、排行榜、音效 |
| V1.3 | 增加四种敌机类型 |
| V1.2 | 增加 5 种难度模式 |
| V1.1 | 完善游戏框架 |
| V1.0 | 基本框架 |

---

> 🎮 **祝您游戏愉快！**

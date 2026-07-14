# 🛩️ 飞机大战 — AircraftWar

> 基于 **Java Swing** 的桌面弹幕射击游戏
> 华南师范大学 课程项目

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
| **5 种难度模式** | 入门 → 普通 → 困难 → 专家 → 地狱 |
| **5 种敌机类型** | 普通 (Mob)、精英 (Elite)、精锐 (Veteran)、王牌 (Ace)、Boss |
| **5 种道具** | 回血、火力增强、超级火力、炸弹、冰冻 |
| **3 种射击策略** | 直射 (Straight)、散射 (Scatter)、环形弹幕 (Ring) |
| **动态难度提升** | 随时间自动增加敌机数量、速度、血量 |
| **Boss 战** | 每达到分数阈值触发 Boss 登场 |
| **排行榜** | 按难度分类存储，支持查看和删除记录 |
| **背景音乐与音效** | 使用 Java Sound API 播放 WAV 格式音频 |

### 1.3 技术栈

- **语言**: Java 11+
- **GUI**: Java Swing (`javax.swing.*`)
- **音频**: Java Sound API (`javax.sound.sampled`)
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
| `hero.png` | 英雄机 |
| `mob.png` | 普通敌机 |
| `elite.png` | 精英敌机 |
| `elitePlus.png` | 精锐敌机 |
| `elitePro.png` | 王牌敌机 |
| `boss.png` | Boss 敌机 |
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
| `rank_beginner.dat` | 入门难度排行榜 |
| `rank_basic.dat` | 普通难度排行榜 |

> ⚠️ `intermediate`、`advanced`、`expert` 三个难度对应的 `.dat` 文件在首次游戏后会自动生成。

---

## 3. 项目结构

```
AircraftWar-main/
├── src/                              # 📂 源代码根目录
│   └── cn/edu/scnu/
│       ├── aircraft/                 # ✈️ 飞机类体系
│       │   ├── AbstractAircraft.java #   飞机抽象基类
│       │   ├── HeroAircraft.java     #   英雄机（单例模式）
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
│       │   ├── Main.java             #   程序入口
│       │   ├── MainMenuFrame.java    #   主菜单窗口
│       │   ├── ImageManager.java     #   图片资源管理器
│       │   ├── HeroController.java   #   鼠标控制英雄机
│       │   ├── RankingBoard.java     #   排行榜调度
│       │   ├── RankingFrame.java     #   排行榜展示窗口
│       │   └── game/                 # 🎮 游戏逻辑
│       │       ├── AbstractGame.java #     游戏基类（模板方法模式）
│       │       ├── BeginningGame.java#     入门难度
│       │       ├── BasicGame.java    #     普通难度
│       │       ├── IntermendtateGame.java # 困难难度
│       │       ├── AdvancedGame.java #     专家难度
│       │       └── ExpertGame.java   #     地狱难度
│       ├── basic/                    # 🔤 基础抽象
│       │   └── AbstractFlyingObject.java # 飞行对象基类
│       ├── bullet/                   # 🔫 子弹体系
│       │   ├── BaseBullet.java       #   子弹基类
│       │   ├── HeroBullet.java       #   英雄机子弹
│       │   └── EnemyBullet.java      #   敌机子弹（观察者模式）
│       ├── shoot/                    # 🎯 射击策略
│       │   ├── ShootStrategy.java    #   射击策略接口
│       │   ├── StraightShoot.java    #   直射策略
│       │   ├── ScatterShoot.java     #   散射策略
│       │   └── RingShoot.java        #   环形弹幕策略
│       ├── prop/                     # 📦 道具体系
│       │   ├── AbstractProp.java     #   道具抽象基类
│       │   ├── GetProp.java          #   获取道具接口
│       │   ├── PropFactory.java      #   道具简单工厂
│       │   ├── PropType.java         #   道具类型枚举
│       │   ├── PropEffectTimer.java  #   道具效果定时器
│       │   ├── observer/             # 👀 观察者模式
│       │   │   ├── EnemyObserver.java   #   敌机观察者接口
│       │   │   └── ObserverManager.java #   观察者管理器（单例）
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
│       │   └── PlayRecordDaoImpl.java#   DAO 实现
│       └── music/                    # 🎵 音频管理
│           ├── MusicManager.java     #   音频管理器（单例）
│           └── MusicThread.java      #   音频播放线程
│
├── images/                           # 🖼️ 图片资源（副本，编译时复制）
├── data/rank/                        # 💾 排行榜数据存储
│   ├── rank_beginner.dat
│   └── rank_basic.dat
├── out/                              # 🔨 编译输出
├── .idea/                            # 📁 IntelliJ IDEA 配置
├── AircraftWar-base.iml              # 📁 IntelliJ 模块配置
└── PROJECT_DOCUMENTATION.md          # 📝 本文档
```

---

## 4. 运行说明

### 4.1 方式一：IntelliJ IDEA（推荐）

1. **打开项目**：File → Open → 选择项目根目录 `AircraftWar-main`
2. **配置 JDK**：File → Project Structure → Project SDK → 选择 JDK 11+
3. **运行主类**：找到 `src/cn/edu/scnu/application/Main.java`，右键 → Run `Main.main()`

### 4.2 方式二：命令行编译运行

建议使用项目根目录的 `run.bat` 脚本一键编译运行（自动处理 UTF-8 编码）。

也可手动执行：

```bash
# 1️⃣ 编译所有 Java 源文件
javac -encoding UTF-8 -d out -sourcepath src src/cn/edu/scnu/application/Main.java

# 2️⃣ 将图片资源复制到输出目录
# Windows
xcopy /E /I src\images out\images
xcopy /E /I src\videos out\videos

# macOS / Linux
cp -r src/images out/images
cp -r src/videos out/videos

# 3️⃣ 运行游戏
# Windows
java -Dfile.encoding=UTF-8 -cp "out" cn.edu.scnu.application.Main

# macOS / Linux
java -Dfile.encoding=UTF-8 -cp "out" cn.edu.scnu.application.Main
```

### 4.3 方式三：使用 VS Code

1. 安装 **Extension Pack for Java**（Microsoft 官方）
2. 打开项目根目录
3. 确保 `.vscode/settings.json` 已正确配置
4. 打开 `Main.java`，点击 "Run" 按钮

### 4.4 启动后效果

启动后出现 **主菜单窗口**（400×500 像素），包含：
- 游戏标题 "飞机大战"
- 5 个难度按钮（简单～地狱）
- 排行榜按钮
- 退出游戏按钮

---

## 5. 游戏玩法指南

### 5.1 基本操作

| 操作 | 方式 | 说明 |
|------|------|------|
| 移动 | **鼠标拖拽** | 在游戏画面上按住并拖动鼠标，英雄机跟随鼠标位置移动 |
| 射击 | **自动** | 英雄机会按固定周期自动开火，无需按键 |
| 暂停 | 不支持 | 当前版本无暂停功能 |
| 退出 | 窗口关闭 | 关闭窗口或击毁后选择退出 |

### 5.2 游戏规则

1. **得分**：击毁敌机获得对应分数
2. **生命值**：英雄机被子弹击中会扣血，血量为 0 时游戏结束
3. **Boss 战**：当累计分数达到阈值时，Boss 敌机登场
4. **难度提升**：游戏过程中会定期自动提升难度（敌机更多、更快、更耐打）
5. **结束登记**：游戏结束后会弹出对话框输入玩家名，记录到排行榜

### 5.3 游戏界面

游戏窗口大小固定为 **512×768** 像素，界面上包含：
- **顶部**：SCORE（当前得分）、LIFE（英雄机剩余生命值）
- **中部**：游戏区域（背景自动向下滚动，营造飞行效果）
- **底部**：英雄机初始位置

### 5.4 游戏流程

```
主菜单 → 选择难度 → 游戏开始（背景音乐启动）
                            ↓
                   敌机不断生成并从上方出现
                            ↓
                   英雄机自动射击，躲避敌机和子弹
                            ↓
                   拾取道具增强能力/恢复生命
                            ↓
                   达到分数阈值 → Boss 登场
                            ↓
                  击毁 Boss / 继续积累分数 → 下一只 Boss
                            ↓
                  英雄机被击毁（HP ≤ 0）→ 游戏结束
                            ↓
                  输入玩家名 → 保存记录 → 显示排行榜
```

---

## 6. 难度模式详解

游戏提供 5 种难度等级，难度从低到高排列：

### 6.1 难度总览

| 难度 | 枚举值 | 背景图 | 初始化游戏难度参数对比 |
|------|--------|--------|----------------------|
| 🌱 **简单** | `BEGINNER` | `bg.jpg` | 最低 |
| 🌿 **普通** | `BASIC` | `bg2.jpg` | 中等 |
| 🔥 **困难** | `INTERMEDIATE` | `bg3.jpg` | 较高 |
| ⚡ **专家** | `ADVANCED` | `bg4.jpg` | 高 |
| 💀 **地狱** | `EXPERT` | `bg5.jpg` | 最高 |

### 6.2 详细参数对比

#### 初始化参数

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
| 子弹周期（帧=50ms） | 20帧=1s | 20帧=1s | 15帧=0.75s | 12帧=0.6s | 10帧=0.5s |

#### 初始敌机类型分布

| 敌机类型 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|----------|:----:|:----:|:----:|:----:|:----:|
| 普通 (Mob) | **70%** | **50%** | **40%** | **30%** | **20%** |
| 精英 (Elite) | 20% | 25% | 25% | 25% | 20% |
| 精锐 (Veteran) | 8% | 15% | 20% | 25% | 25% |
| 王牌 (Ace) | 2% | 10% | 15% | 20% | **35%** |

#### Boss 战特性

| 特性 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|------|:----:|:----:|:----:|:----:|:----:|
| 有 Boss 战 | ❌ | ✅ | ✅ | ✅ | ✅ |
| 初始 Boss 血量 | — | 500 | 1000 | 500 | 500 |
| Boss 血量变化 | — | 固定 | 固定 | 每次+100 | 每次+200 |
| 下次阈值计算 | — | 当前分×1.5 | 当前分×1.2 | 当前分×1.2 | 当前分×1.5 |
| Boss 掉落道具数 | — | 3个 | 3个 | 3个 | 3个 |

### 6.3 难度升级机制（动态提升）

游戏运行中会定期触发难度升级（`difficultyLevelUp()`），各模式效果不同：

| 变化项 | 简单 | 普通 | 困难 | 专家 | 地狱 |
|--------|:----:|:----:|:----:|:----:|:----:|
| 最大敌机数上限 | — | 15 | 15 | 20 | 20 |
| 最大敌机数增幅 | — | +1 | +1 | +1 | +1 |
| 敌机生成周期下限 | — | 10 | 10 | 8 | 8 |
| 周期缩减比例 | — | ×0.9 | ×0.9 | ×0.9 | ×0.9 |
| 血量倍率上限 | — | 2.0 | 2.0 | 3.0 | 2.5 |
| 血量倍率增幅 | — | ×1.1 | ×1.1 | ×1.1 | ×1.1 |
| 速度倍率上限 | — | 2.0 | 2.0 | 3.0 | 2.5 |
| 速度倍率增幅 | — | ×1.1 | ×1.1 | ×1.1 | ×1.1 |
| 英雄射击周期影响 | — | 无 | 无 | 增加 | 大幅增加 |

> 💡 **提示**：简单模式 **没有** Boss 战和难度升级机制，适合新手熟悉操作。

---

## 7. 敌机系统

### 7.1 敌机类型

| 敌机类型 | 基础 HP | 基础分数 | 基础速度(X,Y) | 是否可射击 | Bo m b 反应 | Frozen 反应 |
|----------|:-------:|:--------:|:--------------:|:----------:|:-----------:|:-----------:|
| 🟢 普通 (Mob) | 10 | 10 | (0, 3) | ❌ | 坠毁 | 永久静止 |
| 🔵 精英 (Elite) | 20 | 20 | (0, 5) | ✅ | 坠毁 | 静止4秒后恢复 |
| 🟣 精锐 (Veteran) | 30 | 30 | (3, 7) | ✅ | 坠毁 | 静止3秒后恢复 |
| 🟡 王牌 (Ace) | 50 | 50 | (5, 9) | ✅ | 掉血10点 | 减速50%持续3秒 |
| 🔴 Boss | 100* | 100 | (5, 0) | ✅ | 无影响 | 无影响 |

> `*` Boss 基础 HP 为 100，实际 HP 由各难度模式通过 `setBossEnemyHp()` 方法设定（见 6.2 节）。

### 7.2 敌机行为

- **所有敌机**：从屏幕上方生成，向下移动，超出屏幕底部则消失
- **普通 (Mob)**：最简单的敌机，不会射击，不掉落道具
- **精英 (Elite)**：可以射击，掉落道具（概率受难度参数影响）
- **精锐 (Veteran)**：可以射击，掉落道具，横向移动（speedX > 0）
- **王牌 (Ace)**：可以射击，掉落道具（类型更丰富），横向移动速度更快
- **Boss**：血量极高，使用环形射击，每次掉落 3 个道具，免疫炸弹和冰冻效果

### 7.3 敌机掉落道具概率与类型

各敌机掉落道具时，首先根据难度参数 `propRand` 决定是否掉落，再随机选择道具类型。

| 道具类型 | Elite | Veteran | Ace | Boss |
|----------|:-----:|:-------:|:---:|:----:|
| 💚 回血 (HP) | 33% | 30% | 30% | 50% |
| 🔥 火力 (FIRE) | 33% | 30% | 30% | 20% |
| 🔥🔥 超级火力 (FIRE+) | 34% | 20% | 20% | 10% |
| 💣 炸弹 (BOMB) | ❌ | 20% | 10% | 10% |
| ❄️ 冰冻 (FROZEN) | ❌ | ❌ | 10% | 10% |

> Boss 掉落时必定掉落（概率 100%），且 Boss 被击毁时一次性掉落 3 个道具。
> 普通敌机 (Mob) 不掉落任何道具。

### 7.4 敌机工厂体系

采用 **工厂方法模式**，每种敌机有对应的工厂类：

```
EnemyFactory (接口)
├── MobEnemyFactory    → 创建 MobEnemy
├── EliteEnemyFactory  → 创建 EliteEnemy
├── VeteranEnemyFactory → 创建 VeteranEnemy
├── AceEnemyFactory    → 创建 AceEnemy
└── BossEnemyFactory   → 创建 BossEnemy
```

工厂负责：
- 接收敌机血量倍率 (`enemyHpFactor`) 和速度倍率 (`enemySpeedFactor`)
- 创建对应类型的敌机实例，应用倍率参数
- 受难度升级影响，倍率会随时间动态变化

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
PropFactory.createProp(PropType.HP, x, y);  // 创建回血道具
PropFactory.createProp(PropType.BOMB, x, y); // 创建炸弹道具
```

### 8.3 炸弹道具机制（观察者模式）

炸弹道具是观察者模式的典型应用：

1. 每架敌机在创建时，自动向 `ObserverManager` 注册为 **炸弹观察者**
2. 当玩家拾取炸弹道具时，`BombSupply.activate()` 调用 `observerManager.notifyObservers(PropType.BOMB)`
3. 各观察者根据自身类型做出不同响应：
   - MobEnemy → 直接坠毁
   - EliteEnemy → 直接坠毁
   - VeteranEnemy → 直接坠毁
   - AceEnemy → 减少 10 点 HP（可能存活）
   - BossEnemy → **不受影响**
   - EnemyBullet → 直接消失

### 8.4 冰冻道具机制

与炸弹类似，使用观察者模式通知所有敌机：

- MobEnemy → 永久静止（速度设为 0）
- EliteEnemy → 静止 4 秒后恢复
- VeteranEnemy → 静止 3 秒后恢复
- AceEnemy → 速度减半，3 秒后恢复
- BossEnemy → **不受影响**
- EnemyBullet → 静止 5 秒后恢复

> 若已处于冰冻状态再次拾取冰冻道具，会刷新恢复计时器。

### 8.5 火力道具计时器

`FireSupply` 和 `FirePlusSupply` 使用 `PropEffectTimer` 实现定时恢复：
- 启动一个新线程，在指定秒数后自动将英雄机射击策略重置为 **直射（3 发）**
- 若在持续时间内再次拾取同类道具，会取消旧计时器并启动新计时

---

## 9. 射击策略系统

### 9.1 策略模式

射击系统使用 **策略模式**，`ShootStrategy` 接口定义了射击算法，运行时可以切换不同的策略。

```
ShootStrategy (接口)
├── StraightShoot   — 直射模式（默认，3发子弹排成一条直线）
├── ScatterShoot    — 散射模式（5发子弹呈扇形散开）
└── RingShoot       — 环形弹幕（20发子弹向全方向发射）
```

### 9.2 各策略详解

#### 直射 (Straight) — 默认
```
   |   |   |
   ↑   ↑   ↑    ← 3 发子弹垂直向上
  [英雄机]
```
- 使用场景：默认射击方式，火力道具失效后恢复
- 子弹方向：向上（hero）或向下（enemy）

#### 散射 (Scatter) — 火力道具
```
  \ | /
   ↑↑↑↑↑
   ↑↑↑↑↑    ← 5 发子弹呈散射状
  [英雄机]
```
- 使用场景：拾取 FireSupply 后持续 2 秒
- 子弹数量：5 发
- 子弹有水平偏移分量，形成扩散效果

#### 环形弹幕 (Ring) — 超级火力
```
      ↑
   ↖ ↑ ↗
 ← [英雄机] →
   ↙ ↓ ↘
      ↓        ← 20 发子弹向全方向发射（360°）
```
- 使用场景：拾取 FirePlusSupply 后持续 5 秒
- 子弹数量：20 发
- 全方向发射，是游戏中最强的射击模式

### 9.3 敌机射击特点

| 敌机 | 射击策略 | 说明 |
|------|----------|------|
| Mob | 不射击 | 仅作为移动靶 |
| Elite | 直射 | 1 发子弹 |
| Veteran | 直射 | 1 发子弹 |
| Ace | 直射 | 1 发子弹 |
| Boss | **环形弹幕** | 向下方发射扇形弹幕 |

---

## 10. 排行榜系统

### 10.1 数据模型

排行榜使用 **DAO (Data Access Object) 模式**，数据通过 Java 序列化持久化到本地文件。

```
PlayRecord (实现 Comparable + Serializable)
├── score: int            — 游戏得分
├── name: String          — 玩家名
├── dateTime: LocalDateTime — 记录时间
└── difficulty: Difficulty  — 游戏难度

排序规则: 难度高 > 难度低 → 分数高 > 分数低 → 时间新 > 时间旧
```

### 10.2 存储结构

每个难度对应一个独立的 `.dat` 文件：

| 难度 | 文件路径 |
|------|----------|
| 简单 | `data/rank/rank_beginner.dat` |
| 普通 | `data/rank/rank_basic.dat` |
| 困难 | `data/rank/rank_intermediate.dat` |
| 专家 | `data/rank/rank_advanced.dat` |
| 地狱 | `data/rank/rank_expert.dat` |

### 10.3 排行榜功能

- ✅ 游戏结束后自动保存记录
- ✅ 按难度分类查看排行榜
- ✅ 表格形式展示（排名、玩家名、分数、记录时间）
- ✅ 支持选中并删除指定记录
- ✅ 关闭排行榜后返回主菜单

---

## 11. 音频系统

### 11.1 架构

音频系统使用 **`MusicManager`** 单例管理所有音频资源：

- **背景音乐**（BGM）：使用独立的 `MusicThread` 线程播放，支持循环播放
- **音效**（SFX）：预加载到 `Clip` 对象，通过 4 线程池播放，响应快速无延迟

### 11.2 音频类型

| 类型 | 文件 | 播放方式 | 说明 |
|------|------|:--------:|------|
| 普通 BGM | `bgm.wav` | 循环线程 | 游戏过程中持续播放 |
| Boss BGM | `bgm_boss.wav` | 循环线程 | Boss 登场时切换 |
| 爆炸音效 | `bomb_explosion.wav` | 预加载 Clip | 敌机被击毁时播放 |
| 命中音效 | `bullet_hit.wav` | 预加载 Clip | 子弹命中时播放 |
| 游戏结束 | `game_over.wav` | 预加载 Clip | 英雄机被击毁时播放 |
| 拾取道具 | `get_supply.wav` | 预加载 Clip | 拾取道具时播放 |

### 11.3 音量控制

- 背景音乐：+6.0 dB 增益（稍大音量）
- 短音效：-8.0 dB 增益（爆炸音效 -20.0 dB 以防过于刺耳）

---

## 12. 设计模式分析

本项目使用了多种经典设计模式，是面向对象编程的良好范例。

### 12.1 单例模式 (Singleton)

**使用位置**：
- `HeroAircraft` — 游戏中只有一个英雄机实例
- `MusicManager` — 全局只用一个音频管理器
- `ObserverManager` — 全局共享的观察者注册中心

**实现方式**：双检锁（Double-Checked Locking）

```java
private static volatile HeroAircraft instance = null;
public static HeroAircraft getInstance(...) {
    if (instance == null) {
        synchronized (HeroAircraft.class) {
            if (instance == null) {
                instance = new HeroAircraft(...);
            }
        }
    }
    return instance;
}
```

### 12.2 模板方法模式 (Template Method)

**使用位置**：`AbstractGame` 游戏基类

**体现**：
- `action()` 方法定义了游戏循环的骨架（生成敌机 → 射击 → 移动 → 碰撞检测 → 重绘 → 结束检查）
- `initGameSettings()`、`getRandomEnemyType()`、`shouldSpawnBoss()`、`triggerProp()` 等抽象方法由子类实现
- `spawnBossEnemy()` 中调用了 `setBossEnemyHp()` 钩子方法

### 12.3 工厂方法模式 (Factory Method)

**使用位置**：敌机创建体系

**体现**：
- `EnemyFactory` 接口定义了 `createEnemy()` 方法
- 每种敌机有各自的工厂实现
- `AbstractGame` 中通过 `Map<EnemyType, EnemyFactory>` 管理所有工厂

### 12.4 简单工厂模式 (Simple Factory)

**使用位置**：`PropFactory`

**体现**：根据 `PropType` 枚举值创建不同种类的道具对象

### 12.5 策略模式 (Strategy)

**使用位置**：射击系统

**体现**：
- `ShootStrategy` 接口定义了射击算法
- `StraightShoot`、`ScatterShoot`、`RingShoot` 实现不同算法
- 运行时可以动态切换策略（通过 `setShootStrategy()`）

### 12.6 观察者模式 (Observer)

**使用位置**：炸弹和冰冻道具效果

**体现**：
- `EnemyObserver` 接口定义了 `onBombActivated()` 和 `onFrozenActivated()` 方法
- 所有敌机和敌机子弹实现此接口
- `ObserverManager` 作为事件总线，管理观察者的注册和通知
- 炸弹/冰冻道具触发时统一通知所有观察者

### 12.7 DAO 模式 (Data Access Object)

**使用位置**：排行榜数据层

**体现**：
- `PlayRecordDao` 接口定义了 CRUD 操作
- `PlayRecordDaoImpl` 实现了基于序列化文件的数据持久化
- 业务逻辑（`RankingBoard`）与数据存取分离

### 12.8 设计模式总结图

```
┌─────────────────────────────────────────────────────┐
│                    AbstractGame                      │
│                   (Template Method)                  │
├─────────────────────────────────────────────────────┤
│  action() { … }    ← 游戏主循环（模板方法）           │
│  initGameSettings() ← 抽象方法，子类实现              │
│  spawnBossEnemy()  ← 具体方法 + 钩子(setBossEnemyHp)│
└───────┬─────────────────────────────────────────────┘
        │ 组合
        ├── EnemyFactory (Factory Method) → 创建敌机
        ├── ShootStrategy (Strategy)      → 射击算法
        ├── ObserverManager (Singleton + Observer)
        │       ├── BombSupply → notify BOMB 观察者
        │       └── FrozenSupply → notify FROZEN 观察者
        ├── PropFactory (Simple Factory) → 创建道具
        └── HeroAircraft (Singleton)
```

---

## 13. 代码架构详述

### 13.1 核心类继承关系

#### 飞行对象体系
```
AbstractFlyingObject (坐标 / 速度 / 碰撞检测 / 图片)
├── BaseBullet (子弹动力 / 边界判定)
│   ├── HeroBullet       — 英雄机子弹
│   └── EnemyBullet      — 敌机子弹（实现 EnemyObserver）
├── AbstractAircraft (血量 / 射击 / 道具掉落)
│   ├── HeroAircraft     — 英雄机（单例，鼠标控制）
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

#### 游戏类体系
```
AbstractGame (模板方法 / 游戏循环)
├── BeginningGame    — 简单模式（无Boss / 无难度升级）
├── BasicGame        — 普通模式
├── IntermendtateGame — 困难模式
├── AdvancedGame     — 专家模式
└── ExpertGame       — 地狱模式
```

### 13.2 游戏循环（主循环）

位于 `AbstractGame.action()` 方法中，由 `java.util.Timer` 调度，每 **50ms** 执行一次：

```
① gameTime++ (帧计数)
② shouldLevelUp()? → difficultyLevelUp() (难度升级检查)
③ printInfo() (控制台信息输出)
④ createRandomEnemy() (敌机生成)
⑤ shouldSpawnBoss()? → spawnBossEnemy() (Boss检查)
⑥ shootAction() (射击)
   ├── heroShoot()   — 英雄机自动射击
   └── enemyShoot()  — 所有敌机射击
⑦ bulletsMoveAction() (子弹移动)
⑧ aircraftsMoveAction() (飞机移动)
⑨ propMoveAction() (道具移动)
⑩ crashCheckAction() (碰撞检测)
   ├── 敌机子弹 → 英雄机
   ├── 英雄机子弹 → 敌机
   ├── 英雄机 ↔ 敌机 相撞
   └── 英雄机 → 道具
⑪ postProcessAction() (清理无效对象)
⑫ repaint() (重绘界面)
⑬ checkResultAction() (游戏结束判定)
```

### 13.3 碰撞检测机制

`AbstractFlyingObject.crash()` 使用 **矩形重叠检测**：

- 检测范围：横向 `[x - width/2, x + width/2]`
- 检测范围：纵向 `[y - height/(2*factor), y + height/(2*factor)]`
  - 飞机对象的 `factor = 2`（纵向检测范围缩小，更宽松）
  - 子弹/道具对象的 `factor = 1`（标准检测范围）

### 13.4 图片管理

`ImageManager` 使用 **静态代码块** 在类加载时一次性加载所有图片资源，建立类名到图片的映射关系：

```java
// 通过对象实例获取图片
BufferedImage img = ImageManager.get(someObject);
// 内部机制: CLASSNAME_IMAGE_MAP.get(obj.getClass().getName())
```

这种方式支持多态：传入任意子类对象，都能自动获取对应类型的图片。

---

## 14. 常见问题

### 14.1 游戏无法启动

**现象**：点击运行后无反应或报错

**排查步骤**：
1. 确认 JDK 版本 ≥ 11
2. 如果编译报错，检查源码编码是否为 UTF-8
3. 确认 `src/images/` 目录下有图片文件
4. 如果是通过命令行运行，确保资源文件已复制到输出目录

### 14.2 没有声音/音乐

**原因**：音频 WAV 文件未放入 `src/videos/` 目录

**解决**：在 `src/videos/` 下放入以下 WAV 文件：
- `bgm.wav` — 游戏背景音乐
- `bgm_boss.wav` — Boss 背景音乐
- `bomb_explosion.wav` — 爆炸音效
- `bullet_hit.wav` — 命中音效
- `game_over.wav` — 游戏结束音效
- `get_supply.wav` — 拾取道具音效

> 若不想使用音频，可以注释掉 `AbstractGame` 构造器中的 `musicManager.playBgmMusic(...)` 调用。

### 14.3 排行榜数据文件找不到

游戏首次运行时，只有 `beginner` 和 `basic` 两个难度的 `.dat` 文件。其他难度的文件会在首次游戏并记录分数后自动生成。

若想手动初始化，可以创建一个空的有效序列化文件，或直接运行游戏并打一局。

### 14.4 游戏卡顿/性能问题

**可能原因**：
- 图片分辨率过高（建议背景图 ≤ 512×768，飞机/道具图 ≤ 64×64）
- 敌机数量过多（某些极端情况可能大量产生子弹和敌机）
- 音频文件编码不兼容（建议使用 16-bit PCM WAV）

### 14.5 IDEA 导入后报错

**解决步骤**：
1. File → Project Structure → Project → SDK → 选择 JDK 11+
2. File → Project Structure → Modules → Dependencies → 确保无未解析的依赖
3. 如果源码编码乱码，检查 File → Settings → File Encodings → 设置为 UTF-8

### 14.6 如何调整游戏难度参数？

修改对应 `*Game.java` 中的 `initGameSettings()` 方法即可。例如调整地狱模式的敌机生成周期：

```java
// ExpertGame.java
this.enemySpawnCycle = 10; // 数值越小，敌机生成越快
```

> 每个参数都有详细注释，可根据需要调整数值。

---

## 附录

### A. 快捷键一览

| 快捷键 | 功能 |
|--------|------|
| 鼠标拖拽 | 移动英雄机 |
| 窗口关闭按钮 | 退出游戏 |

游戏暂时不支持键盘操作。

### B. 各包职责速查

| 包路径 | 职责 |
|--------|------|
| `cn.edu.scnu.aircraft` | 飞机类（英雄机 + 所有敌机 + 工厂） |
| `cn.edu.scnu.application` | 应用入口、主菜单、图片管理、排行榜窗口 |
| `cn.edu.scnu.application.game` | 各难度游戏逻辑实现 |
| `cn.edu.scnu.basic` | 飞行对象基类（坐标/碰撞/图片懒加载） |
| `cn.edu.scnu.bullet` | 子弹类 |
| `cn.edu.scnu.shoot` | 射击策略（策略模式） |
| `cn.edu.scnu.prop` | 道具基类/工厂/观察者 |
| `cn.edu.scnu.prop.supply` | 5 种具体道具实现 |
| `cn.edu.scnu.prop.observer` | 观察者接口与管理器 |
| `cn.edu.scnu.rank` | 排行榜数据层 |
| `cn.edu.scnu.music` | 音频播放管理 |

### C. 版本信息

| 项目 | 版本 |
|------|------|
| 项目版本 | v1.2 |
| JDK | 11+ |
| 最后更新 | 2026-07-14 |
| 作者 | 黄彪骐 |

---

> 🎮 **祝您游戏愉快！如有任何问题，欢迎查阅源码或在 Issue 中提出。**

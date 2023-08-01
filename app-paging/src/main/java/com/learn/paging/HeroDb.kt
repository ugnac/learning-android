package com.learn.paging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Hero::class], version = 1, exportSchema = true)
abstract class HeroDb : RoomDatabase() {
    abstract fun heroDao(): HeroDao

    companion object {
        const val DB_NAME = "hero_db"

        private var instance: HeroDb? = null

        @Synchronized
        fun getInstance(context: Context): HeroDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    HeroDb::class.java, DB_NAME
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        fillInitialData(context.applicationContext)
                    }
                }).build()
            }
            return instance!!
        }

        /**
         * fill database with list of students
         */
        private fun fillInitialData(applicationContext: Context) {
            // inserts in Room are executed on the current thread, so we insert in the background
            ioThread {
                getInstance(applicationContext).heroDao().insertAll(HREOS)
            }
        }
    }
}

private val HREOS = arrayListOf<Hero>(
    Hero(1, "拓拔野", "男"),
    Hero(2, "乔蚩尤", "男"),
    Hero(3, "雨师妾", "女"),
    Hero(4, "姑射仙子", "女"),
    Hero(5, "晏紫苏", "女"),
    Hero(6, "洛姬雅", "女"),
    Hero(7, "烈烟石", "女"),
    Hero(8, "纤纤", "女"),
    Hero(9, "姬远玄", "男"),
    Hero(10, "烈炎", "男"),
    Hero(11, "神帝神农氏", "男"),
    Hero(12, "黑帝汁光纪", "男"),
    Hero(13, "白帝白招拒", "男"),
    Hero(14, "青帝灵感仰", "男"),
    Hero(15, "赤帝赤飚怒", "男"),
    Hero(16, "黄帝姬少典", "男"),
    Hero(17, "玄水真神烛龙", "男"),
    Hero(18, "金神石夷", "男"),
    Hero(19, "水伯天吴", "男"),
    Hero(20, "西王母白水香", "女"),
    Hero(21, "长流仙子", "女"),
    Hero(22, "少昊", "男"),
    Hero(23, "古元坎", "男"),
    Hero(24, "木神句芒", "男"),
    Hero(25, "雷神雷破天", "男"),
    Hero(26, "空桑仙子", "女"),
    Hero(27, "乔羽", "男"),
    Hero(28, "水圣女乌丝兰玛", "女"),
    Hero(29, "北海真神双头老祖", "男"),
    Hero(30, "西海老祖拿兹", "男"),
    Hero(31, "龙牙侯科淮汗", "男"),
    Hero(32, "圣女赤霞仙子", "女"),
    Hero(33, "火神祝融", "男"),
    Hero(34, "战神刑天", "男"),
    Hero(35, "火族大长老烈碧光晟", "男"),
    Hero(36, "大荒雨师赤松子", "男"),
    Hero(37, "浮玉城主李衍", "男"),
    Hero(38, "炎帝烈炎", "男"),
    Hero(39, "八郡主烈烟石", "男"),
    Hero(40, "黄帝姬少典", "男"),
    Hero(41, "圣女武罗仙子", "女"),
    Hero(42, "黄龙真神应龙", "男"),
    Hero(43, "广成子", "男"),
    Hero(44, "太子黄帝姬远玄", "男"),
    Hero(45, "木族前亚圣女丁香仙子", "女"),
    Hero(46, "龙神敖语真", "女"),
    Hero(47, "波母汁玄青", "女"),
    Hero(48, "阳极真神公孙婴侯", "男"),
    Hero(49, "九翼天龙缚南仙", "女"),
    Hero(50, "二八神人", "男"),
    Hero(51, "延维", "男"),
    Hero(52, "无晵蛇姥", "女"),
    Hero(53, "轩辕黄帝拓拔野", "男"),
    Hero(54, "拓拔野", "男"),
    Hero(55, "神农", "男"),
    Hero(56, "蚩尤", "男"),
    Hero(57, "女魃", "女"),
    Hero(58, "青帝", "男"),
    Hero(59, "姬远玄", "男"),
    Hero(60, "广成子", "男"),
    Hero(61, "天吴", "男"),
    Hero(62, "二八神人", "男"),
    Hero(63, "鬼帝", "男"),
    Hero(64, "烛龙", "男"),
    Hero(65, "应龙", "男"),
    Hero(66, "科汗淮", "男"),
    Hero(67, "烛龙", "男"),
    Hero(68, "赤帝", "男"),
    Hero(69, "黑帝", "男"),
    Hero(70, "缚南仙", "女"),
    Hero(71, "丁香仙子", "女"),
    Hero(72, "长留仙子", "女"),
    Hero(73, "石夷", "男"),
    Hero(74, "白帝", "男"),
    Hero(75, "夸父", "男"),
    Hero(76, "赤松子", "男"),
    Hero(77, "烈炎", "男"),
    Hero(78, "刑天", "男"),
    Hero(79, "公孙婴候", "男"),
    Hero(80, "九天玄女", "女"),
    Hero(81, "西海老祖", "男"),
)
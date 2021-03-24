package com.udacity.asteroidradar.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {

    @Query("SELECT * FROM DbAsteroid WHERE closeApproachDate >= :day AND  closeApproachDate <= :dayEndWeek ORDER BY closeApproachDate asc")
    fun getAsteroidsWeek(day:String,dayEndWeek:String): LiveData<List<DbAsteroid>>

    @Query("select * from DbAsteroid where closeApproachDate >=:day order by closeApproachDate asc")
    fun getAsteroidSave(day:String): LiveData<List<DbAsteroid>>

    @Query("select * from DbAsteroid where closeApproachDate = :day")
    fun getAsteroidsToday(day:String): LiveData<List<DbAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DbAsteroid)
}


@Database(entities = [DbAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}


private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}

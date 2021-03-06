package de.rki.coronawarnapp.risk.storage.internal

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.rki.coronawarnapp.risk.storage.internal.riskresults.PersistedRiskLevelResultDao
import de.rki.coronawarnapp.risk.storage.internal.windows.PersistedExposureWindowDao
import de.rki.coronawarnapp.risk.storage.internal.windows.PersistedExposureWindowDaoWrapper
import de.rki.coronawarnapp.util.database.CommonConverters
import de.rki.coronawarnapp.util.di.AppContext
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

@Suppress("MaxLineLength")
@Database(
    entities = [
        PersistedRiskLevelResultDao::class,
        PersistedExposureWindowDao::class,
        PersistedExposureWindowDao.PersistedScanInstance::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    CommonConverters::class,
    PersistedRiskLevelResultDao.Converter::class,
    PersistedRiskLevelResultDao.PersistedAggregatedRiskResult.Converter::class
)
abstract class RiskResultDatabase : RoomDatabase() {

    abstract fun riskResults(): RiskResultsDao

    abstract fun exposureWindows(): ExposureWindowsDao

    @Dao
    interface RiskResultsDao {
        @Query("SELECT * FROM riskresults")
        fun allEntries(): Flow<List<PersistedRiskLevelResultDao>>

        @Insert(onConflict = OnConflictStrategy.ABORT)
        suspend fun insertEntry(riskResultDao: PersistedRiskLevelResultDao)

        @Query(
            "DELETE FROM riskresults where id NOT IN (SELECT id from riskresults ORDER BY calculatedAt DESC LIMIT :keep)"
        )
        suspend fun deleteOldest(keep: Int): Int
    }

    @Dao
    interface ExposureWindowsDao {
        @Query("SELECT * FROM exposurewindows")
        fun allEntries(): Flow<List<PersistedExposureWindowDaoWrapper>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertWindows(exposureWindows: List<PersistedExposureWindowDao>): List<Long>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertScanInstances(scanInstances: List<PersistedExposureWindowDao.PersistedScanInstance>)

        @Query(
            "DELETE FROM exposurewindows where riskLevelResultId NOT IN (:riskResultIds)"
        )
        suspend fun deleteByRiskResultId(riskResultIds: List<String>): Int
    }

    class Factory @Inject constructor(@AppContext private val context: Context) {

        fun create(): RiskResultDatabase {
            Timber.d("Instantiating risk result database.")
            return Room
                .databaseBuilder(context, RiskResultDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigrationFrom()
                .build()
        }
    }

    companion object {
        private const val DATABASE_NAME = "riskresults.db"
    }
}

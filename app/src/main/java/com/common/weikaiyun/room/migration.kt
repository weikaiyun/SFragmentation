package com.common.weikaiyun.room

import android.content.Context
import androidx.core.util.ObjectsCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.collections.ArrayList

/**
 * A room-database-migration simple implementation.
 *
 * Migrate [databaseClass] from [fromVersion] to [toVersion], use [entityComparator] to determine
 * which tables and columns should be migrated from or migrate to, the [databaseClass] should
 * same as the second argument of [Room.databaseBuilder].
 *
 * **Note! By default, it works only if the NAME of the tables and columns NOT CHANGED.**
 * It means that you can add and remove some tables or columns,
 * you can add and remove some constraints of them,
 * such as foreign keys or indexes, etc, but **YOU CAN'T CHANGE IT'S NAME!**
 * If you want to change this behavior, implements your own [IEntityComparator] and set to it.
 *
 * It relies on the automatically generated schema file of room,
 * so the following code need to be added to the gradle file:
 *
 * ```
 * android {
 *  ...
 *  sourceSets {
 *      main {
 *          assets.srcDirs += files("$projectDir/schemas".toString())
 *      }
 *   }
 *  }
 * ```
 * **This class can only be used when the data being migrated is small.**
 *
 * During the migration, it will:
 * 1. Copy all the data in the changed table into an **in-memory** temporary table
 * 2. Delete the old table and create a new table
 * 3. Copy data from the temporary table to the new table and delete the temporary table
 *
 * So, if the migrated data is too large, it may cause oom.
 *
 * @since 1.0
 * @author Wing-Hawk
 */
open class DatabaseMigration<in T : RoomDatabase>(
    private val context: Context,
    private val databaseClass: Class<T>,
    private val fromVersion: Int,
    private val toVersion: Int,
    private val entityComparator: IEntityComparator = DefaultEntityComparator()
) : Migration(fromVersion, toVersion) {

    companion object {
        private const val SQLITE_MASTER = "sqlite_master"
        private const val SQLITE_TEMP_MASTER = "sqlite_temp_master"
        private const val BACKUP_SUFFIX = "_BACKUP"
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        // parse schema file
        val oldEntities = parseSchemaFile(fromVersion)
        val newEntities = parseSchemaFile(toVersion)

        val oldChangedEntities = oldEntities.toMutableSet()
        val newChangedEntities = newEntities.toMutableSet()

        // remove unchanged entities
        oldChangedEntities.removeAll { oldEntity ->
            newEntities.any { newEntity ->
                entityComparator.equals(oldEntity, newEntity)
            }
        }

        newChangedEntities.removeAll { newEntity ->
            oldEntities.any { oldEntity ->
                entityComparator.equals(oldEntity, newEntity)
            }
        }

        if (oldChangedEntities.isEmpty() && newChangedEntities.isEmpty()) {
            // no table changed, skip migrate
            return
        }

        // certain old table changed/deleted or certain new table added
        database.runInTx {
            backupData(database, oldChangedEntities, newChangedEntities)
            dropOldTables(database, oldChangedEntities)
            createNewTables(database, newChangedEntities)
            restoreData(database, oldChangedEntities, newChangedEntities)
        }
    }

    open fun backupData(
        database: SupportSQLiteDatabase,
        oldEntities: Set<Entity>,
        newEntities: Set<Entity>) {
        // make temp table stored in memory.
        // if skipped or set to other value, androidx.room.InvalidationTracker#internalInit method
        // will throw an exception later for trying to change the temp_store value after the
        // temporary table is created.
        // shit! It took me half a day reading room's source code to find and solve this problem.
        database.execSQL("PRAGMA temp_store = MEMORY;")

        oldEntities.filter { oldEntity ->
            // only the table which exists and retained in new version need to backup
            isTableExists(database, oldEntity.tableName, false) &&
                    newEntities.any { newEntity -> entityComparator.isEntitySame(oldEntity, newEntity) }
        }.forEach { oldEntity ->
            // drop to make sure the backup-table is new
            database.execSQL("DROP TABLE IF EXISTS `${oldEntity.tableName}$BACKUP_SUFFIX`;")
            // backup data
            database.execSQL(
                """CREATE TEMPORARY TABLE `${oldEntity.tableName}$BACKUP_SUFFIX`
                        AS SELECT * FROM `${oldEntity.tableName}`;"""
            )
        }
    }

    open fun dropOldTables(database: SupportSQLiteDatabase, oldEntities: Set<Entity>) {
        oldEntities.forEach { database.execSQL("DROP TABLE IF EXISTS `${it.tableName}`;") }
    }

    open fun createNewTables(database: SupportSQLiteDatabase, newEntities: Set<Entity>) {
        newEntities.forEach { newEntity ->
            database.execSQL("${newEntity.createSql.replace("\${TABLE_NAME}", newEntity.tableName)};")
            newEntity.indices.forEach { index ->
                database.execSQL("${index.createSql.replace("\${TABLE_NAME}", newEntity.tableName)};")
            }
        }
    }

    open fun restoreData(
        database: SupportSQLiteDatabase,
        oldEntities: Set<Entity>,
        newEntities: Set<Entity>) {
        oldEntities.forEach { oldEntity ->
            // check to see whether a backup table is exists and has backup data, if not, skip restore data.
            if (isTableExists(database, "${oldEntity.tableName}$BACKUP_SUFFIX", true) &&
                oldEntity.fields.isNotEmpty()
            ) {
                val newEntity = newEntities.find { newEntity -> entityComparator.isEntitySame(oldEntity, newEntity) }
                // columns to insert into new table
                val restoreColumns = ArrayList<String>()
                // columns to select from backup table
                val backupColumns = ArrayList<String>()
                // table in new version is exists, find which columns need to be restored
                newEntity?.apply {
                    fields.forEach { newField ->
                        val oldField = oldEntity.fields.find { oldField ->
                            entityComparator.isFieldSame(oldEntity, newEntity, oldField, newField)
                        }
                        if (oldField != null) {
                            // a column needs to restore data
                            backupColumns.add("`${oldField.columnName}`")
                            restoreColumns.add("`${newField.columnName}`")
                            // if old column is nullable but the new is non-null, set default value to it
                            if (newField.notNull && !oldField.notNull) {
                                val replaceNullSql =
                                    """UPDATE `${oldEntity.tableName}$BACKUP_SUFFIX`
                                                        SET ${oldField.columnName} = ${oldField.getDefaultValue()}
                                                        WHERE ${oldField.columnName} IS NULL;"""
                                database.execSQL(replaceNullSql)
                            }
                        } else {
                            if (newField.notNull) {
                                // non-null field added in new version
                                restoreColumns.add("`${newField.columnName}`")
                                // set a default value to it
                                backupColumns.add("${newField.getDefaultValue()} AS `${newField.columnName}`")
                            } else {
                                // do nothing...
                            }
                        }
                    }
                    val restoreColumnsStr = restoreColumns.joinToString(",")
                    val backupColumnsStr = backupColumns.joinToString(",")

                    if (restoreColumns.isNotEmpty() && backupColumns.isNotEmpty()) {
                        database.execSQL(
                            """REPLACE INTO `${tableName}` ($restoreColumnsStr)
                                SELECT $backupColumnsStr FROM `${oldEntity.tableName}$BACKUP_SUFFIX`;"""
                        )
                    }
                }
            }
            // restore finish, delete temp table
            database.execSQL("DROP TABLE IF EXISTS `${oldEntity.tableName}$BACKUP_SUFFIX`;")
        }
    }

    private fun isTableExists(
        database: SupportSQLiteDatabase,
        tableName: String,
        isTemp: Boolean): Boolean {
        val masterTable = if (isTemp) SQLITE_TEMP_MASTER else SQLITE_MASTER
        val sql = "SELECT COUNT(*) FROM `$masterTable` WHERE type = 'table' AND name = ?;"
        return database.query(sql, arrayOf(tableName)).use { cursor ->
            cursor != null && cursor.moveToFirst() && cursor.getInt(0) > 0
        }
    }

    private fun Field.getDefaultValue(): Any {
        return when (affinity) {
            "INTEGER" -> 0
            "REAL" -> 0
            "NUMERIC" -> 0
            else -> "''"
        }
    }

    // get table info from database schema file
    private fun parseSchemaFile(version: Int): List<Entity> {
        return context.assets.open("${databaseClass.name}/$version.json").use {
            val json = Json{ignoreUnknownKeys = true}
            json.decodeFromString(DatabaseSchema.serializer(), String(it.readBytes())).database.entities
        }
    }

    // run in transaction
    private fun SupportSQLiteDatabase.runInTx(block: () -> Unit) {
        beginTransaction()
        try {
            block()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}

/**
 * This interface is used to determine which tables and columns should be migrated from or migrate to.
 */
interface IEntityComparator {

    /**
     * Whether the [oldEntity] is logically the same as the [newEntity].
     * You must ensure that each old table has at most one new table logically the same to it.
     */
    fun isEntitySame(oldEntity: Entity, newEntity: Entity): Boolean

    /**
     * Whether the [oldEntity]'s [oldField] is logically the same as the [newEntity]'s [newField].
     * You must ensure that each old field has at most one new field logically the same as it
     * in it's logically the same table.
     */
    fun isFieldSame(oldEntity: Entity, newEntity: Entity, oldField: Field, newField: Field): Boolean

    /**
     * Compare whether the [oldEntity] and [newEntity] are structurally identical.
     * This method is used to determine if certain entities has any changed.
     * Only changed entities need to be migrated.
     */
    fun equals(oldEntity: Entity, newEntity: Entity): Boolean
}

open class DefaultEntityComparator : IEntityComparator {

    override fun isEntitySame(oldEntity: Entity, newEntity: Entity): Boolean {
        return oldEntity.tableName == newEntity.tableName
    }

    override fun isFieldSame(oldEntity: Entity, newEntity: Entity, oldField: Field, newField: Field): Boolean {
        return isEntitySame(oldEntity, newEntity) && oldField.columnName == newField.columnName
    }

    override fun equals(oldEntity: Entity, newEntity: Entity): Boolean {
        return oldEntity == newEntity
    }
}

@Serializable
data class DatabaseSchema(
    val formatVersion: Int,
    val database: Database)

@Serializable
data class Database(
    val version: Int,
    val identityHash: String,
    val entities: List<Entity>,
    val setupQueries: List<String>)

@Serializable
data class Entity(
    val tableName: String,
    val createSql: String,
    val fields: List<Field>,
    val primaryKey: PrimaryKey,
    val indices: List<Index>,
    val foreignKeys: List<ForeignKey>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        other as Entity
        return ObjectsCompat.equals(createSql, other.createSql)
                && ObjectsCompat.equals(indices, other.indices)
    }

    override fun hashCode(): Int {
        return ObjectsCompat.hash(createSql, indices)
    }
}

@Serializable
data class Field(
    val fieldPath: String,
    val columnName: String,
    val affinity: String,
    val notNull: Boolean)

@Serializable
data class PrimaryKey(
    val columnNames: List<String>,
    val autoGenerate: Boolean)

@Serializable
data class Index(
    val name: String,
    val unique: Boolean,
    val columnNames: List<String>,
    val createSql: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        other as Index
        return ObjectsCompat.equals(createSql, other.createSql)
    }

    override fun hashCode(): Int {
        return ObjectsCompat.hash(createSql)
    }
}

@Serializable
data class ForeignKey(
    val table: String,
    val onDelete: String,
    val onUpdate: String,
    val columns: List<String>,
    val referencedColumns: List<String>)


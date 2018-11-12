package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright © 2006-2018 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2018/4/8.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Options type define by enum
     */
    public enum OpsType {
        INSERT,
        BULK,
        UPDATE,
        DELETE,
        QUERY
    }

    /**
     * Constant
     */
    private static final int ENABLE_TRANSACTION_LIMITED = 50;

    /**
     * Member variables
     */
    private String[] mDBTablesSql;
    private SQLiteDatabase mDatabase;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private final Collection<DatabaseListener> listeners = new HashSet<>();

    /**
     * Constructor
     *
     * @param context        a Context object used to access application assets
     * @param db_name        the file name of the database
     * @param db_ver         the current version of the database
     * @param createTableSQL Create a table base on  SQL statements of array
     */
    public DatabaseHelper(Context context, String db_name, int db_ver, String[] createTableSQL) {
        super(context, db_name, null, db_ver);
        mDBTablesSql = new String[createTableSQL.length];
        System.arraycopy(createTableSQL, 0, mDBTablesSql, 0, createTableSQL.length);
    }

    /**
     * Create database callback
     *
     * @param db SQLiteDatabase handle
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String aDBTablesSql : mDBTablesSql) {
            db.execSQL(aDBTablesSql);
            System.out.println("DatabaseHelper->onCreate:" + aDBTablesSql);
        }
    }

    /**
     * Upgrade database callback
     *
     * @param db         SQLiteDatabase handle
     * @param oldVersion old version of the database
     * @param newVersion new or current version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Open database and get write/read ability
     */
    private void openDatabase() {
        if (mDatabase == null) {
            try {
                mDatabase = getWritableDatabase();
            } catch (SQLException e) {
                mDatabase = getReadableDatabase();
            }
        }
    }

    /**
     * Insert a data into a specified table by ContentValues collection
     *
     * @param listener       callback listener
     * @param table          the table to insert the row into
     * @param nullColumnHack null column hack
     * @param contentValues  a key-value collection object
     */
    public void insert(DatabaseListener listener, String table, String nullColumnHack,
                       ContentValues contentValues) {
        if (listener != null) {
            setListeners(listener);
        }
        opsDatabase(OpsType.INSERT, table, new ContentValues[]{contentValues}, new
                String[]{nullColumnHack});
    }

    /**
     * @param listener      callback listener
     * @param contentValues a key-value collection object array
     * @param insertArgs    insert method arguments, @Nullable. {include: table, nullColumnHack}
     */
    public void bulkInsert(DatabaseListener listener, String table, ContentValues[]
            contentValues, String[]...
                                   insertArgs) {
        if (listener != null) {
            setListeners(listener);
        }
        opsDatabase(OpsType.BULK, table, contentValues, insertArgs);
    }

    /**
     * Query data from table by selections
     *
     * @param listener  callback listener
     * @param queryArgs query method arguments, @Nullable. {include: table, columns[], selection,
     *                  selectionArgs[], groupBy, having,
     *                  orderBy, limit}
     */
    public void query(DatabaseListener listener, String table, String[]... queryArgs) {
        if (listener != null) {
            setListeners(listener);
        }
        opsDatabase(OpsType.QUERY, table, null, queryArgs);
    }

    /**
     * Delete data by delete args
     *
     * @param listener   callback listener
     * @param deleteArgs delete method arguments, @Nullable,. {include: table, whereClause,
     *                   whereArgs[]}
     */
    public void delete(DatabaseListener listener, String table, String[]... deleteArgs) {
        if (listener != null) {
            setListeners(listener);
        }
        opsDatabase(OpsType.DELETE, table, null, deleteArgs);
    }

    /**
     * Close SQLite database
     */
    public void closeDatabase() {
        if (mDatabase != null) {
            if (mDatabase.isOpen())
                mDatabase.close();
            mDatabase = null;
        }
        close();
    }

    /**
     * Database operation
     *
     * @param opsTypes type of operation database
     * @param table    table name of operation database
     * @param values   a key-value collection object array of operation data
     * @param args     operation arguments, @Nullable.
     */
    private void opsDatabase(final OpsType opsTypes, final String table, final ContentValues[] values, final
    String[]... args) {
        System.out.println("opsDatabase------>" + opsTypes.name());
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                openDatabase();
                boolean enableTransaction = false;
                try {
                    switch (opsTypes) {
                        case INSERT:
                            opsDatabaseCallback(opsTypes, null, mDatabase.insert(table, args[0][0], values[0]));
                            break;
                        case BULK:
                            break;
                        case UPDATE:
                            break;
                        case DELETE:
                            opsDatabaseCallback(opsTypes, null, mDatabase.delete(table, checkParameter(args[0]),
                                    args[1]));
                            break;
                        case QUERY:
                            Cursor result = null;
                            System.out.println("args_length:" + args.length);
                            switch (args.length) {
                                case 7:
                                    if (args[1] == null) {
                                        System.out.println("args[1] is " + checkParameter(args[1]));
                                    }
                                    result = mDatabase.query(table, args[0], checkParameter(args[1]), args[2],
                                            checkParameter(args[3]), checkParameter(args[4]), checkParameter(args[5])
                                            , checkParameter(args[6]));
                                    break;
                                case 8:
                                    break;
                                case 9:
                                    break;
                            }
                            if (result != null) {
                                opsDatabaseCallback(opsTypes, cursorConverToContentValues(result), result
                                        .getColumnCount());
                                result.close();
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (enableTransaction) {
                        mDatabase.endTransaction();
                    }
                    closeDatabase();
                }
            }
        });
    }

    /**
     * @param results
     */
    private void opsDatabaseCallback(final OpsType opsTypes, final ContentValues[] results, final
    long rowID) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                DatabaseListener[] array = listeners.toArray(new DatabaseListener[0]);
                for (DatabaseListener listener : array) {
                    listener.onDatabaseOperationResult(opsTypes, results, rowID);
                }
            }
        });
    }

    /**
     * Setting listener
     *
     * @param listener callback listener
     */
    private void setListeners(DatabaseListener listener) {
        if (listener != null) {
            synchronized (this.listeners) {
                this.listeners.add(listener);
            }
        }
    }

    /**
     * @param queryResult
     * @return
     */
    private ContentValues[] cursorConverToContentValues(final Cursor queryResult) {
        if (!queryResult.isClosed() && queryResult.getCount() != 0) {
            ContentValues[] queryResults = new ContentValues[queryResult.getCount()];
            int valueIndex = 0;
            while (queryResult.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                for (String columnName : queryResult.getColumnNames()) {
                    getValueByColumnName(queryResult, columnName, contentValues);
                }
                queryResults[valueIndex] = contentValues;
                valueIndex++;
            }
            return queryResults;
        }
        return null;
    }

    /**
     * @param cursor
     * @param columnName
     * @param contentValues
     */
    private void getValueByColumnName(Cursor cursor, String columnName, ContentValues
            contentValues) {
        int columnIndex = cursor.getColumnIndex(columnName);
        switch (getType(cursor, columnIndex)) {
            case 3:
                contentValues.put(columnName, cursor.getString(columnIndex));
                break;
            case 2:
                contentValues.put(columnName, cursor.getDouble(columnIndex));
                break;
            case 1:
                contentValues.put(columnName, cursor.getInt(columnIndex));
                break;
            case 4:
                contentValues.put(columnName, cursor.getBlob(columnIndex));
                break;
            case 0:
            default:
                contentValues = null;
                break;
        }
    }

    /**
     * @param cursor
     * @param columnName
     * @return
     */
    private int getType(Cursor cursor, int columnName) {
        SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
        CursorWindow cursorWindow = sqLiteCursor.getWindow();
        int pos = cursor.getPosition();
        int type = -1;
        if (cursorWindow.isNull(pos, columnName)) {
            type = 0;
        } else if (cursorWindow.isLong(pos, columnName)) {
            type = 1;
        } else if (cursorWindow.isFloat(pos, columnName)) {
            type = 2;
        } else if (cursorWindow.isString(pos, columnName)) {
            type = 3;
        } else if (cursorWindow.isBlob(pos, columnName)) {
            type = 4;
        }
        return type;
    }

    private String checkParameter(String[] array) {
        if (array == null) {
            return null;
        } else {
            return array[0];
        }
    }

//    private boolean isEmpty(String[][] array) {
//        return (array == null || array.length == 0) || (array.length == 1 && array[0].length ==
// 0);
//    }
//
//    private void ParseParameters(Object... objects) {
//        if (objects != null) {
//            for (Object o : objects) {
//            }
//        }
//    }
}

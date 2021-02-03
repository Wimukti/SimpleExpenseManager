package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.Serializable;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {

    public static final int database_version = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA = ",";

    /**
     * SQL queries for Account Table
     */
    private static final String CREATE_ACCOUNT_ENTRIES =
            "CREATE TABLE " + DatabaseSchema.AccountTable.TABLE_NAME + " (" +
                    DatabaseSchema.AccountTable.ACCOUNT_NUMBER + TEXT_TYPE + " PRIMARY KEY" + COMMA +
                    DatabaseSchema.AccountTable.BANK_NAME + TEXT_TYPE + COMMA +
                    DatabaseSchema.AccountTable.ACCOUNT_HOLDER_NAME + TEXT_TYPE + COMMA +
                    DatabaseSchema.AccountTable.BALANCE + REAL_TYPE + ")";

    private static final String DELETE_ACCOUNT_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseSchema.AccountTable.TABLE_NAME;

    /**
     * SQL queries for Transaction Table
     */
    private static final String CREATE_TRANSACTION_ENTRIES =
            "CREATE TABLE " + DatabaseSchema.TransactionTable.TABLE_NAME + " (" +
                    DatabaseSchema.TransactionTable.TRANSACTION_DATE + TEXT_TYPE + " PRIMARY KEY" + COMMA +
                    DatabaseSchema.TransactionTable.AMOUNT + REAL_TYPE + COMMA +
                    DatabaseSchema.TransactionTable.EXPENSE_TYPE + TEXT_TYPE + COMMA +
                    DatabaseSchema.TransactionTable.ACCOUNT_NUMBER + TEXT_TYPE + COMMA +
                    "FOREIGN KEY (" + DatabaseSchema.AccountTable.ACCOUNT_NUMBER + ") REFERENCES " +
                    DatabaseSchema.AccountTable.TABLE_NAME + "(" + DatabaseSchema.AccountTable.ACCOUNT_NUMBER + "))";

    private static final String DELETE_TRANSACTION_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseSchema.TransactionTable.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DatabaseSchema.DATABASE_NAME, null, database_version);
    }

    /**
     * DatabaseHelper abstract classes implementation
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT_ENTRIES);
        db.execSQL(CREATE_TRANSACTION_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_ACCOUNT_ENTRIES);
        db.execSQL(DELETE_TRANSACTION_ENTRIES);
        onCreate(db);
    }
}
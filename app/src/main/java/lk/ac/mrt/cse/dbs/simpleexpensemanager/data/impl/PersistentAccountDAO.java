package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO,Serializable {

    private SQLiteOpenHelper helper;

    public PersistentAccountDAO(Context context) {
        helper = new DatabaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = String.format(
                "SELECT %s FROM %s",
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER,
                DatabaseSchema.AccountTable.TABLE_NAME);

        List<String> numbers = new ArrayList<>();
        final Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                numbers.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return numbers;
    }

    @Override
    public List<Account> getAccountsList() {

        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = String.format("SELECT %s, %s, %s, %s FROM %s",
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER,
                DatabaseSchema.AccountTable.BANK_NAME,
                DatabaseSchema.AccountTable.ACCOUNT_HOLDER_NAME,
                DatabaseSchema.AccountTable.BALANCE,
                DatabaseSchema.AccountTable.TABLE_NAME);

        List<Account> accounts = new ArrayList<>();
        final Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                accounts.add(new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = ?",
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER,
                DatabaseSchema.AccountTable.BANK_NAME,
                DatabaseSchema.AccountTable.ACCOUNT_HOLDER_NAME,
                DatabaseSchema.AccountTable.BALANCE,
                DatabaseSchema.AccountTable.TABLE_NAME,
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER);

        final Cursor cursor = db.rawQuery(sql, new String[]{accountNo});
        cursor.moveToFirst();

        if (cursor.getCount()>0) {
            Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            cursor.close();
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        cursor.close();
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = String.format("INSERT OR IGNORE INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                DatabaseSchema.AccountTable.TABLE_NAME,
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER,
                DatabaseSchema.AccountTable.BANK_NAME,
                DatabaseSchema.AccountTable.ACCOUNT_HOLDER_NAME,
                DatabaseSchema.AccountTable.BALANCE);

        db.execSQL(sql, new Object[]{
                account.getAccountNo(),
                account.getBankName(),
                account.getAccountHolderName(),
                account.getBalance()});
    }

    /**
     * First using getAccount method, tried to get the relevant account from db. But it will throws an InvalidAccountException if not found
     * Otherwise it will successfully run the query
     */
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        getAccount(accountNo);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                DatabaseSchema.AccountTable.TABLE_NAME,
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER);

        db.execSQL(sql, new Object[]{accountNo});
    }
    /**
     * First using getAccount method, tried to get the relevant account from db. But it will throws an InvalidAccountException if not found
     * Otherwise it will successfully run the query
     */
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        getAccount(accountNo);
        SQLiteDatabase db = helper.getWritableDatabase();

        /**
         * Depending on the transaction type we may need to change the sqlQuery
         */

        String sqlQuery = null;
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                sqlQuery = "UPDATE %s SET %s = %s - ? WHERE %s = ?";
                break;
            case INCOME:
                sqlQuery = "UPDATE %s SET %s = %s + ? WHERE %s = ?";
                break;
        }
        sqlQuery = String.format(sqlQuery,
                DatabaseSchema.AccountTable.TABLE_NAME,
                DatabaseSchema.AccountTable.BALANCE,
                DatabaseSchema.AccountTable.BALANCE,
                DatabaseSchema.AccountTable.ACCOUNT_NUMBER);

        db.execSQL(sqlQuery, new Object[]{amount, accountNo});
    }
}
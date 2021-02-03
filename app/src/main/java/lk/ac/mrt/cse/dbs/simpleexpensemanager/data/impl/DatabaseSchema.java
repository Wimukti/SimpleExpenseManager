package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.provider.BaseColumns;
import java.io.Serializable;

final class DatabaseSchema implements Serializable {

    static final String DATABASE_NAME = "180424D";
    /**
     * Default Constructor
     */
    private DatabaseSchema() {}

    static class AccountTable implements BaseColumns {
        static final String TABLE_NAME = "account_table";
        static final String ACCOUNT_NUMBER = "acc_no";
        static final String BANK_NAME = "bank";
        static final String ACCOUNT_HOLDER_NAME = "holder";
        static final String BALANCE = "balance";
    }

    static class TransactionTable implements BaseColumns {
        static final String TABLE_NAME = "transaction_table";
        static final String TRANSACTION_DATE = "date";
        static final String AMOUNT = "amount";
        static final String EXPENSE_TYPE = "type";
        static final String ACCOUNT_NUMBER = "acc_no";
    }
}
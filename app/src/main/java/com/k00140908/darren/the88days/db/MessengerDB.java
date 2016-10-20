package com.k00140908.darren.the88days.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.k00140908.darren.the88days.Model.Message;
import com.k00140908.darren.the88days.Model.User;

import java.util.ArrayList;

/**
 * Created by darre on 10/04/2016.
 */
public class MessengerDB {

    //adb -d shell "run-as com.k00140908.darren.the88days cat /data/data/com.k00140908.darren.the88days/databases/messeneger.sqlite > /sdcard/messeneger.sqlite"

    // database constants
    public static final String DB_NAME = "messenger.db";
    public static final int DB_VERSION = 1;

    // backpacker table constants
    public static final String BACKPACKER_TABLE = "backpacker";

    public static final String BACKPACKER_ID = "_id";
    public static final int BACKPACKER_ID_COL = 0;

    public static final String BACKPACKER_NAME = "backpacker_name";
    public static final int BACKPACKER_NAME_COL = 1;

    public static final String BACKPACKER_USERID = "backpacker_userid";
    public static final int BACKPACKER_USERID_COL = 2;

    public static final String BACKPACKER_COUNT = "backpacker_count";
    public static final int BACKPACKER_COUNT_COL = 3;

    public static final String BACKPACKER_LAST_MSG = "backpacker_last_msg";
    public static final int BACKPACKER_LAST_MSG_COL = 4;

    // message table constants
    public static final String MESSAGE_TABLE = "message";

    public static final String MESSAGE_ID = "_id ";
    public static final int MESSAGE_ID_COL = 0;

    public static final String MESSAGE_MSG = "msg";
    public static final int MESSAGE_MSG_COL = 1;

    public static final String MESSAGE_BACKPACKER_RECEIVER = "backpacker_receiver_id";
    public static final int MESSAGE_BACKPACKER_RECEIVER_COL = 2;

    public static final String MESSAGE_BACKPACKER_SENDER = "backpacker_sender_id";
    public static final int MESSAGE_BACKPACKER_SENDER_COL = 3;

    public static final String MESSAGE_SENT_AT = "sent_at";
    public static final int MESSAGE_SENT_AT_COL = 4;


    // CREATE and DROP TABLE statements
    public static final String CREATE_BACKPACKER_TABLE =
            "CREATE TABLE " + BACKPACKER_TABLE + " (" +
                    BACKPACKER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BACKPACKER_NAME + " TEXT, " +
                    BACKPACKER_USERID + " TEXT, " +
                    BACKPACKER_COUNT + " INTEGER, " +
                    BACKPACKER_LAST_MSG + " BIGINTEGER)";

    public static final String CREATE_MESSAGE_TABLE =
            "CREATE TABLE " + MESSAGE_TABLE + " (" +
                    MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MESSAGE_MSG + " TEXT, " +
                    MESSAGE_BACKPACKER_RECEIVER + " TEXT, " +
                    MESSAGE_BACKPACKER_SENDER + " TEXT, " +
                    MESSAGE_SENT_AT + " BIGINTEGER)";

    public static final String DROP_BACKPACKER_TABLE =
            "DROP TABLE IF EXISTS " + BACKPACKER_TABLE;

    public static final String DROP_MESSAGE_TABLE =
            "DROP TABLE IF EXISTS " + MESSAGE_TABLE;

    public static final String TASK_MODIFIED =
            "com.k00140908.darren.the88days.TASK_MODIFIED";


    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_BACKPACKER_TABLE);
            db.execSQL(CREATE_MESSAGE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {

            Log.d("Backpacker list", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);

            Log.d("Message list", "Deleting all data!");
            db.execSQL(MessengerDB.DROP_BACKPACKER_TABLE);
            db.execSQL(MessengerDB.DROP_MESSAGE_TABLE);
            onCreate(db);
        }
    }

    // database object and database helper object
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Context context;

    // constructor
    public MessengerDB(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    private void broadcastTaskModified() {
        Intent intent = new Intent(TASK_MODIFIED);
        context.sendBroadcast(intent);
    }

    // public methods
    public ArrayList<User> getBackpackerList() {

        String orderBy = BACKPACKER_LAST_MSG + " DESC";

        ArrayList<User> backpackers = new ArrayList<User>();
        openReadableDB();
        Cursor cursor = db.query(BACKPACKER_TABLE,
                null, null, null, null, null, orderBy);
        while (cursor.moveToNext()) {
            User backpacker = new User();
            backpacker.setUserName(cursor.getString(BACKPACKER_NAME_COL));
            backpacker.setUserId(cursor.getString(BACKPACKER_USERID_COL));
            backpacker.setLastMsg(cursor.getLong(BACKPACKER_LAST_MSG_COL));
            backpacker.setCount(cursor.getInt(BACKPACKER_COUNT_COL));

            long date = cursor.getLong(BACKPACKER_LAST_MSG_COL);
            int count = cursor.getInt(BACKPACKER_COUNT_COL);

            backpackers.add(backpacker);
        }
        cursor.close();
        closeDB();
        return backpackers;
    }

    public ArrayList<Message> getMessage(String userId) {

        String orderBy = MESSAGE_SENT_AT + " DESC";

        ArrayList<Message> Messages = new ArrayList<Message>();
        String where = MESSAGE_BACKPACKER_SENDER + "=? OR "+MESSAGE_BACKPACKER_RECEIVER+"=?";
        String[] whereArgs = {userId,userId};

        openReadableDB();
        Cursor cursor = db.query(MESSAGE_TABLE, null,
                where, whereArgs, null, null, orderBy);
        while (cursor.moveToNext()) {
            Message message = new Message();
            message.setMsg(cursor.getString(MESSAGE_MSG_COL));
            message.setBackpackerReceiver(cursor.getString(MESSAGE_BACKPACKER_RECEIVER_COL));
            message.setBackpackerSender(cursor.getString(MESSAGE_BACKPACKER_SENDER_COL));
            message.setSentAt(cursor.getLong(MESSAGE_SENT_AT_COL));

            Messages.add(message);
        }
        cursor.close();
        this.closeDB();

        return Messages;
    }
    public void ClearDB()
    {
        String where = "1==1";

        this.openWriteableDB();
        int rowCount = db.delete(BACKPACKER_TABLE, where, null);
        this.closeDB();

        broadcastTaskModified();

        this.openWriteableDB();
        int rowCount2 = db.delete(MESSAGE_TABLE, where, null);
        this.closeDB();

        broadcastTaskModified();


        //db.execSQL(MessengerDB.DROP_BACKPACKER_TABLE);
       // db.execSQL(MessengerDB.DROP_MESSAGE_TABLE);
    }
    public boolean checkIfExists(String id) {
        String where = BACKPACKER_USERID + "= ?";
        String[] whereArgs = {id};

        this.openReadableDB();
        Cursor cursor = db.query(BACKPACKER_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            this.closeDB();
            return false;
        }
        else
        {
            cursor.close();
            this.closeDB();
            return true;

        }
    }
//
//    private static Task getTaskFromCursor(Cursor cursor) {
//        if (cursor == null || cursor.getCount() == 0) {
//            return null;
//        } else {
//            try {
//                Task task = new Task(
//                        cursor.getInt(TASK_ID_COL),
//                        cursor.getInt(TASK_LIST_ID_COL),
//                        cursor.getString(TASK_NAME_COL),
//                        cursor.getString(TASK_NOTES_COL),
//                        cursor.getString(TASK_COMPLETED_COL),
//                        cursor.getString(TASK_HIDDEN_COL));
//                return task;
//            } catch (Exception e) {
//                return null;
//            }
//        }
//    }

    public long insertBackpacker(String senderUserId,String senderUserName) {
        ContentValues cv = new ContentValues();
        //cv.put(BACKPACKER_ID, task.getListId());
        cv.put(BACKPACKER_NAME, senderUserName);
        cv.put(BACKPACKER_USERID, senderUserId);
        cv.put(BACKPACKER_LAST_MSG, System.currentTimeMillis());
        cv.put(BACKPACKER_COUNT, 1);

        this.openWriteableDB();
        long rowID = db.insert(BACKPACKER_TABLE, null, cv);
        this.closeDB();

        broadcastTaskModified();

        return rowID;
    }
    public long insertNewChat(String senderUserId,String senderUserName) {
        ContentValues cv = new ContentValues();
        //cv.put(BACKPACKER_ID, task.getListId());
        cv.put(BACKPACKER_NAME, senderUserName);
        cv.put(BACKPACKER_USERID, senderUserId);
        cv.put(BACKPACKER_LAST_MSG, System.currentTimeMillis());
        cv.put(BACKPACKER_COUNT, 0);

        this.openWriteableDB();
        long rowID = db.insert(BACKPACKER_TABLE, null, cv);
        this.closeDB();

        broadcastTaskModified();

        return rowID;
    }
    public void updateBackpacker(String senderUserId,String senderUserName) {
        this.openWriteableDB();

            db.execSQL("UPDATE " + BACKPACKER_TABLE + " SET " + BACKPACKER_COUNT + "=" + "backpacker_count+" + 1 + " WHERE " + BACKPACKER_USERID + "=?",
                    new String[]{senderUserId});

            db.execSQL("UPDATE " + BACKPACKER_TABLE + " SET " + BACKPACKER_NAME + "='" + senderUserName + "', " + BACKPACKER_LAST_MSG + "=" + System.currentTimeMillis() + " WHERE " + BACKPACKER_USERID + "=?",
                    new String[]{senderUserId});

        this.closeDB();


    }
    public void resetProfleCount(String senderUserId) {
        this.openWriteableDB();

        db.execSQL("UPDATE " + BACKPACKER_TABLE + " SET " + BACKPACKER_COUNT + "=0 WHERE " + BACKPACKER_USERID + "=?",
                new String[]{senderUserId});


        this.closeDB();
    }
    public long insertMessage(String senderUserId,String message, String recieverUserId) {

        ContentValues cv = new ContentValues();
        //cv.put(BACKPACKER_ID, task.getListId());
        cv.put(MESSAGE_MSG, message);
        cv.put(MESSAGE_BACKPACKER_RECEIVER, recieverUserId);
        cv.put(MESSAGE_BACKPACKER_SENDER, senderUserId);
        cv.put(MESSAGE_SENT_AT, System.currentTimeMillis());

        this.openWriteableDB();
        long rowID = db.insert(MESSAGE_TABLE, null, cv);
        this.closeDB();

        broadcastTaskModified();

        return rowID;
    }

//    public int deleteTask(long id) {
//        String where = TASK_ID + "= ?";
//        String[] whereArgs = {String.valueOf(id)};
//
//        this.openWriteableDB();
//        int rowCount = db.delete(TASK_TABLE, where, whereArgs);
//        this.closeDB();
//
//        broadcastTaskModified();
//
//        return rowCount;
//    }

//    public String[] getTopTaskNames(int taskCount) {
//        String where = TASK_COMPLETED + "= '0'";
//        String orderBy = TASK_COMPLETED + " DESC";
//        this.openReadableDB();
//        Cursor cursor = db.query(TASK_TABLE, null,
//                where, null, null, null, orderBy);
//
//        String[] taskNames = new String[taskCount];
//        for (int i = 0; i < taskCount; i++) {
//            if (cursor.moveToNext()) {
//                Task task = getTaskFromCursor(cursor);
//                taskNames[i] = task.getName();
//            }
//        }
//
//        if (cursor != null)
//            cursor.close();
//        db.close();
//
//        return taskNames;
//    }

    /*
     * Methods for content provider
     * NOTE: You don't close the DB connection after executing
     * a query, insert, update, or delete operation
     */
//    public Cursor genericQuery(String[] projection, String where,
//                               String[] whereArgs, String orderBy) {
//        this.openReadableDB();
//        return db.query(TASK_TABLE, projection, where, whereArgs, null, null, orderBy);
//    }
//
//    public long genericInsert(ContentValues values) {
//        this.openWriteableDB();
//        return db.insert(TASK_TABLE, null, values);
//    }
//
//    public int genericUpdate(ContentValues values, String where,
//                             String[] whereArgs) {
//        this.openWriteableDB();
//        return db.update(TASK_TABLE, values, where, whereArgs);
//    }
//
//    public int genericDelete(String where, String[] whereArgs) {
//        this.openWriteableDB();
//        return db.delete(TASK_TABLE, where, whereArgs);
//    }
}

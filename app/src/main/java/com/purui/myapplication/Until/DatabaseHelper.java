package com.purui.myapplication.Until;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangruyi on 2017/7/3.
 */

public class DatabaseHelper  extends SQLiteOpenHelper{


    private static final  String TAG ="SQL";
    private static final  int DATABASE_VERSION=1;
    private static final  String DATABASE_NAME="QBC.db";
    /**
     * @param context  上下文环境（例如，一个 Activity）
     * @param name   数据库名字
     * @param factory  一个可选的游标工厂（通常是 Null）
     * @param version  数据库模型版本的整数
     *
     * 会调用父类 SQLiteOpenHelper的构造函数
     */


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    /**
     *  在数据库第一次创建的时候会调用这个方法
     *
     *根据需要对传入的SQLiteDatabase 对象填充表和初始化数据。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS person (personid integer primary key autoincrement, name varchar(20), age INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD phone VARCHAR(12)");
    }
}

/*
创建数据库

        通过openOrCreateDatabase(String path, SQLiteDatabase.CursorFactory factory)方法创建，如果库已创建，则打开数据库。


SQLiteDatabase db =this.openOrCreateDatabase("test_db.db", Context.MODE_PRIVATE, null);

创建表

        SQLiteDatabase没有提供创建表的方法，所以要靠execSQL()方法来实现。看名字也知道execSQL()用于直接执行sql的。




String sql="create table t_user (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,password TEXT NOT NULL)";
db.execSQL(sql);



增

        使用SQLiteDatabase的insert(String table, String nullColumnHack, ContentValues values)方法插入数据。ContentValues 类，类似于java中的Map，以键值对的方式保存数据。




ContentValues values=new ContentValues();
values.put("name", "liangjh");
values.put("password", "123456");
db.insert("t_user", "id", values);



删

        删除数据就比较直接了。使用SQLiteDatabase的delete(String table, String whereClause, String[] whereArgs)实现。如果不想把参数写在whereArgs里面，可以直接把条件写在whereClause里面。




// 方式1 直接将条件写入到条件里面（个人觉得容易被注入，但其实数据都在客户端，没啥安全性可言）
db.delete("t_user", "id=1", null);
// 方式2 条件分开写，感觉比较安全
db.delete("t_user", "name=? and password =?", new String[]{"weiyg","112233"});



查

        查询有2个方法，query()和rawQuery()两个方法，区别在于query()是将sql里面的各参数提取出query()对应的参数中。可参考下面例子。





复制代码
// 使用rawQuery
// Cursor c = db.rawQuery("select * from t_user", null);
// db.rawQuery("select * from t_user where id=1", null);
// db.rawQuery("select * from t_user where id=?", new String[]{"1"});

// 使用query()
Cursor c = db.query("t_user", new String[]{"id","name"}, "name=?", new String[]{"weiyg"}, null, null, null);
c.moveToFirst();
while(!c.isAfterLast()){
    String msg="";
    for(int i=0,j=c.getColumnCount();i<j;i++){
        msg+="--"+c.getString(i);
    }
    Log.v("SQLite", "data:"+msg);
    c.moveToNext();
}

复制代码



改

        使用SQLiteDatabase的update(String table, ContentValues values, String whereClause, String[] whereArgs)可以修改数据。whereClause和whereArgs用于设置其条件。ContentValues对象为数据。




复制代码
ContentValues values=new ContentValues();
values.put("password", "111111");
// 方式1 条件写在字符串内
db.update("t_user", values, "id=1", null);
// 方式2 条件和字符串分开
db.update("t_user", values, "name=? or password=?",new String[]{"weiyg","123456"});

复制代码



其它

无论何时，打开的数据库，记得关闭。


db.close()

 */
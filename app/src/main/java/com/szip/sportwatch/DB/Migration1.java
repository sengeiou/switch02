package com.szip.sportwatch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.sportwatch.DB.dbModel.SportData;

import static com.szip.sportwatch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration1 extends AlterTableMigration<SportData> {
    public Migration1(Class<SportData> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        //所有Java标准的数据类型(boolean、byte、short、int、long、float、double等)及相应的包装类，
        // 以及String，当然我们还默认提供了对java.util.Date、java.sql.Date与Calendar的支持。
        addColumn(SQLiteType.get(int.class.getName()), "heart");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "stride");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "altitude");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "pole");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "height");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "step");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "temp");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "speedPerHour");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "speedArray");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "heartArray");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "strideArray");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "altitudeArray");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "deviceCode");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "tempArray");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "speedPerHourArray");//基本数据类型
    }
}

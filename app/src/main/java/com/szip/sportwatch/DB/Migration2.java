package com.szip.sportwatch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.sportwatch.DB.dbModel.AnimalHeatData;
import com.szip.sportwatch.DB.dbModel.SportData;

import static com.szip.sportwatch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration2 extends AlterTableMigration<AnimalHeatData> {
    public Migration2(Class<AnimalHeatData> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        //所有Java标准的数据类型(boolean、byte、short、int、long、float、double等)及相应的包装类，
        // 以及String，当然我们还默认提供了对java.util.Date、java.sql.Date与Calendar的支持。

        // 使用如下：这里值添加remarks2
        //addColumn(SQLiteType.get(long.class.getName()), "money");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "deviceCode");//基本数据类型
        //addColumn(SQLiteType.get(double.class.getName()), "money");//基本数据类型:浮点数
//        addColumn(SQLiteType.get(String.class.getName()), "money");//基本数据类型:浮点数
//        addColumn(SQLiteType.TEXT, "remarks2");

    }
}

package com.szip.sportwatch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.SleepData;

import static com.szip.sportwatch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration7 extends AlterTableMigration<SleepData> {

    public Migration7(Class<SleepData> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.get(String.class.getName()), "deviceCode");//基本数据类型
    }
}

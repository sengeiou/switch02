package com.szip.sportwatch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.sportwatch.DB.dbModel.BloodOxygenData;
import com.szip.sportwatch.DB.dbModel.BloodPressureData;

import static com.szip.sportwatch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration4 extends AlterTableMigration<BloodPressureData> {
    public Migration4(Class<BloodPressureData> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.get(String.class.getName()), "deviceCode");//基本数据类型
    }
}

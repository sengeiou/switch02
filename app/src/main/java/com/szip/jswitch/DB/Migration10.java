package com.szip.jswitch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.jswitch.DB.dbModel.BodyFatData;
import com.szip.jswitch.DB.dbModel.BodyFatData_Table;
import com.szip.jswitch.DB.dbModel.SportWatchAppFunctionConfigDTO;

import static com.szip.jswitch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration10  extends AlterTableMigration<BodyFatData> {

    public Migration10(Class<BodyFatData> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.get(float.class.getName()), "ageOfBody");//基本数据类型
        addColumn(SQLiteType.get(String.class.getName()), "weightRange");//基本数据类型
        addColumn(SQLiteType.get(float.class.getName()), "idealWeight");//基本数据类型
    }
}
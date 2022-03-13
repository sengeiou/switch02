package com.szip.jswitch.DB;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.szip.jswitch.DB.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.jswitch.DB.dbModel.StepData;

import static com.szip.jswitch.DB.AppDatabase.VERSION;

@Migration(version = VERSION, database = AppDatabase.class)//=2的升级
public class Migration9  extends AlterTableMigration<SportWatchAppFunctionConfigDTO> {

    public Migration9(Class<SportWatchAppFunctionConfigDTO> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.get(int.class.getName()), "watchPlateGroupId");//基本数据类型
        addColumn(SQLiteType.get(int.class.getName()), "sportSync");//基本数据类型
    }
}

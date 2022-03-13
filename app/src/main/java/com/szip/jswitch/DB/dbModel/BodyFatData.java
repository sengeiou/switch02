package com.szip.jswitch.DB.dbModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.jswitch.DB.AppDatabase;

@Table(database = AppDatabase.class)
public class BodyFatData extends BaseModel {
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;
    //身体年龄
    @Column
    public float ageOfBody;
    //BMI指数
    @Column
    public float bmi;
    //BMI指数区间
    @Column
    public String bmiRange;
    //基础代谢
    @Column
    public float bmr;
    //基础代谢区间
    @Column
    public String bmrRange;
    //体型
    @Column
    public int bodyShape;
    //去脂体重
    @Column
    public float fatFreeBodyWeight;
    //内脏脂肪等级
    @Column
    public float levelOfVisceralFat;
    //内脏脂肪等级区间
    @Column
    public String levelOfVisceralFatRange;
    //肥胖等级
    @Column
    public int obesityLevel;
    //肥胖等级区间
    @Column
    public String obesityLevelList;
    //体脂率
    @Column
    public float ratioOfFat;
    //体脂率区间
    @Column
    public String ratioOfFatRange;
    //肌肉率
    @Column
    public float ratioOfMuscle;
    //肌肉率区间
    @Column
    public String ratioOfMuscleRange;
    //蛋白质率
    @Column
    public float ratioOfProtein;
    //蛋白质率区间
    @Column
    public String ratioOfProteinRange;
    //骨骼肌率
    @Column
    public float ratioOfSkeletalMuscle;
    //骨骼肌率区间
    @Column
    public String ratioOfSkeletalMuscleRange;
    //皮下脂肪率
    @Column
    public float ratioOfSubcutaneousFat;
    //皮下脂肪率区间
    @Column
    public String ratioOfSubcutaneousFatRange;
    //身体评分
    @Column
    public int score;
    //体重
    @Column
    public float weight;
    //体重区间
    @Column
    public String weightRange;
    //骨量
    @Column
    public float weightOfBone;
    //骨量区间
    @Column
    public String weightOfBoneRange;
    //脂肪量
    @Column
    public float weightOfFat;
    //肌肉量
    @Column
    public float weightOfMuscle;
    //蛋白质量
    @Column
    public float weightOfProtein;
    //水分重量
    @Column
    public float weightOfWater;

}

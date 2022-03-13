package com.szip.jswitch.Model;

import com.szip.jswitch.Model.HttpBean.BaseApi;

public class BodyFatModel extends BaseApi {
    private Details details;


    public Details getDetails() {
        return details;
    }

    public class Details{
        //身体年龄
        float ageOfBody;
        //BMI指数
        float bmi;
        //BMI指数区间
        float[] bmiRange;
        //基础代谢
        float bmr;
        //基础代谢区间
        float[] bmrRange;
        //体型
        int bodyShape;
        //去脂体重
        float fatFreeBodyWeight;
        //内脏脂肪等级
        float levelOfVisceralFat;
        //内脏脂肪等级区间
        float[] levelOfVisceralFatRange;
        //肥胖等级
        int obesityLevel;
        //肥胖等级区间
        int[] obesityLevelList;
        //体脂率
        float ratioOfFat;
        //体脂率区间
        float[] ratioOfFatRange;
        //肌肉率
        float ratioOfMuscle;
        //肌肉率区间
        float[] ratioOfMuscleRange;
        //蛋白质率
        float ratioOfProtein;
        //蛋白质率区间
        float[] ratioOfProteinRange;
        //骨骼肌率
        float ratioOfSkeletalMuscle;
        //骨骼肌率区间
        float[] ratioOfSkeletalMuscleRange;
        //皮下脂肪率
        float ratioOfSubcutaneousFat;
        //皮下脂肪率区间
        float[] ratioOfSubcutaneousFatRange;
        //身体评分
        int score;
        //体重
        float weight;
        //体重区间
        float[] weightRange;
        //骨量
        float weightOfBone;
        //骨量区间
        float[] weightOfBoneRange;
        //脂肪量
        float weightOfFat;
        //肌肉量
        float weightOfMuscle;
        //蛋白质量
        float weightOfProtein;
        //水分重量
        float weightOfWater;

        public float getAgeOfBody() {
            return ageOfBody;
        }

        public float getBmi() {
            return bmi;
        }

        public float[] getBmiRange() {
            return bmiRange;
        }

        public float getBmr() {
            return bmr;
        }

        public float[] getBmrRange() {
            return bmrRange;
        }

        public int getBodyShape() {
            return bodyShape;
        }

        public float getFatFreeBodyWeight() {
            return fatFreeBodyWeight;
        }

        public float getLevelOfVisceralFat() {
            return levelOfVisceralFat;
        }

        public float[] getLevelOfVisceralFatRange() {
            return levelOfVisceralFatRange;
        }

        public int getObesityLevel() {
            return obesityLevel;
        }

        public int[] getObesityLevelList() {
            return obesityLevelList;
        }

        public float getRatioOfFat() {
            return ratioOfFat;
        }

        public float[] getRatioOfFatRange() {
            return ratioOfFatRange;
        }

        public float getRatioOfMuscle() {
            return ratioOfMuscle;
        }

        public float[] getRatioOfMuscleRange() {
            return ratioOfMuscleRange;
        }

        public float getRatioOfProtein() {
            return ratioOfProtein;
        }

        public float[] getRatioOfProteinRange() {
            return ratioOfProteinRange;
        }

        public float getRatioOfSkeletalMuscle() {
            return ratioOfSkeletalMuscle;
        }

        public float[] getRatioOfSkeletalMuscleRange() {
            return ratioOfSkeletalMuscleRange;
        }

        public float getRatioOfSubcutaneousFat() {
            return ratioOfSubcutaneousFat;
        }

        public float[] getRatioOfSubcutaneousFatRange() {
            return ratioOfSubcutaneousFatRange;
        }

        public int getScore() {
            return score;
        }

        public float getWeight() {
            return weight;
        }

        public float[] getWeightRange() {
            return weightRange;
        }

        public float getWeightOfBone() {
            return weightOfBone;
        }

        public float[] getWeightOfBoneRange() {
            return weightOfBoneRange;
        }

        public float getWeightOfFat() {
            return weightOfFat;
        }

        public float getWeightOfMuscle() {
            return weightOfMuscle;
        }

        public float getWeightOfProtein() {
            return weightOfProtein;
        }

        public float getWeightOfWater() {
            return weightOfWater;
        }
    }
}

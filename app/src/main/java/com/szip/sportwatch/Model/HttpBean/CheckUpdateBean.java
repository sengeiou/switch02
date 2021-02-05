package com.szip.sportwatch.Model.HttpBean;

public class CheckUpdateBean extends BaseApi{

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        int supported;
        Version newVersion;

        public int getSupported() {
            return supported;
        }

        public void setSupported(int supported) {
            this.supported = supported;
        }

        public Version getNewVersion() {
            return newVersion;
        }

        public void setNewVersion(Version newVersion) {
            this.newVersion = newVersion;
        }
    }


    public class Version{
        String versionNumber;
        String url;
        int fileSize;
        String mark;
        String createTime;

        public String getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}

package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.request.PostJsonListRequest;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/6/14.
 */

public class PostJsonListBuider extends OkHttpRequestBuilder<PostJsonListBuider> implements HasParamsable{
    private List<PostJsonListBuider.FileInput> files = new ArrayList<>();

    @Override
    public RequestCall build()
    {
        return new PostJsonListRequest(url, tag, params, headers, files,id).build();
    }

    public PostJsonListBuider files(String key, Map<String, File> files)
    {
        for (String filename : files.keySet())
        {
            this.files.add(new PostJsonListBuider.FileInput(key, filename, files.get(filename)));
        }
        return this;
    }

    public PostJsonListBuider addFile(String name, String filename, File file)
    {
        files.add(new PostJsonListBuider.FileInput(name, filename, file));
        return this;
    }

    public static class FileInput
    {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file)
        {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString()
        {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }



    @Override
    public PostJsonListBuider params(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public PostJsonListBuider addParams(String key, String val)
    {
        if (this.params == null)
        {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

//    @Override
//    public PostJsonBuilder addParams(String key, int val) {
//
//        if (this.params1 == null)
//        {
//            params1 = new LinkedHashMap<>();
//        }
//        params1.put(key, val);
//        return this;
//    }
}

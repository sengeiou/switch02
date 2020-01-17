package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.request.PostJsonRequest;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class PostJsonBuilder extends OkHttpRequestBuilder<PostJsonBuilder> implements HasParamsable {
    private List<FileInput> files = new ArrayList<>();

    @Override
    public RequestCall build()
    {
        return new PostJsonRequest(url, tag, params, headers, files,id).build();
    }

    public PostJsonBuilder files(String key, Map<String, File> files)
    {
        for (String filename : files.keySet())
        {
            this.files.add(new FileInput(key, filename, files.get(filename)));
        }
        return this;
    }

    public PostJsonBuilder addFile(String name, String filename, File file)
    {
        files.add(new FileInput(name, filename, file));
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
    public PostJsonBuilder params(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public PostJsonBuilder addParams(String key, String val)
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

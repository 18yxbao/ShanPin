package com.example.shanpin.bean;

public class ImageBean {
    /**
     * success : true
     * code : success
     * message : Upload success.
     * data : {"file_id":0,"width":689,"height":690,"filename":"微信图片_20201207200743.jpg","storename":"2UqLwIrJPmhH936.jpg","size":30566,"path":"/2020/12/07/2UqLwIrJPmhH936.jpg","hash":"a5bicGLqesFTOSyuhA4HlpxjPt","url":"https://i.loli.net/2020/12/07/2UqLwIrJPmhH936.jpg","delete":"https://sm.ms/delete/a5bicGLqesFTOSyuhA4HlpxjPt","page":"https://sm.ms/image/2UqLwIrJPmhH936"}
     * RequestId : 12DFE077-30CD-431F-8035-C7625B4FBD8E
     */

    private boolean success;
    private String code;
    private String message;
    /**
     * file_id : 0
     * width : 689
     * height : 690
     * filename : 微信图片_20201207200743.jpg
     * storename : 2UqLwIrJPmhH936.jpg
     * size : 30566
     * path : /2020/12/07/2UqLwIrJPmhH936.jpg
     * hash : a5bicGLqesFTOSyuhA4HlpxjPt
     * url : https://i.loli.net/2020/12/07/2UqLwIrJPmhH936.jpg
     * delete : https://sm.ms/delete/a5bicGLqesFTOSyuhA4HlpxjPt
     * page : https://sm.ms/image/2UqLwIrJPmhH936
     */

    private DataBean data;
    private String RequestId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }

    public static class DataBean {
        private int file_id;
        private int width;
        private int height;
        private String filename;
        private String storename;
        private int size;
        private String path;
        private String hash;
        private String url;
        private String delete;
        private String page;

        public int getFile_id() {
            return file_id;
        }

        public void setFile_id(int file_id) {
            this.file_id = file_id;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getStorename() {
            return storename;
        }

        public void setStorename(String storename) {
            this.storename = storename;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }
}

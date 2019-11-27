package connectis.dbstorage;

public class DatabaseFileManage {
    private String filename;
    private Long size;
    private String downloadUri;
    private String deleteUri;
    private String fileType;

    public String getFilename() {
        return filename;
    }

    public Long getSize() {
        return size;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public String getDeleteUri() {
        return deleteUri;
    }

    public String getFileType() {
        return fileType;
    }

    public DatabaseFileManage(Builder builder) {
        this.filename = builder.filename;
        this.size = builder.size;
        this.downloadUri = builder.downloadUri;
        this.deleteUri = builder.deleteUri;
        this.fileType = builder.fileType;
    }

    public static class Builder {
        private String filename;
        private Long size;
        private String downloadUri;
        private String deleteUri;
        private String fileType;



        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public Builder downloadUri(String downloadUri) {
            this.downloadUri = downloadUri;
            return this;
        }

        public Builder deleteUri(String deleteUri) {
            this.deleteUri = deleteUri;
            return this;
        }

        public Builder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }


        public DatabaseFileManage build() {
            return new DatabaseFileManage(this);
        }
    }
}

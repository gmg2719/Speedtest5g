package cn.nokia.speedtest5g.app.uitl.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.nokia.speedtest5g.app.uitl.ftp.FTP.UploadProgressListener;

public class ProgressInputStream extends InputStream {

    private static final int TEN_KILOBYTES = 1024;  //每上传1K返回一次

    private InputStream inputStream;

    private long progress;
    private long lastUpdate;
    
    private long filsSize;

    private boolean closed;
    
    private UploadProgressListener listener;
    private File localFile;
    
    public ProgressInputStream(InputStream inputStream,UploadProgressListener listener,File localFile) {
        this.inputStream = inputStream;
        this.progress = 0;
        this.lastUpdate = 0;
        this.listener = listener;
        this.localFile = localFile;
        
        this.closed = false;
        this.filsSize = localFile.length();
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        this.listener.onUploadSumProgress(count,progress, this.filsSize);
        return count;
    }

    private int lastR;
    private long maybeUpdateDisplay(long progress, long lastUpdate) {
    	 if (progress - lastUpdate > TEN_KILOBYTES) {
             lastUpdate = progress;
             long fize = this.localFile.length();
 			 float num = (float)progress / (float)fize;
 			 int result = (int) (num * 100);
 			 if (result > lastR + 1) {
 				lastR = result;
 	            this.listener.onUploadProgress(FtpState.FTP_UPLOAD_LOADING, lastR, this.localFile);
 			 }
         }
         return lastUpdate;
    }
}

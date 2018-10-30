package com.example.aw.conversion;

public interface VideoCompressionCallback {

    void onCompressUpdateProgress(int progress,String currentIndexString);
    
    void onCompressSuccessful(String path);
    
    void onCompressFailed(String path);

    void onCompressFinished(String currentIndexString);
}

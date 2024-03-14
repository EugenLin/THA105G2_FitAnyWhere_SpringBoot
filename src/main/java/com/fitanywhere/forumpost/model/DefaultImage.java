package com.fitanywhere.forumpost.model;

public class DefaultImage {
	
	 // 預設圖片的Base64編碼
    private static final String DEFAULT_PIC_BASE64 = "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";


    // 可調用預設圖片的編碼
    public static String getDefaultPicBase64() {
        return DEFAULT_PIC_BASE64;
    }

}

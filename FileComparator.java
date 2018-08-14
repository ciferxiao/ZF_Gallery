package com.cifer.xiaogallery;

import java.io.File;
import java.util.Comparator;

/**
 * Created by cifer
 * on 8/10/18.
 */

class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        if (file1.lastModified() < file2.lastModified()) {
            return 1;// 最后修改的文件在前
        } else {
            return -1;
        }
    }
}

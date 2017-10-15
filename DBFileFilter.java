/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author gsb
 */
public class DBFileFilter implements FilenameFilter{
    private String ext;
    
    DBFileFilter(String ext){
        this.ext = "."+ext.toLowerCase();
    }
    
    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(ext);
    }
    
}

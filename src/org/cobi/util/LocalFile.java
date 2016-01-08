/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cobi.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 *
 * @author mxli
 */
public class LocalFile {

    static public boolean retrieveData(String fileName, List<String[]> arry, int[] orgIndices,
            String delimiter) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = null;
        String[] cells = null;
        String[] row = null;
        int selectedColNum = orgIndices.length;
        int i;

        while ((line = br.readLine()) != null) {
            //line = line.trim();
            if (line.trim().length() == 0) {
                continue;
            }
            cells = line.split(delimiter, -1);
            row = new String[selectedColNum];
            for (i = 0; i < selectedColNum; i++) {
                row[i] = cells[orgIndices[i]];
            }
            arry.add(row);
        }
        br.close();
        return true;
    }

    static public boolean retrieveData(String fileName, List<String[]> arry, String delimiter) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = null;
            String[] cells = null;

            while ((line = br.readLine()) != null) {
                //line = line.trim();
                if (line.trim().length() == 0) {
                    continue;
                }
                cells = line.split(delimiter, -1);
                arry.add(cells);
            }
            br.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
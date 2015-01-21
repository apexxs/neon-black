/*
 * Copyright (c) 2014.
 * Apex Expert Solutions
 * http://www.apexxs.com
 *
 *  ====================================================================
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 * ====================================================================
 *
 *
 *
 * ====================================================================
 */

package com.apexxs.neonblack.setup;


import com.apexxs.neonblack.dao.BorderData;
import com.apexxs.neonblack.utilities.MD5Utilities;
import org.apache.solr.client.solrj.SolrServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class NaturalEarthLoader implements SolrLoader
{
    public int load(SolrServer server)
    {
        Configuration config = Configuration.getInstance();
        int count = 0;
        try {
            System.out.println("*****Loading Natural Earth borders*****");
            File neDir = new File(config.getPolygonDirectory());
            File[] neFiles = neDir.listFiles();

            for(File neFile : neFiles) {
                try {
                    System.out.println("Processing " + neFile.getName());
                    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(neFile.getAbsolutePath()), "UTF-8"));
                    String line;
                    while ((line = r.readLine()) != null) {
                        BorderData bd = new BorderData();
                        String[] tokens = line.split("\t");

                        bd.fips = tokens[0];
                        bd.iso2 = tokens[1];
                        bd.iso3 = tokens[2];
                        bd.name = tokens[3];
                        bd.shapeType = tokens[4];
                        bd.wkt = tokens[5];
                        bd.id = MD5Utilities.getMD5Hash(bd.wkt);

                        try {
                            server.addBean(bd);
                            count++;
                            System.out.println("Added " + bd.name);
                        } catch (Exception ex) {
                            System.out.println("#### Error adding ##### " + bd.name);
                        }
                    }
                    r.close();
                } catch (Exception ex) {
                    System.out.println("Error in Natural Earth Border Loader.load()");
                    ex.printStackTrace();
                }
            }
            //return count;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return count;
    }
}

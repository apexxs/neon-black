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
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class TMBorderLoader implements SolrLoader
{
    public int load(SolrServer server)
    {
        int count = 0;
        try
        {
            URL url = TMBorderLoader.class.getResource("/TM_WORLD_BORDERS-0.3.csv");
            File file = new File(url.toURI());

            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;

            r.readLine();

            while ((line = r.readLine()) != null)
            {
                BorderData bd = new BorderData();

                String[] tokens = line.split(";");
                bd.fips = tokens[1];
                bd.iso2 = tokens[2];
                bd.iso3 = tokens[3];
                bd.name = tokens[5];
                bd.wkt = tokens[0].replace("\"", StringUtils.EMPTY);
                bd.id = MD5Utilities.getMD5Hash(bd.wkt);
                bd.shapeType = "adm0";
                server.addBean(bd);
                System.out.println("Added " + bd.name);
                count++;
            }
            r.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error in TMBorderLoader.load()");
            ex.printStackTrace();
        }
        return count;
    }
}

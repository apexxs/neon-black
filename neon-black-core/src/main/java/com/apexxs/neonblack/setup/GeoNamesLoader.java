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
 * GeoNamesLoader.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.setup;


import com.apexxs.neonblack.dao.GeoNamesEntry;
import com.apexxs.neonblack.solr.SolrServerFactory;
import com.apexxs.neonblack.solr.SolrUtilities;
import org.apache.solr.client.solrj.SolrServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class GeoNamesLoader implements SolrLoader
{
    public GeoNamesLoader()
    {
    }

    public int load(SolrServer server)
    {

        int count = 0;

        try
        {
            URL url = GeoNamesLoader.class.getResource("/allCountries.txt");
            File file = new File(url.toURI());

            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            // Throw away the first line
            r.readLine();

            while ((line = r.readLine()) != null)
            {
                GeoNamesEntry entry = new GeoNamesEntry(line);
                server.addBean(entry);
                count++;
                if(count % 50000 == 0)
                {
                    System.out.println("Added " + count + " lines...");
                }
            }
            r.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return count;
    }

    public void loadSupplemental(String fname)
    {
        SolrServerFactory factory = new SolrServerFactory();
        SolrServer server;
        int count = 0;

        server = factory.getHTTPServer("geonames");

        try
        {
            File file = new File(fname);
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;

            ArrayList<GeoNamesEntry> entries = new ArrayList<>(25000);
            while ((line = r.readLine()) != null)
            {
                GeoNamesEntry entry = new GeoNamesEntry(line);

                entries.add(entry);
                count++;
                if(count % 25000 == 0)
                {
                    server.addBeans(entries);
                    entries.clear();
                    System.out.println("Added " + count + " lines...");
                }
            }

            server.addBeans(entries);
            server.commit();
            server.optimize();
            r.close();
            System.out.println("Supplemental index load complete - inserted " + count + " documents.");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            server.shutdown();
        }
    }
}

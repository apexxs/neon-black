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

package com.apexxs.neonblack.solr;


import com.apexxs.neonblack.setup.Configuration;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrUtilities
{
    public final static Logger logger = LoggerFactory.getLogger(SolrUtilities.class);
    private SolrServerFactory factory = new SolrServerFactory();
    SolrServer server;

    public SolrUtilities() {}

    public void clearSolrIndices()
    {
        clearGeodata();
        clearGeonames();
    }

    public void clearGeonames()
    {
        server = factory.getHTTPServer("geonames");
        try
        {
            server.deleteByQuery("*:*");
            server.commit();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void clearGeodata()
    {
        server = factory.getHTTPServer("geodata");
        try
        {
            server.deleteByQuery("*:*");
            server.commit();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Boolean checkCoreStatus(String coreName)
    {
        Configuration config = Configuration.getInstance();
        server = factory.getHTTPServer(coreName);

        int connectionAttempts = 0;
        while (connectionAttempts <= 5)
        {
            try
            {
                SolrPingResponse spr = server.ping();
                server.shutdown();
                return true;
            }
            catch (Exception ex)
            {
                logger.error("Unable to connect to " + coreName + " core.  Make sure your instance of SOLR at " + config.getSolrURL() + " is available.");
                connectionAttempts++;
                try
                {
                    Thread.sleep(5000);
                }
                catch (Exception ex2)
                {
                    //Ignore it
                }
            }
        }

        return false;
    }
}

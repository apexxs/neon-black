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
 * SolrServerFactory.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.solr;

import com.apexxs.neonblack.setup.Configuration;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class SolrServerFactory
{
    public final static Logger logger = LoggerFactory.getLogger(SolrServerFactory.class);
    public SolrServerFactory() {}

/*
    public SolrServer getEmbeddedSolrServer(String coreName)
    {
        SolrServer server = null;
        URL solrDir = SolrServerFactory.class.getResource("/solr/solr.xml");
        File solrConfigFile = new File(solrDir.getFile());
        String path = solrConfigFile.getParent();

        try
        {
            CoreContainer container = new CoreContainer(path);
            container.load();
            server = new EmbeddedSolrServer(container, coreName);
        }
        catch(Exception ex)
        {
            System.out.println();
            ex.printStackTrace();
        }

        return server;
    }*/

    public SolrServer getHTTPServer(String coreName)
    {
        Configuration config = Configuration.getInstance();
        SolrServer server = null;
        String connString = config.getSolrURL() + coreName;
        try
        {
          server = new HttpSolrServer(connString);
        }
        catch(Exception ex)
        {
            logger.error("Error in getHTTPServer: " + ex.getMessage());
        }
        return server;
    }

    public SolrServer getConcurrentSolrServer(String coreName)
    {
        Configuration config = Configuration.getInstance();
        SolrServer server = null;
        String connString = config.getSolrURL() + coreName;
        try
        {
            server = new ConcurrentUpdateSolrServer(connString, 25000, 4);
        }
        catch(Exception ex)
        {
            logger.error("Error in getConcurrentSolrServer: " + ex.getMessage());
        }
        return server;
    }

    public SolrServer getConcurrentSolrServer(String coreName, int queueSize, int numThreads)
    {
        Configuration config = Configuration.getInstance();
        SolrServer server = null;
        String connString = config.getSolrURL() + coreName;
        try
        {
            server = new ConcurrentUpdateSolrServer(connString, queueSize, numThreads);
        }
        catch(Exception ex)
        {
            logger.error("Error in getConcurrentSolrServer: " + ex.getMessage());
        }
        return server;
    }
}

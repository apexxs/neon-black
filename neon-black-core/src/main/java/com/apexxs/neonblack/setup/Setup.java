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
 * Setup.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.setup;

import com.apexxs.neonblack.solr.SolrServerFactory;
import com.apexxs.neonblack.solr.SolrUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;

public class Setup
{
    static Logger logger = Logger.getLogger(Setup.class);

    public static void main(String[] args)
    {
        Configuration config = Configuration.getInstance();
        SolrLoader solrLoader = null;
        int count = 0;

        SolrUtilities solrUtilities = new SolrUtilities();
        if (!solrUtilities.checkCoreStatus("geonames"))
        {
            logger.error("Unable to establish connection to geonames core - Application terminated");
            System.exit(-1);
        }

        if (!solrUtilities.checkCoreStatus("geodata"))
        {
            logger.error("Unable to establish connection to geodata core - Application terminated");
            System.exit(-1);
        }

        logger.warn("Solr geonames and geodata indices will be cleared...");
        SolrUtilities sutil = new SolrUtilities();
//        sutil.clearSolrIndices();

        long startTime = System.currentTimeMillis();

        try
        {
            SolrServerFactory solrServerFactory = new SolrServerFactory();
            SolrServer server = solrServerFactory.getConcurrentSolrServer("geonames");

//            GeoNamesLoader geoNamesLoader = new GeoNamesLoader();
//            System.out.println("***Loading Geonames gazetteer***");
//            count = geoNamesLoader.load(server);
//            System.out.println("Committing and optimizing Geonames index....");
//            server.commit();
//            server.optimize();
//
//            System.out.println("Geonames index complete - inserted " + count + " documents.");



            server = solrServerFactory.getHTTPServer("geodata");
            if (StringUtils.equalsIgnoreCase(config.getPolygonType(), "gadm"))
            {
                System.out.println("***Loading GADM Border polygons***");
                solrLoader = new GADMBorderLoader();
                count = solrLoader.load(server);
            }
            else if(StringUtils.equalsIgnoreCase(config.getPolygonType(), "ne"))
            {
                System.out.println("***Loading Natural Earth Border polygons***");
                solrLoader = new NaturalEarthLoader();
                count = solrLoader.load(server);
            }
            else
            {
                System.out.println("***Loading Thematicmapping polygons***");
                solrLoader = new TMBorderLoader();
                count = solrLoader.load(server);
                solrLoader = new StateBorderLoader();
                count += solrLoader.load(server);
            }
            System.out.println("Committing and optimizing Geodata index....");
            server.commit();
            server.optimize();
            server.shutdown();
            System.out.println("***Loaded " + count + " polygons***");
        }
        catch(Exception ex)
        {
            logger.error("Error loading SOLR indices - " + ex.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("***Solr Index load complete***");
        System.out.println("Elapsed time: " + (endTime - startTime) / 60000 + " minutes");
    }
}

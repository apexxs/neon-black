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
 * Queries.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.solr;


import com.apexxs.neonblack.dao.BorderData;
import com.apexxs.neonblack.dao.GeoNamesEntry;
import com.apexxs.neonblack.setup.Configuration;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Queries
{
    private SolrServer geonamesCore;
    private SolrServer geodataCore;
    private Configuration config = Configuration.getInstance();
    public final static Logger logger = LoggerFactory.getLogger(Queries.class);

    public Queries()
    {
        SolrUtilities solrUtilities = new SolrUtilities();
        if(!solrUtilities.checkCoreStatus("geonames"))
        {
            logger.error("Unable to establish connection to geonames core - Application terminated");
            System.exit(-1);
        }

        if(!solrUtilities.checkCoreStatus("geodata"))
        {
            logger.error("Unable to establish connection to geodata core - Application terminated");
            System.exit(-1);
        }

        SolrServerFactory factory = new SolrServerFactory();
        this.geonamesCore = factory.getHTTPServer("geonames");
        this.geodataCore = factory.getHTTPServer("geodata");

        //Warm up the indices
        doNameQuery("Boston");
        doBorderQuery("-104.11 33");    // Roswell, NM
    }

    public List<GeoNamesEntry> doNameQuery(String searchTerm)
    {
        List<GeoNamesEntry> entries = new ArrayList<>();

        String sortString;
        if(config.populationSort())
        {
            sortString = "population desc, score desc";
        }
        else
        {
            sortString = "score desc";
        }

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "/nameSearcher");
        params.set("q", searchTerm);
        params.set("mm", "100%");
        params.set("qs", "3");
        params.set("fl", "*, score");
        params.set("sort", sortString);
        params.set("rows", config.getMaxHits());

        try
        {
            QueryResponse response = geonamesCore.query(params);
            entries = response.getBeans(GeoNamesEntry.class);

        }
        catch(Exception ex)
        {
            logger.error("Error in doNameQuery for argument " + searchTerm + " " + ex.getMessage());
        }

        return entries;
    }

    public List<GeoNamesEntry> doNameQuery(String searchTerm, String minShouldMatch)
    {
        List<GeoNamesEntry> entries = new ArrayList<>();

        String sortString;
        if(config.populationSort())
        {
            sortString = "population desc, score desc";
        }
        else
        {
            sortString = "score desc";
        }

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "/nameSearcher");
        params.set("q", searchTerm);
        params.set("mm", minShouldMatch);
        params.set("fl", "*, score");
        params.set("sort", sortString);
        params.set("rows", config.getMaxHits());

        try
        {
            QueryResponse response = geonamesCore.query(params);
            entries = response.getBeans(GeoNamesEntry.class);
        }
        catch(Exception ex)
        {
            logger.error("Error in doNameQuery for argument " + searchTerm + " " + ex.getMessage());
        }

        return entries;
    }

    public List<GeoNamesEntry> doSelectQuery(String searchTerm)
    {
        List<GeoNamesEntry> entries = new ArrayList<>();

        String sortString;
        if(config.populationSort())
        {
            sortString = "population desc, score desc";
        }
        else
        {
            sortString = "score desc";
        }

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "/select");
        params.set("q", searchTerm);
        params.set("fl", "*, score");
        params.set("sort", sortString);
        params.set("rows", config.getMaxHits());

        try
        {
            QueryResponse response = geonamesCore.query(params);
            entries = response.getBeans(GeoNamesEntry.class);

        }
        catch(Exception ex)
        {
            logger.error("Error in doSelectQuery for argument " + searchTerm + " " + ex.getMessage());
        }

        return entries;
    }

    public List<BorderData> doBorderQuery(String lonLat)
    {
        List<BorderData> entries = new ArrayList<>();

        String queryString = "shape:\"Intersects(" + lonLat + ")\"";

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.add("q", "*:*");
        params.add("fq", queryString);

        try
        {
            QueryResponse response = geodataCore.query(params);
            entries = response.getBeans(BorderData.class);
        }
        catch(Exception ex)
        {
            logger.error("Error in doBorderQuery for argument " + lonLat + " " + ex.getMessage());
        }

        return entries;
    }

    public List<GeoNamesEntry> geoNamesWithinDistanceOf(String lonLat, String distanceInKm)
    {
        List<GeoNamesEntry> entries = new ArrayList<>();

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.add("q", "*:*");
        params.add("fq", "{!geofilt sfield=lonLat}");
        params.add("pt", lonLat);
        params.add("d", distanceInKm);
        params.add("rows", config.getNumProximalHits().toString());

        try
        {
            QueryResponse response = geonamesCore.query(params);
            entries = response.getBeans(GeoNamesEntry.class);
        }
        catch(Exception ex)
        {
            logger.error("Error in geoNamesWithinDistance for argument " + lonLat + ", " + distanceInKm + " " + ex.getMessage());
        }

        return entries;
    }

}

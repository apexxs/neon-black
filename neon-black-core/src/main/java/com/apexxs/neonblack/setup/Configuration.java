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
 * Configuration.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.setup;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Properties;


public class Configuration
{
    private String stanfordModel;
    private String solrURL;
    private Integer maxHits;
    private Boolean sortOnPopulation;
    private Boolean removeStopWords;
    private Boolean removeDemonyms;
    private Boolean replaceSynonyms;
    private String polygonType;
    private String polygonDirectory;
    private String proximalDistance;
    private Integer numProximalHits;
    private Integer numAlternates;
    private String resultFormat;

    private String resourceDirectory;

    private static Configuration instance = null;


    private Configuration()
    {
        Properties configFile = new Properties();
        try
        {
            configFile.load(Configuration.class.getClassLoader().getResourceAsStream("NB.properties"));

            this.stanfordModel = configFile.getProperty("ner.detector.stanford.model");
            this.maxHits = Integer.parseInt(configFile.getProperty("solr.search.maxHits"));
            this.sortOnPopulation = Boolean.parseBoolean(configFile.getProperty("solr.populationSort"));
            this.removeStopWords = Boolean.parseBoolean(configFile.getProperty("ner.detector.removeStopWords"));
            this.removeDemonyms = Boolean.parseBoolean(configFile.getProperty("ner.detector.removeDemonyms"));
            this.replaceSynonyms = Boolean.parseBoolean(configFile.getProperty("ner.detector.replaceSynonyms"));
            this.polygonType = configFile.getProperty("polygon.type");
            this.polygonDirectory = configFile.getProperty("polygon.directory");
            if (!StringUtils.endsWith(this.polygonDirectory, "/"))
            {
                this.polygonDirectory += "/";
            }
            this.solrURL = configFile.getProperty("solr.url");
            if (!StringUtils.endsWith(this.solrURL, "/"))
            {
                this.solrURL += "/";
            }
            this.proximalDistance = configFile.getProperty("coords.proximalDistanceKM");
            this.numProximalHits = Integer.parseInt(configFile.getProperty("coords.numProximalHits"));
            this.numAlternates = Integer.parseInt(configFile.getProperty("results.numAlternates"));
            this.resultFormat = configFile.getProperty("results.format");

            this.resourceDirectory = configFile.getProperty("resource.directory");
            if (!StringUtils.endsWith(this.resourceDirectory, "/"))
            {
                this.resourceDirectory += "/";
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance()
    {
        if (instance == null)
        {
            instance = new Configuration();
        }
        return instance;
    }

    public String getStanfordModel()
    {
        return stanfordModel;
    }

    public String getSolrURL()
    {
        return solrURL;
    }

    public Integer getMaxHits()
    {
        return maxHits;
    }

    public Boolean populationSort()
    {
        return sortOnPopulation;
    }

    public Boolean removeStopWords()
    {
        return removeStopWords;
    }

    public Boolean removeDemonyms()
    {
        return removeDemonyms;
    }

    public Boolean replaceSynonyms()
    {
        return replaceSynonyms;
    }

    public String getPolygonType() { return polygonType;}

    public String getPolygonDirectory() { return polygonDirectory;}

    public String getProximalDistance()
    {
        return proximalDistance;
    }

    public Integer getNumProximalHits()
    {
        return numProximalHits;
    }

    public Integer getNumAlternates()
    {
        return numAlternates;
    }

    public String getResultFormat()
    {
        return resultFormat.toLowerCase();
    }

    public String getResourceDirectory()
    {
        return resourceDirectory.toLowerCase();
    }
}

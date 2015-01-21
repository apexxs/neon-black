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
 * GeoNamesEntry.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.dao;


import com.apexxs.neonblack.utilities.MD5Utilities;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeoNamesEntry
{
    @Field
    public String id;
    @Field
    public String name;
    @Field
    public List<String> allNames;
    @Field
    public String lonLat;
    @Field
    public String featureClass;
    @Field
    public String featureCode;
    @Field
    public String countryCode;
    @Field
    public String admin1;
    @Field
    public String admin2;
    @Field
    public Long population = 0L;
    @Field
    public Float score;



    private Float levenshteinScore;
    private Float levenshteinNormScore;

    public GeoNamesEntry() {}

    public GeoNamesEntry(String line)
    {
        String[] tokens = line.split("\t");

        if(tokens.length < 6) return;

        Set<String> tempNames = new HashSet<>();

        //this.id = tokens[0];
        this.id = MD5Utilities.getMD5Hash(line);
        this.name = tokens[1];
        String asciiName = tokens[2];

        if(!this.name.equals(asciiName))
        {
            tempNames.add(tokens[1]);
            tempNames.add(tokens[2]);
            this.name = asciiName;
        }
        else
        {
            tempNames.add(tokens[1]);
        }

        if(tokens[3].length() > 0)
        {
            String[] altNames = StringUtils.split(tokens[3], ",");
            for(String altName : altNames)
            {
                if(!tempNames.contains(altName))
                {
                    tempNames.add(altName);
                }
            }
        }
        this.allNames = new ArrayList<>(tempNames);
        this.lonLat = tokens[5] + " " + tokens[4];


        if(tokens.length >= 7)
        {
            if (!StringUtils.isBlank(tokens[6]))
            {
                featureClass = tokens[6];
            }
        }
        if(tokens.length >= 8)
        {
            if (!StringUtils.isBlank(tokens[7]))
            {
                featureCode = tokens[7];
            }
        }
        if(tokens.length >= 9)
        {
            if (!StringUtils.isBlank(tokens[8]))
            {
                countryCode = tokens[8];
            }
        }
        if(tokens.length >= 11)
        {
            if (!StringUtils.isBlank(tokens[10]))
            {
                admin1 = tokens[10];
            }
        }
        if(tokens.length >= 12)
        {
            if (!StringUtils.isBlank(tokens[11]))
            {
                admin2 = tokens[11];
            }
        }
        if(tokens.length >= 15)
        {
            try
            {
                if (!StringUtils.isBlank(tokens[14]))
                {
                    population = Long.parseLong(tokens[14]);
                }
            }
            catch (NumberFormatException e)
            {
                //ignore it
            }
        }
    }

    public Float getLevenshteinScore()
    {
        return levenshteinScore;
    }

    public void setLevenshteinScore(Float levenshteinScore)
    {
        this.levenshteinScore = levenshteinScore;
    }

    public Float getLevenshteinNormScore()
    {
        return levenshteinNormScore;
    }

    public void setLevenshteinNormScore(Float levenshteinNormScore)
    {
        this.levenshteinNormScore = levenshteinNormScore;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Gazetteer name: " + this.name + "\t");
        sb.append("LonLat: " + this.lonLat + "\t");
        sb.append("Feature code: " + this.featureCode + "\t");
        sb.append("Country code: " + this.countryCode + "\t");
        sb.append("Admin1: " + this.admin1 + "\t");
        sb.append("Admin2: " + this.admin2 + " ");

        return sb.toString();
    }
}

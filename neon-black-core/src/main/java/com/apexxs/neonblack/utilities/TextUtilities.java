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

package com.apexxs.neonblack.utilities;


import com.apexxs.neonblack.setup.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextUtilities
{
    public final static Logger logger = LoggerFactory.getLogger(TextUtilities.class);
    private List<String> stopWords;
    private CaseInsensitiveList demonyms;
    private Map<String, String> synonymMap;

    private Configuration config = Configuration.getInstance();

    public TextUtilities()
    {
        this.stopWords = getStopwords();
        this.demonyms = getDemonyms();
        this.synonymMap = getSynonymMap();
    }

    public String cleanStopwords(String text)
    {
        String cleanText = text;
        try
        {
            for(String stopWord : stopWords)
            {
                String repl = StringUtils.repeat("-", stopWord.length());
                cleanText = cleanText.replace(stopWord, repl);
            }
        }
        catch(Exception ex)
        {
            logger.error("Error in cleanStopwords: " + ex.getMessage());
        }
        return cleanText;
    }

    private List<String> getStopwords()
    {
        this.stopWords = new ArrayList<>();
        File f;
        try
        {
            f = new File(config.getResourceDirectory() + "Stopwords.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            while ((line = r.readLine()) != null)
            {
                stopWords.add(line.trim());
            }
            r.close();
        }
        catch(Exception ex)
        {
            logger.error("Error in getStopwords: " + ex.getMessage());
        }

        return stopWords;
    }

    public Boolean isStopword(String word)
    {
        return stopWords.contains(word);
    }

    private CaseInsensitiveList getDemonyms()
    {
        CaseInsensitiveList demonyms = new CaseInsensitiveList();

        try
        {
            File file = new File(config.getResourceDirectory() + "/Demonyms.txt");

            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            while ((line = r.readLine()) != null)
            {
                demonyms.add(line);
            }
            r.close();
        }
        catch(Exception ex)
        {
            logger.error("Error in getDemonyms: " + ex.getMessage());
        }

        return demonyms;
    }

    public Boolean isDemonym(String word)
    {
        return demonyms.contains(word);
    }

    public String getSynonym(String text)
    {
        String synonym = StringUtils.EMPTY;

        if(synonymMap.containsKey(text))
        {
            synonym = synonymMap.get(text);
        }

        return synonym;
    }

    private Map<String, String> getSynonymMap()
    {
        Map<String, String> synonymMap = new HashMap<>();
        String line;
        try
        {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File(config.getResourceDirectory() + "/Synonyms.txt")), "UTF-8"));
            while ((line = r.readLine()) != null)
            {
                String[] kvp = line.split(":");
                synonymMap.put(kvp[0], kvp[1]);
            }
        }
        catch(Exception ex)
        {
            logger.error("Error in getSynonymMap: " + ex.getMessage());
        }
        return synonymMap;
    }
}

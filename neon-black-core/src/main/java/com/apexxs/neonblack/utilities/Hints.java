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
import java.util.HashMap;
import java.util.Map;

public class Hints
{
    public final static Logger logger = LoggerFactory.getLogger(Hints.class);

    Map<String, String> hintMap = new HashMap<>();

    private Configuration config = Configuration.getInstance();

    public Hints()
    {
        try
        {
            File f = new File(config.getResourceDirectory() + "/Hints.txt");

            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            String line;

            while ((line = r.readLine()) != null)
            {
                if(!StringUtils.startsWith(line, "#"))
                {
                    String[] tokens = line.split(";");
                    hintMap.put(tokens[0], tokens[1]);
                }
            }
        }
        catch(Exception ex)
        {
            logger.error("Error loading hints file: " + ex.getMessage());
        }
    }

    public String containsHint(String name)
    {
        try
        {
            for (Map.Entry<String, String> entry : hintMap.entrySet())
            {
                if (StringUtils.containsIgnoreCase(name, entry.getKey()))
                {
                    return entry.getValue();
                }
            }
        }
        catch(Exception ex)
        {
            //Let it go, return an empty String
        }
        return StringUtils.EMPTY;
    }
}

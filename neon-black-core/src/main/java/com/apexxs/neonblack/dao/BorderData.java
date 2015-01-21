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
 * BorderData.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.dao;


import org.apache.solr.client.solrj.beans.Field;

public class BorderData
{
    @Field
    public String id;
    @Field
    public String shapeType;
    @Field
    public String fips;
    @Field
    public String iso2;
    @Field
    public String iso3;
    @Field
    public String name;
    @Field("shape")
    public String wkt;
    @Field("centerPoint")
    public String lonLat;

    private String lon;
    private String lat;

    public BorderData() {}

}

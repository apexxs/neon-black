package com.apexxs.neonblack.api.rest;

import com.apexxs.neonblack.NeonBlack;
import com.apexxs.neonblack.dao.FinalLocation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NeonBlackRestController {

    private NeonBlack neonBlack = new NeonBlack();

    @RequestMapping(value = "/extract", method = RequestMethod.GET)
    public @ResponseBody
    List<FinalLocation> getLocations_GET(
            @RequestParam(value="input", required=true, defaultValue="") String input) {
        return getLocations(input);
    }

    @RequestMapping(value = "/extract", method = RequestMethod.POST)
    public @ResponseBody
    List<FinalLocation> getLocations_POST(
            @RequestBody(required=true) String input) {
        return getLocations(input);
    }

    private List<FinalLocation> getLocations(String input) {
        return neonBlack.detectLocationsFromText(input);
    }

}

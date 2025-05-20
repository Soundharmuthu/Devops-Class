package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.GetallBucketsService;

@RestController
@RequestMapping("/buckets")
public class Getallbuckets {

    @Autowired
    private GetallBucketsService bucketsService;

    @GetMapping("/allbucketList")
    public Object getbucketallList() {
        System.out.println("Entry controller");
        return bucketsService.getbucketallList();
    }

    @GetMapping("/callsign")
    public Object getQueryCallsign(@RequestBody Map<String, String> bucketname) {
        String bucket = bucketname.get("bucketname");
        return bucketsService.getQueryCallsign(bucket);
    }

    @GetMapping("/contenttype")
    public Object getQueryContenttype(@RequestBody Map<String, String> bucketname) {
        String bucket = bucketname.get("bucketname");
        return bucketsService.getQueryContenttype(bucket);
    }

    @GetMapping("/purchasetype")
    public Object getQueryPurchasetype(@RequestBody Map<String, String> bucketname) {
        String bucket = bucketname.get("bucketname");
        return bucketsService.getQueryPurchasetype(bucket);
    }

}

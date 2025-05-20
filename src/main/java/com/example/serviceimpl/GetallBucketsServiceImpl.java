package com.example.serviceimpl;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.example.ModelClass.DataDTO;
import com.example.service.GetallBucketsService;

@Service
public class GetallBucketsServiceImpl implements GetallBucketsService {

    @Autowired
    private Cluster cluster;

    @Override
    public List<String> getbucketallList() {

        System.out.println("Entry service impl");
        List<String> buckets = cluster.buckets().getAllBuckets().values().stream()
                .map(BucketSettings::name)
                .collect(Collectors.toList());

        return buckets;
    }

    @Override
    public List<DataDTO> getQueryCallsign(String bucket) {
        String query = String.format(
                """
                        SELECT
                          l2v_cs AS channel,
                          SUM(CASE WHEN cty = "movie" AND st = "draft" THEN 1 ELSE 0 END) AS movieDraft,
                          SUM(CASE WHEN cty = "movie" AND st = "published" THEN 1 ELSE 0 END) AS moviePublished,
                          SUM(CASE WHEN cty = "movie" AND st IN ["draft", "published"] THEN 1 ELSE 0 END) AS movieTotal,
                          SUM(CASE WHEN cty = "tvepisode" AND st = "draft" THEN 1 ELSE 0 END) AS tvepisodeDraft,
                          SUM(CASE WHEN cty = "tvepisode" AND st = "published" THEN 1 ELSE 0 END) AS tvepisodePublished,
                          SUM(CASE WHEN cty = "tvepisode" AND st IN ["draft", "published"] THEN 1 ELSE 0 END) AS tvepisodeTotal,
                          SUM(CASE WHEN cty = "tvseason" AND st = "draft" THEN 1 ELSE 0 END) AS tvseasonDraft,
                          SUM(CASE WHEN cty = "tvseason" AND st = "published" THEN 1 ELSE 0 END) AS tvseasonPublished,
                          SUM(CASE WHEN cty = "tvseason" AND st IN ["draft", "published"] THEN 1 ELSE 0 END) AS tvseasonTotal,
                          SUM(CASE WHEN cty = "tvseries" AND st = "draft" THEN 1 ELSE 0 END) AS tvseriesDraft,
                          SUM(CASE WHEN cty = "tvseries" AND st = "published" THEN 1 ELSE 0 END) AS tvseriesPublished,
                          SUM(CASE WHEN cty = "tvseries" AND st IN ["draft", "published"] THEN 1 ELSE 0 END) AS tvseriesTotal,
                          SUM(CASE WHEN cty IN ["movie", "tvepisode", "tvseason", "tvseries"] AND st IN ["draft", "published"] THEN 1 ELSE 0 END) AS grandTotal
                        FROM `%s`
                        WHERE
                          cty IN ["movie", "tvseries", "tvseason", "tvepisode"]
                          AND pt = "SVOD"
                          AND pn = "unifitv"
                        GROUP BY l2v_cs
                        ORDER BY l2v_cs;
                        """,
                bucket);

        try {
            QueryResult result = cluster.query(query,
                    QueryOptions.queryOptions().timeout(Duration.ofSeconds(300)));

            // ✅ Map to DTO
            return result.rowsAs(DataDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Query failed");
        }
    }

    @Override
    public List<DataDTO> getQueryContenttype(String bucket) {
        String query = String.format(
                """
                        SELECT
                          cty,
                          SUM(CASE WHEN st = 'draft' THEN 1 ELSE 0 END) AS totalDraft,
                          SUM(CASE WHEN st = 'published' THEN 1 ELSE 0 END) AS totalPublished
                        FROM `%s`
                        WHERE cty IN ['movie', 'tvseries', 'tvseason', 'tvepisode']
                          AND pn = 'unifitv'
                        GROUP BY cty
                        ORDER BY cty;
                        """,
                bucket);
        try {
            QueryResult result = cluster.query(query, QueryOptions.queryOptions()
                    .timeout(Duration.ofSeconds(300)));
            List<DataDTO> dtos = result.rowsAs(DataDTO.class);

            return dtos;

        } catch (Exception e) {
            throw new RuntimeException("Query failed");
        }
    }

    @Override
    public List<DataDTO> getQueryPurchasetype(String bucket) {
        String query = String.format(
                """
                        SELECT
                            purchaseType AS channel,
                            cty,
                            COUNT(CASE WHEN st = "draft" THEN 1 END) AS totalDraft,
                            COUNT(CASE WHEN st = "published" THEN 1 END) AS totalPublished,
                            COUNT(CASE WHEN st IN ["draft", "published"] THEN 1 END) AS grandTotal
                        FROM (
                            SELECT
                                CASE
                                    WHEN l2v = "true" AND pt = "SVOD" THEN "Catch up"
                                    WHEN l2v = "false" AND pt = "SVOD" THEN "SVOD"
                                    WHEN l2v = "false" AND pt = "TVOD" AND cty = "movie" THEN "TVOD"
                                    ELSE "Other"
                                END AS purchaseType,
                                cty,
                                st
                            FROM `%s`
                            WHERE cty IN ["movie", "tvseries", "tvseason", "tvepisode"]
                              AND pn = "unifitv"
                        ) AS sub
                        WHERE purchaseType != "Other"
                        GROUP BY purchaseType, cty
                        ORDER BY purchaseType, cty;
                        """,
                bucket);

        try {
            QueryResult result = cluster.query(query,
                    QueryOptions.queryOptions().timeout(Duration.ofSeconds(300)));

            return result.rowsAs(DataDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Query failed");
        }
    }
}

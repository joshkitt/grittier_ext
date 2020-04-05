package com.maxdemarzi.timeline;

import com.maxdemarzi.Schema;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.rule.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.ArrayList;
import java.util.HashMap;

public class GetTimelineTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture(FIXTURE)
            .withUnmanagedExtension("/v1", Timeline.class)
            .withUnmanagedExtension("/v1", Schema.class);

    @Test
    public void shouldGetTimeline() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/timeline").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTimelineLimited() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/timeline?limit=1").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertTrue(actual.size() == 1);
        Assert.assertEquals(expected.get(0), actual.get(0));
    }

    @Test
    public void shouldGetTimelineSince() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/timeline?since=1490140300").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertTrue(actual.size() == 1);
        Assert.assertEquals(unReposted, actual.get(0));
    }

    private static final String FIXTURE =
            "CREATE (max:User {username:'maxdemarzi', " +
                    "email: 'max@neo4j.com', " +
                    "hash: 'hash', " +
                    "name: 'Max De Marzi'," +
                    "time: 1490054400," +
                    "password: 'swordfish'})" +
            "CREATE (jexp:User {username:'jexp', " +
                    "email: 'michael@neo4j.com', " +
                    "hash: 'hash', " +
                    "time: 1490054400," +
                    "name: 'Michael Hunger'," +
                    "password: 'tunafish'})" +
            "CREATE (laeg:User {username:'laexample', " +
                    "email: 'luke@neo4j.com', " +
                    "hash: 'hash', " +
                    "time: 1490054400," +
                    "name: 'Luke Gannon'," +
                    "password: 'cuddlefish'})" +
            "CREATE (max)-[:FOLLOWS]->(jexp)" +
            "CREATE (max)-[:FOLLOWS]->(laeg)" +
            "CREATE (post1:Post {status:'Hello World!', " +
                    "time: 1490140299})" +
            "CREATE (post2:Post {status:'How are you!', " +
                    "time: 1490208700})" +
            "CREATE (post3:Post {status:'Doing fine!', " +
                    "time: 1490208800})" +
            "CREATE (jexp)-[:POSTED_ON_2017_03_21 {time: 1490140299}]->(post1)" +
            "CREATE (laeg)-[:POSTED_ON_2017_03_22 {time: 1490208700}]->(post2)" +
            "CREATE (max)-[:POSTED_ON_2017_03_22 {time: 1490208800}]->(post3)" +
            "CREATE (laeg)-[:LIKES {time:1490143299}]->(post1)" +
            "CREATE (laeg)-[:REPOSTED_ON_2017_03_22 {time:1490208000}]->(post1)" +
            "CREATE (max)-[:LIKES {time: 1490214800}]->(post2)" ;

    private static final ArrayList<HashMap<String, Object>> expected = new ArrayList<HashMap<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("username", "maxdemarzi");
            put("name", "Max De Marzi");
            put("hash", "hash");
            put("status", "Doing fine!");
            put("time", 1490208800);
            put("likes", 0);
            put("reposts", 0);
            put("liked", false);
            put("reposted", false);

        }});
        add(new HashMap<String, Object>() {{
            put("username", "laexample");
            put("name", "Luke Gannon");
            put("hash", "hash");
            put("status", "How are you!");
            put("time", 1490208700);
            put("likes", 1);
            put("reposts", 0);
            put("liked", true);
            put("reposted", false);
        }});
        add(new HashMap<String, Object>() {{
            put("reposter_username", "laexample");
            put("reposter_name", "Luke Gannon");
            put("reposted_time",1490208000);
            put("hash", "hash");
            put("username", "jexp");
            put("name", "Michael Hunger");
            put("status", "Hello World!");
            put("time", 1490140299);
            put("likes", 1);
            put("reposts", 1);
            put("liked", false);
            put("reposted", false);

        }});
    }};

    private static final HashMap<String, Object> unReposted = new HashMap<String, Object>() {{
            put("username", "jexp");
            put("name", "Michael Hunger");
            put("hash", "hash");
            put("status", "Hello World!");
            put("time", 1490140299);
            put("likes", 1);
            put("reposts", 1);
            put("liked", false);
            put("reposted", false);
    }};

}

package com.maxdemarzi.posts;

import com.maxdemarzi.Schema;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.rule.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.ArrayList;
import java.util.HashMap;

public class GetPostsTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture(FIXTURE)
            .withUnmanagedExtension("/v1", Posts.class)
            .withUnmanagedExtension("/v1", Schema.class);

    @Test
    public void shouldGetPosts() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/posts").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetPostsWithUser() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/posts?username2=jexp").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertEquals(expected2, actual);
    }

    @Test
    public void shouldGetPostsLimited() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/posts?limit=1").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertTrue(actual.size() == 1);
        Assert.assertEquals(expected.get(0), actual.get(0));
    }

    @Test
    public void shouldGetPostsSince() {
        HTTP.POST(neo4j.httpURI().resolve("/v1/schema/create").toString());

        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/users/maxdemarzi/posts?since=1490140300").toString());
        ArrayList<HashMap> actual  = response.content();
        Assert.assertTrue(actual.size() == 1);
        Assert.assertEquals(expected.get(1), actual.get(0));
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
                    "name: 'Michael Hunger'," +
                    "hash: '0bd90aeb51d5982062f4f303a62df935'," +
                    "time: 1490054400," +
                    "password: 'tunafish'})" +
            "CREATE (post1:Post {status:'Hello World!', " +
                    "time: 1490140299})" +
            "CREATE (post2:Post {status:'How are you!', " +
                    "time: 1490208700})" +
            "CREATE (max)-[:POSTED_ON_2017_03_21 {time: 1490140299}]->(post1)" +
            "CREATE (jexp)-[:LIKES {time: 1490141300}]->(post1)" +
            "CREATE (max)-[:POSTED_ON_2017_03_22 {time: 1490208700}]->(post2)";

    private static final ArrayList<HashMap<String, Object>> expected = new ArrayList<HashMap<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("status", "How are you!");
            put("time", 1490208700);
            put("name", "Max De Marzi");
            put("username", "maxdemarzi");
            put("hash", "hash");
            put("reposts", 0);
            put("likes", 0);
        }});
        add(new HashMap<String, Object>() {{
            put("status", "Hello World!");
            put("time", 1490140299);
            put("name", "Max De Marzi");
            put("username", "maxdemarzi");
            put("hash", "hash");
            put("reposts", 0);
            put("likes", 1);
        }});
    }};

    private static final ArrayList<HashMap<String, Object>> expected2 = new ArrayList<HashMap<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("status", "How are you!");
            put("time", 1490208700);
            put("name", "Max De Marzi");
            put("username", "maxdemarzi");
            put("hash", "hash");
            put("reposts", 0);
            put("likes", 0);
            put("liked", false);
            put("reposted", false);

        }});
        add(new HashMap<String, Object>() {{
            put("status", "Hello World!");
            put("time", 1490140299);
            put("name", "Max De Marzi");
            put("username", "maxdemarzi");
            put("hash", "hash");
            put("reposts", 0);
            put("likes", 1);
            put("liked", true);
            put("reposted", false);

        }});
    }};
}

package terrastore.search;

import java.util.Properties;
import org.elasticsearch.action.get.GetRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Sergio Bossa
 */
public class ElasticSearchTest {

    private static final String INDEX = "search";
    private static final String BUCKET = "bucket";
    private volatile ElasticSearchServer server;
    private volatile ElasticSearchListener listener;

    @Before
    public void setUp() {
        server = new ElasticSearchServer(new Properties());
        listener = new ElasticSearchListener(server, new FixedIndexNameResolver(INDEX), true, BUCKET);
        listener.init();
    }

    @After
    public void tearDown() {
        listener.cleanup();
    }

    @Test
    public void testOnValueChanged() throws Exception {
        String key = "key";
        String value = "{\"key\":\"value\"}";

        listener.onValueChanged(BUCKET, key, value.getBytes("UTF-8"));
        //
        Thread.sleep(3000);
        //
        assertEquals(value, server.getClient().get(new GetRequest(INDEX, BUCKET, key)).actionGet().source());
    }

    @Test
    public void testOnValueChangedAndRemoved() throws Exception {
        String key = "key";
        String value = "{\"key\":\"value\"}";

        listener.onValueChanged(BUCKET, key, value.getBytes("UTF-8"));
        //
        Thread.sleep(3000);
        //
        assertEquals(value, server.getClient().get(new GetRequest(INDEX, BUCKET, key)).actionGet().source());
        //
        listener.onValueRemoved(BUCKET, key);
        //
        Thread.sleep(3000);
        //
        assertNull(server.getClient().get(new GetRequest(INDEX, BUCKET, key)).actionGet().source());
    }
}
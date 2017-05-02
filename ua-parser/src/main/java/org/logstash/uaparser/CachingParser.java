package org.logstash.uaparser;

import java.io.InputStream;
import java.util.Map;
import org.apache.commons.collections.map.LRUMap;

/**
 * When doing webanalytics (with for example PIG) the main pattern is to process
 * weblogs in clickstreams. A basic fact about common clickstreams is that in
 * general the same browser will do multiple requests in sequence. This has the
 * effect that the same useragent will appear in the logfiles and we will see
 * the need to parse the same useragent over and over again.
 *
 * This class introduces a very simple LRU cache to reduce the number of times
 * the parsing is actually done.
 * @author Niels Basjes
 */
public final class CachingParser extends Parser {

    // TODO: Make configurable
    private static final int CACHE_SIZE = 1000;

    private Map<String, Client> cacheClient;
    private Map<String, UserAgent> cacheUserAgent;
    private Map<String, Device> cacheDevice;
    private Map<String, OS> cacheOS;
    // ------------------------------------------

    public CachingParser() {
        super();
    }

    public CachingParser(InputStream regexYaml) {
        super(regexYaml);
    }
    // ------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Client parse(String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheClient == null) {
            this.cacheClient = new LRUMap(CachingParser.CACHE_SIZE);
        }
        Client client = this.cacheClient.get(agentString);
        if (client != null) {
            return client;
        }
        client = super.parse(agentString);
        this.cacheClient.put(agentString, client);
        return client;
    }
    // ------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public UserAgent parseUserAgent(String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheUserAgent == null) {
            this.cacheUserAgent = new LRUMap(CachingParser.CACHE_SIZE);
        }
        UserAgent userAgent = this.cacheUserAgent.get(agentString);
        if (userAgent != null) {
            return userAgent;
        }
        userAgent = super.parseUserAgent(agentString);
        this.cacheUserAgent.put(agentString, userAgent);
        return userAgent;
    }
    // ------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Device parseDevice(String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheDevice == null) {
            this.cacheDevice = new LRUMap(CachingParser.CACHE_SIZE);
        }
        Device device = this.cacheDevice.get(agentString);
        if (device != null) {
            return device;
        }
        device = super.parseDevice(agentString);
        this.cacheDevice.put(agentString, device);
        return device;
    }
    // ------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public OS parseOS(String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheOS == null) {
            this.cacheOS = new LRUMap(CachingParser.CACHE_SIZE);
        }
        OS os = this.cacheOS.get(agentString);
        if (os != null) {
            return os;
        }
        os = super.parseOS(agentString);
        this.cacheOS.put(agentString, os);
        return os;
    }
    // ------------------------------------------

}

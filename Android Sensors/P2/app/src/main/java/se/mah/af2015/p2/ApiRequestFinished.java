package se.mah.af2015.p2;

/**
 * ApiRequestFinished provides and interface for handling retrieved data from the Weather API request
 *
 * @author Alexander Johansson (AF2015).
 */

public interface ApiRequestFinished {
    /**
     * Method should be forwarded with the desired output response
     * so classes listening can handle the response.
     * @param output String
     */
    void requestFinished(String output);
}

package se.mah.af2015.p2;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * WeatherApiRequest makes a request to OpenWeatherMap API to retrieve data about given location in latitude and longitude
 *
 * @author Alexander Johansson (AF2015).
 */
public class WeatherApiRequest extends AsyncTask<Object, Void, String> {
    // Application context
    private Context context;
    // Delegate for handling results
    private ApiRequestFinished delegate = null;

    /**
     * Constructor for WeatherApiRequest
     * @param delegate ApiRequestFinished
     */
    public WeatherApiRequest(Context context, ApiRequestFinished delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        // Make Toast on UI thread
        Toast.makeText(context, context.getResources().getText(R.string.could_not_connect), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            // Input parameters
            String apiUrl = (String) params[0];
            String apiKey = (String) params[1];
            double latitude = (Double) params[2];
            double longitude = (Double) params[3];

            // Format url, open connection
            String formattedUrl = String.format(apiUrl, latitude, longitude, apiKey);
            URL url = new URL(formattedUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                // Read from connection
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                // Initialise string builder
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                // Read from source
                while((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                // Close reader and return result
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                // Disconnect
                urlConnection.disconnect();
            }
        } catch(Exception e) {
            // Likely a disconnection
            publishProgress();
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // Forward result to delegate
        delegate.requestFinished(result);
    }
}

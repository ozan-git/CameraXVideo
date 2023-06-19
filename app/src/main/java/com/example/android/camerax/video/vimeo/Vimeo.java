package com.example.android.camerax.video.vimeo;

import android.util.Log;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Vimeo {
    private static final String UTF_8 = "UTF-8";
    private static final String VIMEO_VERSION = "3.4";
    private static final String VIMEO_SERVER = "https://api.vimeo.com";
    private final String token;
    private final String tokenType;
    private URL proxy;

    public Vimeo(String token) {
        this(token, "bearer");
    }

    public Vimeo(String token, String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
    }

    public URL getProxy() {
        return proxy;
    }

    public void setProxy(URL proxy) {
        this.proxy = proxy;
    }

    public VimeoResponse getVideoInfo(String endpoint) throws IOException, JSONException {
        return get(endpoint);
    }

    public VimeoResponse get(String endpoint) throws IOException {
        return get(endpoint, null, null);
    }

    public VimeoResponse get(String endpoint, Object params, Map<String, String> headers) throws IOException {
        return apiRequest(endpoint, HttpGet.METHOD_NAME, params, headers);
    }

    public VimeoResponse post(String endpoint) throws IOException {
        return post(endpoint, null, null);
    }

    public VimeoResponse post(String endpoint, Object params, Map<String, String> headers) throws IOException {
        return apiRequest(endpoint, HttpPost.METHOD_NAME, params, headers);
    }

    public VimeoResponse put(String endpoint) throws IOException, JSONException {
        return put(endpoint, null, null);
    }

    public VimeoResponse put(String endpoint, Object params, Map<String, String> headers) throws IOException {
        return apiRequest(endpoint, HttpPut.METHOD_NAME, params, headers);
    }

    public VimeoResponse delete(String endpoint) throws IOException, JSONException {
        return delete(endpoint, null, null);
    }

    public VimeoResponse delete(String endpoint, Object params, Map<String, String> headers) throws IOException {
        return apiRequest(endpoint, HttpDelete.METHOD_NAME, params, headers);
    }

    public VimeoResponse patch(String endpoint) throws IOException, JSONException {
        return patch(endpoint, null, null);
    }

    public VimeoResponse patch(String endpoint, Object params, Map<String, String> headers) throws IOException {
        return apiRequest(endpoint, HttpPatch.METHOD_NAME, params, headers);
    }

    public VimeoResponse head(String endpoint) throws IOException, JSONException {
        return head(endpoint, null, null);
    }

    public VimeoResponse head(String endpoint, Object params, Map<String, String> headers) throws IOException {
        return apiRequest(endpoint, HttpHead.METHOD_NAME, params, headers);
    }

    public VimeoResponse updateVideoMetadata(String videoEndpoint, String name, String description, String license, String privacyView, String privacyEmbed, boolean reviewLink) throws IOException, JSONException {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("description", description);
        params.put("license", license);
        params.put("privacy.view", privacyView);
        params.put("privacy.embed", privacyEmbed);
        params.put("review_page.active", reviewLink ? "true" : "false");
        return patch(videoEndpoint, params, null);
    }

    public VimeoResponse addVideoPrivacyDomain(String videoEndpoint, String domain) throws IOException, JSONException {
        domain = URLEncoder.encode(domain, UTF_8);
        return put(videoEndpoint + "/privacy/domains/" + domain);
    }

    public VimeoResponse getVideoPrivacyDomains(String videoEndpoint) throws IOException, JSONException {
        return get(videoEndpoint + "/privacy/domains");
    }

    public VimeoResponse removeVideo(String videoEndpoint) throws IOException, JSONException {
        return delete(videoEndpoint);
    }

    public VimeoResponse setVideoThumb(String videoEndpoint, float time, boolean active) throws IOException, JSONException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("time", Float.toString(time));
        params.put("active", Boolean.toString(active));
        return post(videoEndpoint + "/pictures", params, null);
    }

    public VimeoResponse getMe() throws IOException, JSONException {
        return get("/me");
    }

    public VimeoResponse getVideos() throws IOException, JSONException {
        return get("/me/videos");
    }

    public VimeoResponse searchVideos(String query) throws IOException, JSONException {
        return searchVideos(query, null, null);
    }

    public VimeoResponse searchVideos(String query, String pageNumber, String itemsPerPage) throws IOException, JSONException {
        Map<String, String> params = new HashMap<>();
        params.put("query", query);
        params.put("page", pageNumber);
        params.put("per_page", itemsPerPage);
        return searchVideos(params);
    }

    public VimeoResponse searchVideos(Map<String, String> params) throws IOException, JSONException {
        return get("/videos", params, null);
    }

    public String addVideo(File file) throws IOException, VimeoException, JSONException {
        return addVideo(Files.newInputStream(file.toPath()), file.length(), null, null);
    }

    public String addVideo(File file, String name, Map<String, String> privacy) throws IOException, VimeoException, JSONException {
        return addVideo(Files.newInputStream(file.toPath()), file.length(), name, privacy);
    }

    public String addVideo(byte[] bytes, long fileSize) throws IOException, VimeoException, JSONException {
        return addVideo(new ByteArrayInputStream(bytes), fileSize, null, null);
    }

    public String addVideo(byte[] bytes, long fileSize, String name, Map<String, String> privacy) throws IOException, VimeoException, JSONException {
        return addVideo(new ByteArrayInputStream(bytes), fileSize, name, privacy);
    }

    public String addVideo(InputStream inputStream, long fileSize, String name, Map<String, String> privacy) throws IOException, VimeoException, JSONException {
        VimeoResponse response = beginUploadVideo(fileSize, name, privacy);
        if (response.getStatusCode() == 200) {
            JSONObject upload = response.getJson().getJSONObject("upload");
            if ("tus".equalsIgnoreCase(upload.getString("approach"))) {
                uploadVideo(upload.getString("upload_link"), inputStream);
                return response.getJson().getString("uri");
            }
        }
        throw new VimeoException("HTTP Status Code: " + response.getStatusCode());
    }

    public VimeoResponse beginUploadVideo(long fileSize, String name, Map<String, String> privacy) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("upload.approach", "tus");
        params.put("upload.size", fileSize + "");
        if (name != null) {
            params.put("name", name);
        }
        if (privacy != null) {
            for (String key : privacy.keySet()) {
                params.put("privacy." + key, privacy.get(key));
            }
        }
        return post("/me/videos", params, null);
    }

    public VimeoResponse uploadVideo(String uploadLink, byte[] bytes) throws IOException {
        return uploadVideo(uploadLink, new ByteArrayInputStream(bytes));
    }

    public VimeoResponse uploadVideo(String uploadLink, File file) throws IOException {
        return uploadVideo(uploadLink, Files.newInputStream(file.toPath()));
    }

    public VimeoResponse uploadVideo(String uploadLink, InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Tus-Resumable", "1.0.0");
        headers.put("Upload-Offset", "0");
        headers.put("Content-Type", "application/offset+octet-stream");
        return patch(uploadLink, inputStream, headers);
    }

    public VimeoResponse uploadVerify(String uploadLink) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Tus-Resumable", "1.0.0");
        return head(uploadLink, null, headers);
    }

    public VimeoResponse likesVideo(String videoId) throws IOException {
        return get("/me/likes/" + videoId);
    }

    public VimeoResponse likeVideo(String videoId) throws IOException, JSONException {
        return put("/me/likes/" + videoId);
    }

    public VimeoResponse unlikeVideo(String videoId) throws IOException, JSONException {
        return delete("/me/likes/" + videoId);
    }

    public VimeoResponse checkEmbedPreset(String videoEndPoint, String presetId) throws IOException {
        return get(videoEndPoint + "/presets/" + presetId);
    }

    public VimeoResponse addEmbedPreset(String videoEndPoint, String presetId) throws IOException, JSONException {
        return put(videoEndPoint + "/presets/" + presetId);
    }

    public VimeoResponse removeEmbedPreset(String videoEndPoint, String presetId) throws IOException, JSONException {
        return delete(videoEndPoint + "/presets/" + presetId);
    }

    public VimeoResponse getTextTracks(String videoEndPoint) throws IOException, JSONException {
        return get(videoEndPoint + "/texttracks");
    }

    public VimeoResponse getTextTrack(String videoEndPoint, String textTrackId) throws IOException, JSONException {
        return get(videoEndPoint + "/texttracks/" + textTrackId);
    }

    public String addTextTrack(String videoEndPoint, File file, boolean active, String type, String language, String name) throws IOException, VimeoException, JSONException {
        return addTextTrack(videoEndPoint, Files.newInputStream(file.toPath()), active, type, language, name);
    }

    public String addTextTrack(String videoEndPoint, byte[] bytes, boolean active, String type, String language, String name) throws IOException, VimeoException, JSONException {
        return addTextTrack(videoEndPoint, new ByteArrayInputStream(bytes), active, type, language, name);
    }

    public String addTextTrack(String videoEndPoint, InputStream inputStream, boolean active, String type, String language, String name) throws IOException, VimeoException, JSONException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("active", active ? "true" : "false");
        params.put("type", type);
        params.put("language", language);
        params.put("name", name);

        VimeoResponse addVideoRespose = post(videoEndPoint + "/texttracks", params, null);
        VimeoResponse response = null;
        if (addVideoRespose.getStatusCode() == 201) {
            String textTrackUploadLink = addVideoRespose.getJson().getString("link");
            response = apiRequest(textTrackUploadLink, HttpPut.METHOD_NAME, inputStream, null);
            if (response.getStatusCode() == 200) {
                return addVideoRespose.getJson().getString("uri");
            }
        }
        throw new VimeoException("HTTP Status Code: " + Objects.requireNonNull(response).getStatusCode());
    }

    public VimeoResponse updateTextTrack(String videoEndPoint, String textTrackUri, boolean active, String type, String language, String name) throws IOException, JSONException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("active", active ? "true" : "false");
        params.put("type", type);
        params.put("language", language);
        params.put("name", name);
        return patch(videoEndPoint + textTrackUri, params, null);
    }

    public VimeoResponse removeTextTrack(String videoEndPoint, String textTrackId) throws IOException, JSONException {
        return delete(videoEndPoint + "/texttracks/" + textTrackId);
    }

    protected VimeoResponse apiRequest(String endpoint, String methodName, Object params, Map<String, String> headers) throws IOException {
        URL url;
        if (endpoint.startsWith("http")) url = new URL(endpoint);
        else url = new URL(VIMEO_SERVER + endpoint);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(methodName);

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        }

        connection.setRequestProperty("Accept", "application/vnd.vimeo.*+json;version=" + VIMEO_VERSION);
        connection.setRequestProperty("Authorization", tokenType + ' ' + token);

        if (params != null) {
            if (params instanceof JSONObject) {
                JSONObject jsonParams = (JSONObject) params;
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(jsonParams.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.close();

            } else if (params instanceof File) {
                File file = (File) params;
                String boundary = "Boundary-" + UUID.randomUUID().toString();
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
                writer.append("\r\n");
                writer.flush();
                Files.copy(file.toPath(), outputStream);
                outputStream.flush();
                writer.append("\r\n");
                writer.flush();
                writer.append("--").append(boundary).append("--").append("\r\n");
                writer.close();

            } else if (params instanceof InputStream) {
                InputStream inputStream = (InputStream) params;
                connection.setRequestProperty("Content-Type", "application/offset+octet-stream");
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
            } else {
                Map<String, String> map = (Map<String, String>) params;
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(postDataBytes);
                outputStream.close();
            }
        }

        // Applying proxy to the connection
        if (proxy != null) {
            Proxy proxyInstance = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
            connection = (HttpURLConnection) url.openConnection(proxyInstance);
        }

        int statusCode = connection.getResponseCode();

        JSONObject responseJson;
        JSONObject responseHeaders;
        String responseAsString = null;

        if (statusCode != HttpURLConnection.HTTP_NO_CONTENT) {
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                inputStream = connection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                responseAsString = outputStream.toString(StandardCharsets.UTF_8.name());
            } catch (IOException e) {
                // Handle the exception or print the error message
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        // Handle the exception or print the error message
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        // Handle the exception or print the error message
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            responseJson = new JSONObject(Objects.requireNonNull(responseAsString));
            responseHeaders = new JSONObject();
            Map<String, List<String>> headerFields = connection.getHeaderFields();

            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                String headerName = entry.getKey();
                List<String> headerValues = entry.getValue();
                if (headerName != null && headerValues != null && !headerValues.isEmpty()) {
                    responseHeaders.put(headerName, headerValues.get(0));
                }
            }
        } catch (JSONException e) {
            responseJson = new JSONObject();
            responseHeaders = new JSONObject();
        }


        VimeoResponse vimeoResponse = new VimeoResponse(responseJson, responseHeaders, statusCode);
        connection.disconnect();
        return vimeoResponse;
    }
}
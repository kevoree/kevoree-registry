package org.kevoree.registry.server.oauth.google;

import com.eclipsesource.json.JsonObject;
import io.undertow.util.Headers;
import io.undertow.util.QueryParameterUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.kevoree.registry.server.model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

/**
 * Created by leiko on 19/11/14.
 */
public class GoogleOAuth2Manager {

    public static String authEndPoint(Auth auth) {
        Map<String, Deque<String>> queryParams = new HashMap<String, Deque<String>>();

        Deque<String> responseType = new ArrayDeque<String>();
        responseType.add(auth.responseType);
        queryParams.put("response_type", responseType);

        Deque<String> clientID = new ArrayDeque<String>();
        clientID.add(auth.clientId);
        queryParams.put("client_id", clientID);

        Deque<String> state = new ArrayDeque<String>();
        state.add(auth.state);
        queryParams.put("state", state);

        Deque<String> redirectURI = new ArrayDeque<String>();
        redirectURI.add(auth.redirectURI);
        queryParams.put("redirect_uri", redirectURI);

        Deque<String> scope = new ArrayDeque<String>();
        scope.add(auth.scope);
        queryParams.put("scope", scope);

        return "https://accounts.google.com/o/oauth2/auth?" + QueryParameterUtils.buildQueryString(queryParams);
    }

    public static AuthAccess getAuthAccess(String code, Auth auth) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost();
            post.setURI(URI.create("https://accounts.google.com/o/oauth2/token"));
            post.setHeader(Headers.USER_AGENT_STRING, "Kevoree/registry");
            post.setHeader(Headers.CONTENT_TYPE_STRING, "application/x-www-form-urlencoded");

            StringBuilder postData = new StringBuilder();
            postData.append("code=").append(code);
            postData.append("&");
            postData.append("client_id=").append(auth.clientId);
            postData.append("&");
            postData.append("client_secret=").append(auth.clientSecret);
            postData.append("&");
            postData.append("redirect_uri=").append(auth.redirectURI);
            postData.append("&");
            postData.append("grant_type=authorization_code");

            post.setEntity(new StringEntity(postData.toString()));

            CloseableHttpResponse response = client.execute(post);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                JsonObject responseObj = JsonObject.readFrom(reader);
                reader.close();

                if (responseObj.get("error") != null) {
                    // something went wrong while retrieving token
                    String error = responseObj.get("error").toString();
                    throw new GoogleOAuth2Exception(error);

                } else {
                    // alright
                    AuthAccess authAccess = new AuthAccess();
                    authAccess.accessToken = responseObj.get("access_token").asString();
                    authAccess.idToken = responseObj.get("id_token").asString();
                    authAccess.tokenType = responseObj.get("token_type").asString();
                    return authAccess;
                }

            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
    }

    public static User getUserInfo(String code, Auth auth) throws Exception {
        return getUserInfo(getAuthAccess(code, auth));
    }

    public static User getUserInfo(AuthAccess access) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpGet get = new HttpGet();
            get.setURI(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"));
            get.setHeader(Headers.USER_AGENT_STRING, "Kevoree/registry");
            get.setHeader(Headers.AUTHORIZATION_STRING, access.tokenType + " " + access.accessToken);

            CloseableHttpResponse response = client.execute(get);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                JsonObject responseObj = JsonObject.readFrom(reader);
                reader.close();

                if (responseObj.get("error") != null) {
                    // something went wrong while retrieving user.info
                    String error = responseObj.get("error").asString();
                    throw new GoogleOAuth2Exception(error);

                } else {
                    // alright
                    User user = new User();
                    user.setId(responseObj.get("email").asString());
                    user.setGravatarEmail(user.getId());
                    user.setName(responseObj.get("given_name").asString());
                    return user;
                }

            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
    }
}

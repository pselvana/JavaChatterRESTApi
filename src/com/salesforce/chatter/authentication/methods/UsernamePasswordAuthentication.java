/*******************************************************************************
 * Copyright (c) 2013, salesforce.com, inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 * 
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.salesforce.chatter.authentication.methods;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.salesforce.chatter.authentication.AuthenticationException;
import com.salesforce.chatter.authentication.ChatterAuthToken;
import com.salesforce.chatter.authentication.IChatterData;
import com.salesforce.chatter.authentication.UnauthenticatedSessionException;

public class UsernamePasswordAuthentication extends AuthentificationMethod {

    private final IChatterData chatterData;

    public UsernamePasswordAuthentication(IChatterData chatterData) {
        this.chatterData = chatterData;
    }

    /**
     * <p>Authenticate using a username and password. This is discouraged by the oauth flow,
     * but it allows for transparent (and non human-intervention) authentication).</p>
     * 
     * @return The response retrieved from the REST API (usually an XML string with all the tokens)
     * @throws IOException
     * @throws UnauthenticatedSessionException
     * @throws AuthenticationException
     */
    public ChatterAuthToken authenticate() throws IOException, UnauthenticatedSessionException, AuthenticationException {
        String clientId = URLEncoder.encode(chatterData.getClientKey(), "UTF-8");
        String clientSecret = chatterData.getClientSecret();
        String username = chatterData.getUsername();
        String password = chatterData.getPassword();

        String authenticationUrl = "TEST".equalsIgnoreCase(chatterData.getEnvironment()) ? TEST_AUTHENTICATION_URL : PRODUCTION_AUTHENTICATION_URL;
        PostMethod post = new PostMethod(authenticationUrl);

        NameValuePair[] data = { new NameValuePair("grant_type", "password"), new NameValuePair("client_id", clientId),
            new NameValuePair("client_secret", clientSecret), new NameValuePair("username", username),
            new NameValuePair("password", password) };

        post.setRequestBody(data);

        int statusCode = getHttpClient().executeMethod(post);
        if (statusCode == HttpStatus.SC_OK) {
            return processResponse(post.getResponseBodyAsString());
        }

        throw new UnauthenticatedSessionException(statusCode + " " + post.getResponseBodyAsString());
    }

}

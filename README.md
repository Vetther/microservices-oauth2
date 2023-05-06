# Spring Boot 3 Microservices App

User login/registration system with OAuth2/Credentials using JWT + linking accounts system with different providers

## Main goal

Create system, where user can register/login and link his account with different auth providers (Google, Facebook, Credentials etc.) and login using all of them to his account. Additionally, project uses JWT system (access token + refresh token), signed by private keys and verificated by public keys in other microservices on zero-trust architecture.

## Architecture

### Registration

#### Creating account using OAuth2 - Before login
![image](https://user-images.githubusercontent.com/28400410/236621988-d831a02e-073a-48d7-81c2-1c4de1906670.png)

**Explanation of steps:**
<ol>
<li>The user is redirected by the frontend to the appropriate endpoint depending on the selected provider, along with information about the URL to which the user should be redirected after logging in, for example, http://localhost:8080/auth/register/GOOGLE?redirect_url="http://localhost:3000/callback".</li>
<li>The Gateway API redirects the user to the Authorization Server, which selects the appropriate path to the OAuth2 Service.</li>
<li>The OAuth2 Service receives all the necessary variables (redirect_url from the frontend, callback_url on the authorization_server, etc.) and stores them in cookies for later retrieval.</li></li>
<li>The OAuth2 Service redirects the user to the provider's login servers, such as Google servers, for the user to log in.</li>
</ol>

#### Creating account using OAuth2 - After login
![image](https://user-images.githubusercontent.com/28400410/236621877-6235b243-f6cc-4d6b-912b-9d8e0b47b75c.png)

**Explanation of steps:**
<ol>
<li>The provider's login servers redirect the user to the OAuth2 Service along with the retrieved data.</li>
<li>The OAuth2 Service stores the data under a randomly generated secret key in Redis to avoid directly passing them in the URL.</li>
<li>Thanks to the previous step, the URL redirected to the Authorization Server contains the secret key instead of the data.</li>
<li>The Authorization Server receives the secret key and sends a request to the OAuth2 Service for the data, but this time as a regular GET request instead of a redirect.</li>
<li>The OAuth2 Service retrieves the data from Redis and passes it to the Authorization Server in the response body.</li>
<li>The Authorization Server uses its private key to create an access_token and refresh_token. To avoid passing them strictly as variables in the URL, it stores them in Redis under a new secret key, just like before.</li>
<li>The Authorization Server retrieves the redirect_url from the previous OAuth2 Service request, which the user provided at the beginning of the login process (e.g., http://localhost:3000/callback). The newly generated secret key from the previous step is passed along in this URL (e.g., http://localhost:3000/callback?secret_key=[KEY]).</li>
<li>The Gateway API redirects the user to the URL along with the key.</li>
</ol>

#### Creating account using OAuth2 - Grabbing JWT
![image](https://user-images.githubusercontent.com/28400410/236621974-b3c91320-e68b-4914-946e-56f25a21cbf4.png)

**Explanation of steps:**
<ol>
<li>The user sends a GET request using the previously obtained secret key from the URL in the request body to the appropriate endpoint to fetch the JWT.</li>
<li>The Gateway API redirects the request to the corresponding microservice.</li>
<li>The Authorization Server verifies the authenticity of the secret key by sending a request for data to Redis.</li>
<li>If the key is valid, the Authorization Server receives the previously generated tokens (access_token, refresh_token).</li>
<li>The Authorization Server creates a response with the JWT tokens in the response body.</li>
<li>The response with the tokens in the body is returned to the user.</li>
</ol>

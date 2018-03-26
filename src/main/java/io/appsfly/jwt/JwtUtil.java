package io.appsfly.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.appsfly.util.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JwtUtil {
    public static void main(String[] args) {
        JwtUtil jwtUtil = new JwtUtil();
        String data = new JSONObject() {{
            this.put("find", "srikanth");
        }}.toString();
        System.out.println("data is " + data);
        String mohit = jwtUtil.generateChecksum(data, "123");
        System.out.println("token for the above keys is " + mohit);
        String data1 = new JSONObject() {{
            this.put("find", "srikanth");
        }}.toString();
        String b = jwtUtil.verifyCheckSum(mohit, "123");
        System.out.println("data validation is " + b);
    }

    public String generateChecksum(String data, String secret) {
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            token = JWT.create()
                    .withClaim("af_claim", data)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
            exception.printStackTrace();
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            exception.printStackTrace();
        }
        return token;

    }

    public String verifyCheckSum(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt1 = JWT.decode(token);
            Claim claim = jwt1.getClaim("af_claim");
            return new JSONObject(claim.asMap()).toString();
//            return ((JsonNodeClaim) claim).data.toString();
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
            //UTF-8 encoding not supported
            return null;
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            exception.printStackTrace();
            return null;
        }
    }

}
